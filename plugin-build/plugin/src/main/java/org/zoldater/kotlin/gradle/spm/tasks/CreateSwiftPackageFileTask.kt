package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.konan.target.Family
import org.zoldater.kotlin.gradle.spm.SwiftPackageBuildDirs
import org.zoldater.kotlin.gradle.spm.entity.impl.DependencyManager
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin

abstract class CreateSwiftPackageFileTask : DefaultTask() {
    init {
        description = "Create package.swift file with content"
        group = KotlinSpmPlugin.TASK_GROUP
    }

    @Input
    lateinit var buildDirs: List<SwiftPackageBuildDirs>

    @Input
    lateinit var dependencies: Map<Family, Pair<String, List<DependencyManager.Package>>>

    @TaskAction
    fun action() {
        buildDirs.forEach { fillSwiftPackageTemplate(it) }
    }

    private fun fillSwiftPackageTemplate(swiftPackageBuildDirs: SwiftPackageBuildDirs) {
        val fileContent = this::class.java.getResource("/Package.swift").readText()

        val platformFamily = swiftPackageBuildDirs.family
        val platformArea = "${platformFamily.platformToPackageTemplate()}(\"${dependencies[platformFamily]?.first}\")"

        val dependencyArea = dependencies[platformFamily]
            ?.second
            ?.joinToString(", ") { it.convertToPackageContent() }
            ?: throw Exception("Unable to create Package.swift file")

        val targetDependencyArea = dependencies[platformFamily]
            ?.second
            ?.joinToString(", ") { "\"${it.dependencyName}\"" }
            ?: throw Exception("Unable to create Package.swift file")

        swiftPackageBuildDirs.swiftPackageFile.writeText(fileContent
            .replace("\$PLATFORM_NAME", swiftPackageBuildDirs.family.name)
            .replace("\$PLATFORM_TYPE", platformArea)
            .replace("\$DEPENDENCIES", dependencyArea)
            .replace("\$TARGET_DEPENDENCY", targetDependencyArea)
        )
    }

    private fun Family.platformToPackageTemplate(): String = when (this) {
        Family.IOS -> ".iOS"
        Family.OSX -> ".macOS"
        Family.TVOS -> ".tvOS"
        Family.WATCHOS -> ".watchOS"
        else -> throw Exception("Apple family platform not found")
    }
}