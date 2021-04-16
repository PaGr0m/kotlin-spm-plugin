package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.tasks.*
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import org.zoldater.kotlin.gradle.spm.swiftPackageBuildDirs
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
    val xcFramework: File = project.swiftPackageBuildDirs.xcFrameworkDir().resolve("all.xcframework")

    override fun exec() {
        if (xcFramework.exists()) {
            Files.delete(xcFramework.toPath())
        }

        workingDir = project.swiftPackageBuildDirs.root
        commandLine("xcodebuild", "-create-xcframework")

        val frameworks: List<File> = targetsRoot
            .walkTopDown()
            .filter { it.extension == "framework" }
            .toList()

        val dSYMs: List<File> = targetsRoot
            .walkTopDown()
            .filter { it.extension == "dSYM" }
            .toList()

        frameworks.zip(dSYMs).forEach { (framework, dSYM) ->
            args("-framework", framework.absolutePath)
            args("-debug-symbols", dSYM.absolutePath)
        }

        args("-output", xcFramework)

        super.exec()
    }
}