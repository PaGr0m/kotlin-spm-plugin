package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import org.zoldater.kotlin.gradle.spm.SwiftPackageCLICommand
import org.zoldater.kotlin.gradle.spm.SwiftPackageCLICommand.Companion.toCommand
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import java.io.File

abstract class BuildFrameworksTask : Exec() {
    init {
        /**
         * Task like a command: `xcodebuild build --target ${TARGET_NAME}`
         */
        description = "Build the target in the build root"
        group = KotlinSpmPlugin.TASK_GROUP

        commandLine("echo", "todo: remove") // FIXME: ???
    }

    @Nested
    lateinit var platformRootDirectories: List<File>

    @TaskAction
    fun action() {
        platformRootDirectories.forEach {
            workingDir = it
            commandLine(
                *SwiftPackageCLICommand.BUILD_XCODE_PROJECT.toCommand(),
                "-project", "${it.name}.xcodeproj",
                "-target", it.name,
                "-configuration", "Release",
                "-quiet"
            )
            exec()
        }
    }
}