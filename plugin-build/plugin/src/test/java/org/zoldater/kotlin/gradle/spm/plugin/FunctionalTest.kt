package org.zoldater.kotlin.gradle.spm.plugin

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.jetbrains.kotlin.konan.target.Family
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class FunctionalTest {
    @Rule
    @JvmField
    val testProjectDir: TemporaryFolder = TemporaryFolder()
    private lateinit var settingsFile: File
    private lateinit var buildFile: File

    private val templateSettingsFile = this::class.java.getResource("/settings.gradle.kts").readText()
    private val templateBuildFile = this::class.java.getResource("/build.gradle.kts").readText()

    @Before
    fun setup() {
        settingsFile = testProjectDir.newFile("settings.gradle.kts")
        buildFile = testProjectDir.newFile("build.gradle.kts")
    }

    @Test
    fun `test plugin activate`() {
        settingsFile.writeText(templateSettingsFile)
        buildFile.writeText(templateBuildFile)

        val taskName = "${KotlinSpmPlugin.INITIALIZE_SWIFT_PACKAGE_PROJECT_TASK_NAME}${Family.IOS}"
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(taskName)
            .withPluginClasspath()
            .build()

        assertTrue(result.output.contains("Hello world!"))
        assertEquals(TaskOutcome.SUCCESS, result.task(":$taskName")?.outcome)

        println("Tasks: ${result.tasks}")
    }
}