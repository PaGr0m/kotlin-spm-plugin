package com.pagrom.kotlin.gradle.spm.tasks

import com.pagrom.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import com.pagrom.kotlin.gradle.spm.swiftPackageBuildDirs
import groovy.text.SimpleTemplateEngine
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.jetbrains.kotlin.konan.target.Family
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.util.*

@Suppress("UnstableApiUsage")
@CacheableTask
abstract class PublishXCFramework : Exec() {
    init {
        description = "Publishing the XCFramework to the git repository"
        group = KotlinSpmPlugin.TASK_GROUP
    }

    private val binaryTargetPackageTemplateContent = this::class.java.getResource("/BinaryPackage.swift")!!.readText()

    @InputFile
    @PathSensitive(PathSensitivity.ABSOLUTE)
    val archiveXCFramework: Property<RegularFile> = project.objects.fileProperty()

    @Input
    val family: Property<Family> = project.objects.property(Family::class.java)

    override fun exec() {
        // Create synthetic Swift Package project if not exist
        var syntheticFamily = false

        val familyDirectory = project.swiftPackageBuildDirs.platformRoot(family.get())
        if (!familyDirectory.exists()) {
            workingDir = familyDirectory
            Files.createDirectories(workingDir.toPath())

            commandLine("swift", "package", "init")
            super.exec()

            syntheticFamily = true
        }

        // Compute checksum
        val archive = archiveXCFramework.get().asFile

        standardOutput = ByteArrayOutputStream()
        workingDir = project.swiftPackageBuildDirs.platformRoot(Family.IOS)
        commandLine(
            "swift", "package", "compute-checksum", archive.absolutePath
        )

        super.exec()
        val checksum = standardOutput.toString()

        // Collect properties
        val properties = Properties().apply {
            load(project.rootProject.file("local.properties").inputStream())
        }

        val gitUrl = properties.getProperty("git.credentials.giturl")
        val archiveUrl = "${gitUrl.removeSuffix(".git")}/blob/master/generated/KotlinLibrary.xcframework.zip"

        // Create Package.swift example file
        val binding = mapOf(
            "CHECK_SUM" to checksum,
            "ARCHIVE_XCFRAMEWORK_LIBRARY" to archiveUrl
        )
        val engine = SimpleTemplateEngine()
        val template = engine.createTemplate(binaryTargetPackageTemplateContent).make(binding)

        val credentials = UsernamePasswordCredentialsProvider(
            properties.getProperty("git.credentials.username"),
            properties.getProperty("git.credentials.password")
        )
        gitActions(credentials, gitUrl, archive, template.toString())

        if (syntheticFamily) {
            familyDirectory.deleteRecursively()
        }
    }

    private fun gitActions(
        credentials: CredentialsProvider,
        gitUrl: String,
        xcFrameworkArchive: File,
        templateContent: String
    ) {
        val gitTemporaryFolder = project.swiftPackageBuildDirs.gitDir()
        gitTemporaryFolder.deleteRecursively()

        val git = Git.cloneRepository()
            .setDirectory(gitTemporaryFolder)
            .setURI(gitUrl)
            .call()

        val generatedDirectory = gitTemporaryFolder.resolve("generated")

        FileUtils.copyFile(
            xcFrameworkArchive,
            generatedDirectory.resolve(xcFrameworkArchive.name)
        )

        val binaryPackageSwift = Files.createFile(generatedDirectory.resolve("Package.swift").toPath()).toFile()
        binaryPackageSwift.writeText(templateContent)

        git.add()
            .addFilepattern(generatedDirectory.name)
            .call()

        git.commit()
            .setMessage(COMMIT_MESSAGE)
            .call()

        git.push()
            .setCredentialsProvider(credentials)
            .call()

        gitTemporaryFolder.deleteRecursively()
    }

    companion object {
        private const val COMMIT_MESSAGE = "Add XCFramework"
    }
}
