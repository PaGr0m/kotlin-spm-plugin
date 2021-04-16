package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import org.zoldater.kotlin.gradle.spm.swiftPackageBuildDirs
import java.io.File

@CacheableTask
abstract class BundleXCFramework : Exec() {
    init {
        description = "Bundle XCFramework from frameworks"
        group = KotlinSpmPlugin.TASK_GROUP
    }

    @InputDirectory
    val targetsRoot: File = project.buildDir.resolve("bin")

    @get:OutputFile
    val xcFramework: File = project.swiftPackageBuildDirs.root.resolve("all.xcframework")

    override fun exec() {
        workingDir = project.swiftPackageBuildDirs.root
        commandLine("xcodebuild", "--create-xcframework")

        targetsRoot
            .walkTopDown()
            .filter { it.extension == "framework" }
            .forEach {
                args("-framework", it.absolutePath)
            }

        args("-output", xcFramework)

        super.exec()
    }
}