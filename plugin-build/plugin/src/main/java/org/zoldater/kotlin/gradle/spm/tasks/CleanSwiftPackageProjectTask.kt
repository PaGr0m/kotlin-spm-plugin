package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import org.zoldater.kotlin.gradle.spm.swiftPackageBuildDirs

abstract class CleanSwiftPackageProjectTask : DefaultTask() {
    init {
        description = "Clean swift package project in build directory"
        group = KotlinSpmPlugin.TASK_GROUP
    }

    @TaskAction
    fun action() {
        project.swiftPackageBuildDirs.root.deleteRecursively()
    }
}