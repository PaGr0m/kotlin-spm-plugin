package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.konan.target.Family
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import org.zoldater.kotlin.gradle.spm.swiftPackageBuildDirs
import java.io.File

abstract class GenerateDefFileTask : DefaultTask() {
    init {
        description = "Create .def file on each platform"
        group = KotlinSpmPlugin.TASK_GROUP
    }

    @Nested
    val platformFamily: Property<Family> = project.objects.property(Family::class.java)

    @Input
    val platformDependency: Property<String> = project.objects.property(String::class.java)

    @get:OutputFile
    val outputDefFile: Provider<File>
        get() = platformFamily.map {
            project.swiftPackageBuildDirs.defsDir(it).resolve("${platformDependency.get()}.def")
        }

    @TaskAction
    fun action() {
        val family = platformFamily.get()
        val frameworkName = platformDependency.get()

        val frameworkDir = project.swiftPackageBuildDirs
            .releaseDir(family)
            .resolve("$frameworkName.framework")

        val headers = frameworkDir
            .resolve("Headers")
            .walkTopDown()
            .filter { it.extension == "h" }

        val defsDir = project.swiftPackageBuildDirs.defsDir(family)
        if (!defsDir.exists()) {
            defsDir.mkdirs()
        }

        val defFile = defsDir.resolve("$frameworkName.def")
        defFile.createNewFile()
        defFile.writeText(
            """
            language = Objective-C
            headers = ${headers.joinToString(" ")}
            """.trimIndent()
        )
    }
}