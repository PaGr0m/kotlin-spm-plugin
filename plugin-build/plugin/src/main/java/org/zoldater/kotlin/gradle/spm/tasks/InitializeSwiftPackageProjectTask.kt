package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.konan.target.Family
import org.zoldater.kotlin.gradle.spm.SwiftPackageCLICommand
import org.zoldater.kotlin.gradle.spm.SwiftPackageCLICommand.Companion.toCommand
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import org.zoldater.kotlin.gradle.spm.swiftPackageBuildDirs
import java.io.File

abstract class InitializeSwiftPackageProjectTask : Exec() {
    init {
        /**
         * Task like a command: `swift package init`
         */
        description = "Initialize swift package template"
        group = KotlinSpmPlugin.TASK_GROUP

        commandLine("echo", "todo: remove") // FIXME: ???
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
            .filterNot { it.exists() }
            .onEach { it.mkdirs() }
            .forEach {
                workingDir = it
                commandLine(*SwiftPackageCLICommand.INITIALIZE_SWIFT_PACKAGE_PROJECT.toCommand())
                exec()
            }
    }
}