package com.pagrom.kotlin.gradle.spm.tasks

import com.pagrom.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import com.pagrom.kotlin.gradle.spm.swiftPackageBuildDirs
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import java.io.File
import java.nio.file.Files

@CacheableTask
abstract class BundleXCFramework : Exec() {
    init {
        description = "Bundle XCFramework from frameworks"
        group = KotlinSpmPlugin.TASK_GROUP
    }

    @InputDirectory
    val targetsRoot: File = project.buildDir.resolve("bin")

    @get:OutputDirectory
    val xcFramework: File = project.swiftPackageBuildDirs.xcFrameworkDir()
        .resolve("$XCFRAMEWORK_NAME.xcframework")

    override fun exec() {
        if (xcFramework.exists()) {
            Files.delete(xcFramework.toPath())
        }

        workingDir = project.swiftPackageBuildDirs.xcFrameworkDir()
        commandLine("xcodebuild", "-create-xcframework")

        val frameworks: List<File> = targetsRoot
            .walkTopDown()
            .filter { it.extension == "framework" }
            .toList()

        val dSYMs: List<File> = targetsRoot
            .walkTopDown()
            .filter { it.extension == "dSYM" }
            .toList()

        frameworks.forEach { args("-framework", it.absolutePath) }
        dSYMs.forEach { args("-debug-symbols", it.absolutePath) }

        args("-output", xcFramework)

        super.exec()
    }

    private companion object {
        private const val XCFRAMEWORK_NAME = "KotlinLibrary"
    }
}
