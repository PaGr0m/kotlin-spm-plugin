package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import org.zoldater.kotlin.gradle.spm.SwiftPackageCLICommand
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import java.io.File

abstract class BuildFrameworksTask : DefaultTask() {
    init {
        /**
         * Task like a command: `xcodebuild build --target ${TARGET_NAME}`
         */
        description = "Build the target in the build root"
        group = KotlinSpmPlugin.TASK_GROUP
    }

    @Nested
    lateinit var platformRootDirectories: List<File>

    @TaskAction
    fun action() {
        platformRootDirectories.forEach { SwiftPackageCLICommand.generateFrameworks(it, it.nameWithoutExtension) }
    }
}