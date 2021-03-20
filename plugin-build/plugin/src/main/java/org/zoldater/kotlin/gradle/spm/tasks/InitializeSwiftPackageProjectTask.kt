package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.konan.target.Family
import org.zoldater.kotlin.gradle.spm.SwiftPackageCLICommand
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import org.zoldater.kotlin.gradle.spm.swiftPackageBuildDirs
import java.io.File

abstract class InitializeSwiftPackageProjectTask : DefaultTask() {
    init {
        /**
         * Task like a command: `swift package init`
         */
        description = "Initialize swift package template"
        group = KotlinSpmPlugin.TASK_GROUP
    }

    @Nested
    lateinit var platformFamilies: List<Family>

    @get:OutputDirectories
    val platformRootDirectories: List<File>
        get() = platformFamilies.map { project.swiftPackageBuildDirs.pathToPlatformRoot(it) }

    @TaskAction
    fun action() {
        platformFamilies
            .map { project.swiftPackageBuildDirs.pathToPlatformRoot(it) }
            .onEach { it.mkdirs() }
            .forEach { SwiftPackageCLICommand.initializeProject(it) }
    }
}