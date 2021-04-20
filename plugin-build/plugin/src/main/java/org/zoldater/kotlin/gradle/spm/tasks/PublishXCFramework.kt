package org.zoldater.kotlin.gradle.spm.tasks

import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.jetbrains.kotlin.konan.target.Family
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import org.zoldater.kotlin.gradle.spm.swiftPackageBuildDirs
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

@Suppress("UnstableApiUsage")
@CacheableTask
abstract class PublishXCFramework : Exec() {
    init {
        description = "Publishing the XCFramework to the git repository"
        group = KotlinSpmPlugin.TASK_GROUP
    }

    @InputFile
    val archiveXCFramework: Property<RegularFile> = project.objects.fileProperty()

    @Input
    val family: Property<Family> = project.objects.property(Family::class.java)

    override fun exec() {
        val archive = archiveXCFramework.get().asFile

        standardOutput = ByteArrayOutputStream()
        workingDir = project.swiftPackageBuildDirs.platformRoot(Family.IOS)
        commandLine(
            "swift", "package", "compute-checksum", archive.absolutePath
        )

        super.exec()

        val checksum = standardOutput.toString()

        val properties = Properties().apply {
            load(project.rootProject.file("local.properties").inputStream())
        }

        val credentials = UsernamePasswordCredentialsProvider(
            properties.getProperty("git.credentials.username"),
            properties.getProperty("git.credentials.password")
        )

        val gitUrl = properties.getProperty("git.credentials.giturl")

        gitActions(credentials, gitUrl, archive, checksum)
    }

    private fun gitActions(
        credentials: CredentialsProvider,
        gitUrl: String,
        xcFrameworkArchive: File,
        checksum: String
    ) {
        val gitTemporaryFolder = project.swiftPackageBuildDirs.gitDir()
        gitTemporaryFolder.deleteRecursively()

        val git = Git.cloneRepository()
            .setDirectory(gitTemporaryFolder)
            .setURI(gitUrl)
            .call()

        FileUtils.copyFile(
            xcFrameworkArchive,
            gitTemporaryFolder.resolve(xcFrameworkArchive.name)
        )

        val readme = gitTemporaryFolder.resolve("readme.md")
        readme.writeText("Checksum = $checksum")

        git.add()
            .addFilepattern(xcFrameworkArchive.name)
            .addFilepattern(readme.name)
            .call()

        git.commit()
            .setMessage(COMMIT_MESSAGE)
            .call()

        git.push()
            .setCredentialsProvider(credentials)
            .call()
    }

    companion object {
        private const val COMMIT_MESSAGE = "Add XCFramework"
    }
}