package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import org.zoldater.kotlin.gradle.spm.SwiftPackageCLICommand
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import java.io.File

abstract class GenerateXcodeTask : DefaultTask() {
    init {
        /**
         * Task like a command: `swift package generate-xcodeproj`
         */
        description = "Generate Xcode project"
        group = KotlinSpmPlugin.TASK_GROUP
    }

    @Nested
    lateinit var platformRootDirectories: List<File>

    @TaskAction
    fun action() {
        platformRootDirectories.forEach { SwiftPackageCLICommand.generateXcodeProject(it) }
    }
}