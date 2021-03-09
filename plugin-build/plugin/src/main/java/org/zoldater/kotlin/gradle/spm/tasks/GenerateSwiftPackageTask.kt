package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmExtension
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import org.zoldater.kotlin.gradle.spm.swiftPackageBuildDirs
import java.io.File

abstract class GenerateSwiftPackageTask : DefaultTask() {
    init {
        group = KotlinSpmPlugin.TASK_GROUP
    }

    // TODO: abstract for paths
    @get:OutputFile
    val swiftPackageFile: File
        get() = project.swiftPackageBuildDirs.root.resolve("package.swift")

    @TaskAction
    fun action() {
        swiftPackageFile.writeText(
            File("/home/pagrom/University/diploma/kotlin-spm-plugin/plugin-build/plugin/src/main/resources/package.swift").readText()
        )


    }

    // FIXME: create package.swift template
    private fun swiftPackageTemplate(spmException: KotlinSpmExtension) = """
        import PackageDescription

        let package = Package(
            name: ${spmException.name},
            targets: [ ${spmException.targets}]
        )
    """.trimIndent()
}