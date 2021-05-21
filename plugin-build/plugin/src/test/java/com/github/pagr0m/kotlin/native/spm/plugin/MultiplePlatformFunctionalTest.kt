package com.github.pagr0m.kotlin.native.spm.plugin

import com.github.pagr0m.kotlin.native.spm.plugin.KotlinSpmPlugin
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.jetbrains.kotlin.konan.target.Family
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class MultiplePlatformFunctionalTest {
    @Rule
    @JvmField
    val testProjectDir: TemporaryFolder = TemporaryFolder()

    private val templateSettingsFile = this::class.java.getResource(TEMPLATE_GRADLE_SETTINGS)!!.readText()
    private val templateBuildFile = this::class.java.getResource(TEMPLATE_GRADLE_BUILD)!!.readText()

    private val initTaskName = "${KotlinSpmPlugin.INITIALIZE_SWIFT_PACKAGE_PROJECT_TASK_NAME}${Family.OSX}"
    private val createPackageSwiftTaskName = "${KotlinSpmPlugin.CREATE_PACKAGE_SWIFT_FILE_TASK_NAME}${Family.OSX}"
    private val generateXcodeTaskName = "${KotlinSpmPlugin.GENERATE_XCODE_TASK_NAME}${Family.OSX}"
    private val buildFrameworkTaskName1 = "${KotlinSpmPlugin.BUILD_FRAMEWORK_TASK_NAME}${Family.OSX}$FRAMEWORK1_NAME"
    private val buildFrameworkTaskName2 = "${KotlinSpmPlugin.BUILD_FRAMEWORK_TASK_NAME}${Family.OSX}$FRAMEWORK2_NAME"

    private lateinit var settingsFile: File
    private lateinit var buildFile: File

    @Before
    fun setup() {
        settingsFile = testProjectDir.newFile("settings.gradle.kts").apply { writeText(templateSettingsFile) }
        buildFile = testProjectDir.newFile("build.gradle.kts").apply { writeText(templateBuildFile) }
    }

    @Test
    fun `test build framework with common tasks`() {
        val buildFrameworkResult1 = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(buildFrameworkTaskName1)
            .withPluginClasspath()
            .build()

        val buildFrameworkResult2 = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(buildFrameworkTaskName2)
            .withPluginClasspath()
            .build()

        val expectedTasks1 = listOf(
            buildFrameworkResult1.task(":$initTaskName"),
            buildFrameworkResult1.task(":$createPackageSwiftTaskName"),
            buildFrameworkResult1.task(":$generateXcodeTaskName"),
            buildFrameworkResult1.task(":$buildFrameworkTaskName1"),
        )

        val expectedTasks2: List<BuildTask?> = listOf(
            buildFrameworkResult2.task(":$initTaskName"),
            buildFrameworkResult2.task(":$createPackageSwiftTaskName"),
            buildFrameworkResult2.task(":$generateXcodeTaskName"),
            buildFrameworkResult2.task(":$buildFrameworkTaskName2"),
        )

        assertTrue(buildFrameworkResult1.tasks.containsAll(expectedTasks1))
        assertTrue(buildFrameworkResult2.tasks.containsAll(expectedTasks2))

        checkTasksOutcome(buildFrameworkResult1.tasks, TaskOutcome.SUCCESS)

        buildFrameworkResult2.tasks.forEach { assertNotNull(it) }
        assertEquals(TaskOutcome.UP_TO_DATE, buildFrameworkResult2.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, buildFrameworkResult2.task(":$createPackageSwiftTaskName")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, buildFrameworkResult2.task(":$generateXcodeTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, buildFrameworkResult2.task(":$buildFrameworkTaskName2")?.outcome)
    }

    @Test
    fun `test with wrong platform`() {
        GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("wrong task")
            .withPluginClasspath()
            .buildAndFail()
    }

    private companion object {
        private const val TEMPLATE_GRADLE_SETTINGS = "/multiple-platform/settings.gradle.kts"
        private const val TEMPLATE_GRADLE_BUILD = "/multiple-platform/build.gradle.kts"

        private const val FRAMEWORK1_NAME = "AFNetworking"
        private const val FRAMEWORK2_NAME = "Alamofire"
    }
}
