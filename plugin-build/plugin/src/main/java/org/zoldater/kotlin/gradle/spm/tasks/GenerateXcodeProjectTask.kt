package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import org.zoldater.kotlin.gradle.spm.swiftPackageBuildDirs
import java.io.File
import java.util.concurrent.TimeUnit

abstract class GenerateXcodeProjectTask : DefaultTask() {
    init {
//        description = TODO()
        group = KotlinSpmPlugin.TASK_GROUP
    }

    fun String.runCommand(workingDir: File) {
        ProcessBuilder(*split(" ").toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor(60, TimeUnit.MINUTES)
    }

    @TaskAction
    fun action() {
        project.swiftPackageBuildDirs.root.mkdirs()
        project.swiftPackageBuildDirs.root.resolve("package.swift").createNewFile()

//        File(project.swiftPackageBuildDirs.root.name + "package.swift").mkdirs()
        "swift package generate-xcodeproj".runCommand(project.swiftPackageBuildDirs.root)
    }
}