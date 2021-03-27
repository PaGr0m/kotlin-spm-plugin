package org.zoldater.kotlin.gradle.spm.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class SimpleSpmTaskTest {
    @Rule
    @JvmField
    val testProjectDir: TemporaryFolder = TemporaryFolder()

    private val templateSettingsFile = this::class.java.getResource(TEMPLATE_GRADLE_SETTINGS).readText()
    private val templateBuildFile = this::class.java.getResource(TEMPLATE_GRADLE_BUILD).readText()

    private lateinit var settingsFile: File
    private lateinit var buildFile: File
    private lateinit var project: Project

    @Before
    fun setup() {
        settingsFile = testProjectDir.newFile("settings.gradle.kts").apply { writeText(templateSettingsFile) }
        buildFile = testProjectDir.newFile("build.gradle.kts").apply { writeText(templateBuildFile) }

        project = ProjectBuilder.builder()
            .withProjectDir(testProjectDir.root)
            .build()
//        project.pluginManager.apply(PLUGIN_PACKAGE)
    }

    @Test
    fun `test dependsOn`() {
        println("Tasks: " + project.tasks.toList())
    }

    private companion object {
        private const val TEMPLATE_GRADLE_SETTINGS = "/single-platform/settings.gradle.kts"
        private const val TEMPLATE_GRADLE_BUILD = "/single-platform/build.gradle.kts"
    }
}
