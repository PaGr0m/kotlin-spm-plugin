package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import org.zoldater.kotlin.gradle.spm.SwiftPackageBuildDirs
import org.zoldater.kotlin.gradle.spm.SwiftPackageCLICommand
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin

abstract class InitializeSwiftPackageProjectTask : DefaultTask() {
    init {
        /**
         * Task like a command: `swift package init`
         */
        description = "Initialize swift package template"
        group = KotlinSpmPlugin.TASK_GROUP
    }

    @Input
    lateinit var buildDirs: List<SwiftPackageBuildDirs>

    @TaskAction
    fun action() {
        buildDirs
            .map { it.root }
            .onEach { it.mkdirs() }
            .forEach { SwiftPackageCLICommand.initializeProject(it) }
    }
}