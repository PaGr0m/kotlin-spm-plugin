package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.zoldater.kotlin.gradle.spm.SwiftPackageBuildDirs
import org.zoldater.kotlin.gradle.spm.SwiftPackageCLICommand
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin

abstract class CleanSwiftPackageProjectTask : DefaultTask() {
    init {
        description = "Clean swift package project in build directory"
        group = KotlinSpmPlugin.TASK_GROUP
    }

    @Input
    lateinit var buildDirs: List<SwiftPackageBuildDirs>

    @TaskAction
    fun action() {
        buildDirs.forEach { it.root.deleteRecursively() }
    }
}