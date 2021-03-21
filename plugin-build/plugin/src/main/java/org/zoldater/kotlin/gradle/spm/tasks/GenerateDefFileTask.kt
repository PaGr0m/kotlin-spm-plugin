package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.TaskAction
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import java.io.File

abstract class GenerateDefFileTask : DefaultTask() {
    init {
        description = "Create .def file on each platform"
        group = KotlinSpmPlugin.TASK_GROUP
    }

    @Nested
    lateinit var platformRootDirectories: List<File>

    @get:OutputDirectories
    val platformDefDirectories: List<File>
        get() = platformRootDirectories.map { it.resolve("def") }

    @TaskAction
    fun action() {
        platformRootDirectories.map { generateDefFile(it) }
    }

    private fun generateDefFile(platformDir: File) {
        // Collect all *.framework files without PROJECT_NAME.framework
        val headers = platformDir.resolve("build").resolve("Release")
            .walkTopDown()
            .filter { it.extension == "framework" && it.nameWithoutExtension != platformDir.name }
            .map { it.resolve("Headers") }
            .flatMap { it.walkTopDown() }
            .filter { it.extension == HEADER_FILE_EXTENSION }

        // Create .def file with directory
        val defDir = platformDir.resolve(DEF_FILE_EXTENSION)
        defDir.mkdirs()

        val defFile = defDir.resolve("${platformDir.name}.${DEF_FILE_EXTENSION}")
        defFile.createNewFile()
        defFile.writeText(
            """
            language = Objective-C
            headers = ${headers.joinToString(" ")}
            """.trimIndent()
        )
    }

    companion object {
        private const val HEADER_FILE_EXTENSION = "h"
        private const val DEF_FILE_EXTENSION = "def"
    }
}