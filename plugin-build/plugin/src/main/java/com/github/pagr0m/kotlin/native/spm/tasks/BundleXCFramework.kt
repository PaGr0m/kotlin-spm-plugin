package com.github.pagr0m.kotlin.native.spm.tasks

import com.github.pagr0m.kotlin.native.spm.plugin.KotlinSpmPlugin
import com.github.pagr0m.kotlin.native.spm.swiftPackageBuildDirs
import org.gradle.api.tasks.*
import java.io.File
import java.nio.file.Files

@CacheableTask
abstract class BundleXCFramework : Exec() {
    init {
        description = "Bundle XCFramework from frameworks"
        group = KotlinSpmPlugin.TASK_GROUP
    }

    @InputDirectory
    @PathSensitive(PathSensitivity.ABSOLUTE)
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
