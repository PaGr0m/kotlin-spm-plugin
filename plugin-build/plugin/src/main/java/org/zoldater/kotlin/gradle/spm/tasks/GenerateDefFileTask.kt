package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.zoldater.kotlin.gradle.spm.SwiftPackageBuildDirs
import org.zoldater.kotlin.gradle.spm.SwiftPackageCLICommand
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin

abstract class GenerateDefFileTask : DefaultTask() {
    init {
        description = "Create .def file on each platform"
        group = KotlinSpmPlugin.TASK_GROUP
    }

    @Input
    lateinit var buildDirs: List<SwiftPackageBuildDirs>

    @TaskAction
    fun action() {
        buildDirs.map { generateDefFile(it) }
    }

    private fun generateDefFile(swiftPackageBuildDirs: SwiftPackageBuildDirs) {
        // Take all *.framework files without PROJECT_NAME.framework
        val headers = swiftPackageBuildDirs.release
            .listFiles { _, filename -> filename.endsWith(".framework") }
            .filterNot { it.name.startsWith(swiftPackageBuildDirs.platformName) }
            .map { it.resolve("Headers") }
            .flatMap { it.listFiles().toList() }

        val defDir = swiftPackageBuildDirs.def
        defDir.mkdirs()

        val simpleDef = defDir.resolve("simple.def")
        simpleDef.createNewFile()
        simpleDef.writeText(
            """
            language = Objective-C
            headers = ${headers.joinToString(" ")}
            """.trimIndent()
        )
    }
}