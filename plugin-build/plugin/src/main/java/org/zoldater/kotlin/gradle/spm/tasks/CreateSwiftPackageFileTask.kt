package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.konan.target.Family
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import org.zoldater.kotlin.gradle.spm.swiftPackageBuildDirs
import java.io.File

abstract class CreateSwiftPackageFileTask : DefaultTask() {
    init {
        description = "Create package.swift file with content"
        group = KotlinSpmPlugin.TASK_GROUP
    }

    private val swiftPackageTemplateContent = this::class.java.getResource("/Package.swift").readText()

    @Nested
    lateinit var dependencies: Map<Family, KotlinSpmPlugin.PlatformDependency>

    @Nested
    lateinit var platformRootDirectories: List<File>

    @get:OutputFiles
    val platformSwiftPackages: List<File>
        get() = platformRootDirectories.map { it.resolve("Package.swift") }

    @TaskAction
    fun action() {
        platformRootDirectories.forEach { fillSwiftPackageTemplate(it) }
    }

    private fun fillSwiftPackageTemplate(platformDir: File) {
        val family = platformDir.name.toFamily()

        val platformArea = "${family.toPlatformSwiftPackageTemplate()}(\"${dependencies[family]?.version}\")"
        val dependencyArea = dependencies[family]
            ?.dependencies
            ?.joinToString(", ") { it.convertToPackageContent() }
            ?: throw IllegalArgumentException("Unable to create Package.swift file")

        val targetDependencyArea = dependencies[family]
            ?.dependencies
            ?.joinToString(", ") { "\"${it.dependencyName}\"" }
            ?: throw IllegalArgumentException("Unable to create Package.swift file")

        platformDir.resolve("Package.swift").writeText(swiftPackageTemplateContent
            .replace("\$PLATFORM_NAME", family.name)
            .replace("\$PLATFORM_TYPE", platformArea)
            .replace("\$DEPENDENCIES", dependencyArea)
            .replace("\$TARGET_DEPENDENCY", targetDependencyArea)
        )
    }

    private fun Family.toPlatformSwiftPackageTemplate(): String = when (this) {
        Family.IOS -> ".iOS"
        Family.OSX -> ".macOS"
        Family.TVOS -> ".tvOS"
        Family.WATCHOS -> ".watchOS"
        else -> throw IllegalArgumentException("Apple family platform not found")
    }

    private fun String.toFamily(): Family = when (this) {
        "IOS" -> Family.IOS
        "OSX" -> Family.OSX
        "TVOS" -> Family.TVOS
        "WATCHOS" -> Family.WATCHOS
        else -> throw IllegalArgumentException("Apple platform not found")
    }
}