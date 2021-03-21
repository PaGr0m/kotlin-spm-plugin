package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.tasks.*
import org.zoldater.kotlin.gradle.spm.SwiftPackageCLICommand
import org.zoldater.kotlin.gradle.spm.SwiftPackageCLICommand.Companion.toCommand
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import java.io.File


abstract class GenerateXcodeTask : Exec() {
    init {
        /**
         * Task like a command: `swift package generate-xcodeproj`
         */
        description = "Generate Xcode project"
        group = KotlinSpmPlugin.TASK_GROUP

        commandLine("echo", "todo: remove") // FIXME: ???
    }

    @Nested
    lateinit var platformRootDirectories: List<File>

    @get:OutputDirectories
    val platformXcodeProjects: List<File>
        get() = platformRootDirectories.map { it.resolve("${it.name}.xcodeproj") }

    @TaskAction
    fun action() {
        platformRootDirectories.forEach {
            workingDir = it
            commandLine(*SwiftPackageCLICommand.GENERATE_XCODE_PROJECT.toCommand())
            exec()
        }
    }
}