package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.jetbrains.kotlin.konan.target.Family
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import org.zoldater.kotlin.gradle.spm.swiftPackageBuildDirs
import java.io.File

@CacheableTask
abstract class InitializeSwiftPackageProjectTask : Exec() {
    init {
        /**
         * Task like a command: `swift package init`
         */
        description = "Initialize swift package template"
        group = KotlinSpmPlugin.TASK_GROUP
    }

    @Nested
    val platformFamily: Property<Family> = project.objects.property(Family::class.java)

    @get:Internal
    val platformRootDirectory: Provider<File>
        get() = platformFamily.map {
            project.swiftPackageBuildDirs.platformRoot(it)
        }

    @get:OutputFile
    val outputPlatformInitFile: Provider<File>
        get() = platformFamily.map {
            project.swiftPackageBuildDirs.platformRoot(it).resolve("$it.init")
        }

    override fun exec() {
        workingDir = platformRootDirectory.get()
        workingDir.mkdirs()

        commandLine("swift", "package", "init")
        super.exec()

        outputPlatformInitFile.get().createNewFile()
    }
}