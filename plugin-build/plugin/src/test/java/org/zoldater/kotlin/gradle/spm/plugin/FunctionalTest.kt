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

        assertTrue(result.output.contains(initializeTaskOutput(Family.IOS), true))
        assertEquals(TaskOutcome.SUCCESS, result.task(":$taskName")?.outcome)

        val sameResult = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(taskName)
            .withPluginClasspath()
            .build()

        assertTrue(result.output.contains(initializeTaskOutput(Family.IOS), true))
        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$taskName")?.outcome)
    }

    private fun initializeTaskOutput(family: Family): String {
        return """
        Creating library package: ${family.name}
        Creating Package.swift
        Creating README.md
        Creating .gitignore
        Creating Sources/
        Creating Sources/${family.name}/${family.name}.swift
        Creating Tests/
        Creating Tests/LinuxMain.swift
        Creating Tests/${family.name}Tests/
        Creating Tests/${family.name}Tests/${family.name}Tests.swift
        Creating Tests/${family.name}Tests/XCTestManifests.swift
    """.trimIndent()
    }
}