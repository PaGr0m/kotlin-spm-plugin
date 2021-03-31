package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.jetbrains.kotlin.konan.target.Family
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import org.zoldater.kotlin.gradle.spm.swiftPackageBuildDirs
import java.io.File

abstract class BuildFrameworksTask : Exec() {
    init {
        /**
         * Task like a command: `xcodebuild build --target ${TARGET_NAME}`
         */
        description = "Build the target in the build root"
        group = KotlinSpmPlugin.TASK_GROUP
    }

    @Nested
    val platformFamily: Property<Family> = project.objects.property(Family::class.java)

    @Input
    val platformDependency: Property<String> = project.objects.property(String::class.java)

    @get:OutputDirectory
    val outputFrameworkDirectory: Provider<File>
        get() = platformFamily.map {
            project.swiftPackageBuildDirs.releaseDir(it).resolve("${platformDependency.get()}.framework")
        }

    override fun exec() {
        val family = platformFamily.get()

        workingDir = project.swiftPackageBuildDirs.platformRoot(family)
        commandLine(
            "xcodebuild", "build",
            "-project", "${family.name}.xcodeproj",
            "-target", platformDependency.get(),
            "-configuration", "Release",
            "-quiet"
        )

        super.exec()
    }
}