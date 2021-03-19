package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.zoldater.kotlin.gradle.spm.SwiftPackageBuildDirs
import org.zoldater.kotlin.gradle.spm.SwiftPackageCLICommand
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin

abstract class BuildFrameworksTask : DefaultTask() {
    init {
        /**
         * Task like a command: `xcodebuild build --target ${TARGET_NAME}`
         */
        description = "Build the target in the build root"
        group = KotlinSpmPlugin.TASK_GROUP
    }

    @Input
    lateinit var buildDirs: List<SwiftPackageBuildDirs>

    @TaskAction
    fun action() {
        buildDirs.forEach { SwiftPackageCLICommand.generateFrameworks(it.root, it.family.name) }
    }
}