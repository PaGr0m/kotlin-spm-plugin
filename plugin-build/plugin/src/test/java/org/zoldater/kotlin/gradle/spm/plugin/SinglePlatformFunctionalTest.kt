package org.zoldater.kotlin.gradle.spm.plugin

import org.junit.Assert.*
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.jetbrains.kotlin.konan.target.Family
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.zoldater.kotlin.gradle.spm.SwiftPackageBuildDirs
import java.io.File

// TODO: add not null
class SinglePlatformFunctionalTest {
    @Rule
    @JvmField
    val testProjectDir: TemporaryFolder = TemporaryFolder()

    private val templateSettingsFile = this::class.java.getResource(TEMPLATE_GRADLE_SETTINGS).readText()
    private val templateBuildFile = this::class.java.getResource(TEMPLATE_GRADLE_BUILD).readText()

    private val initTaskName = "${KotlinSpmPlugin.INITIALIZE_SWIFT_PACKAGE_PROJECT_TASK_NAME}${Family.IOS}"
    private val createPackageSwiftTaskName = "${KotlinSpmPlugin.CREATE_PACKAGE_SWIFT_FILE_TASK_NAME}${Family.IOS}"
    private val generateXcodeTaskName = "${KotlinSpmPlugin.GENERATE_XCODE_TASK_NAME}${Family.IOS}"
    private val buildFrameworkTaskName = "${KotlinSpmPlugin.BUILD_FRAMEWORK_TASK_NAME}${Family.IOS}$FRAMEWORK_NAME"
    private val generateDefFileTaskName = "${KotlinSpmPlugin.GENERATE_DEF_FILE_TASK_NAME}${Family.IOS}$FRAMEWORK_NAME"
    private val interopFrameworkTaskName = "cinteropFilesIosX64"

    private lateinit var settingsFile: File
    private lateinit var buildFile: File

    @Before
    fun setup() {
        settingsFile = testProjectDir.newFile("settings.gradle.kts").apply { writeText(templateSettingsFile) }
        buildFile = testProjectDir.newFile("build.gradle.kts").apply { writeText(templateBuildFile) }
    }

    /**
     * Command call for one platform with one dependency
     */
    @Test
    fun `test initialize IOS swift project task`() {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(initTaskName)
            .withPluginClasspath()
            .build()

        val expectedTasks = listOf(
            result.task(":$initTaskName"),
        )

        // Checking participating tasks
        assertArrayEquals(expectedTasks, result.tasks)
        checkTasksOutcome(result.tasks, TaskOutcome.SUCCESS)

        // Checking task output
        assertTrue(result.output.contains(initializeTaskOutput(Family.IOS), true))

        // Checking directories creation and files creation using task
        val platformDir = testProjectDir.root.toSpmBuildDir().resolve("${Family.IOS}")
        assertTrue(platformDir.exists())
        assertTrue(platformDir.resolve("Sources").exists())
        assertTrue(platformDir.resolve("Tests").exists())
        assertTrue(platformDir.resolve("Package.swift").exists())
        assertTrue(platformDir.resolve("${Family.IOS}.init").exists())
    }

    @Test
    fun `test create package swift IOS task`() {
        val packageSwiftContentBeforeTask = testProjectDir.root
            .toSpmBuildDir()
            .resolve("Package.swift")
            .readText()

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(createPackageSwiftTaskName)
            .withPluginClasspath()
            .build()

        val expectedTasks = listOf(
            result.task(":$initTaskName"),
            result.task(":$createPackageSwiftTaskName"),
        )

        val packageSwiftContentAfterTask = testProjectDir.root
            .toSpmBuildDir()
            .resolve("Package.swift")
            .readText()

        // Checking participating tasks
        assertArrayEquals(expectedTasks, result.tasks)
        checkTasksOutcome(result.tasks, TaskOutcome.SUCCESS)

        // TODO: check Package.swift content?
        // Checking file changes
        assertNotEquals(packageSwiftContentBeforeTask, packageSwiftContentAfterTask)
    }

    @Test
    fun `test generate Xcode project IOS task`() {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(generateXcodeTaskName)
            .withPluginClasspath()
            .build()

        val expectedTasks = listOf(
            result.task(":$initTaskName"),
            result.task(":$createPackageSwiftTaskName"),
            result.task(":$generateXcodeTaskName"),
        )

        // Checking participating tasks
        assertArrayEquals(expectedTasks, result.tasks)
        checkTasksOutcome(result.tasks, TaskOutcome.SUCCESS)

        // TODO: check Package.resolved content?
        // Checking directories creation and files creation using task
        val platformDir = testProjectDir.root.toSpmBuildDir().resolve("${Family.IOS}")
        assertTrue(platformDir.resolve(".build").exists())
        assertTrue(platformDir.resolve("${Family.IOS}.${SwiftPackageBuildDirs.XCODEPROJECT_EXTENSION}").exists())
        assertTrue(platformDir.resolve("Package.resolved").exists())
    }

    @Test
    fun `test build File framework for IOS task`() {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(buildFrameworkTaskName)
            .withPluginClasspath()
            .build()

        val expectedTasks = listOf(
            result.task(":$initTaskName"),
            result.task(":$createPackageSwiftTaskName"),
            result.task(":$generateXcodeTaskName"),
            result.task(":$buildFrameworkTaskName"),
        )

        // Checking participating tasks
        assertArrayEquals(expectedTasks, result.tasks)
        checkTasksOutcome(result.tasks, TaskOutcome.SUCCESS)

        // TODO: check other directories?
        // Checking directories creation and files creation using task
        val platformBuildDir = testProjectDir.root.toSpmBuildDir().resolve("${Family.IOS}").resolve("build")
        assertTrue(platformBuildDir.exists())
        assertTrue(platformBuildDir.resolve("Release").exists())
        assertTrue(platformBuildDir.resolve("Release").resolve("$FRAMEWORK_NAME.framework").exists())
    }

    @Test
    fun `test generate def file IOS task`() {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(generateDefFileTaskName)
            .withPluginClasspath()
            .build()

        val expectedTasks = listOf(
            result.task(":$initTaskName"),
            result.task(":$createPackageSwiftTaskName"),
            result.task(":$generateXcodeTaskName"),
            result.task(":$buildFrameworkTaskName"),
            result.task(":$generateDefFileTaskName"),
        )

        // Checking participating tasks
        assertArrayEquals(expectedTasks, result.tasks)
        checkTasksOutcome(result.tasks, TaskOutcome.SUCCESS)

        // TODO: check .def file content?
        // Checking directories creation and files creation using task
        val platformDefDir = testProjectDir.root
            .toSpmBuildDir()
            .resolve("${Family.IOS}")
            .resolve(SwiftPackageBuildDirs.DEF_DIRECTORY)
        assertTrue(platformDefDir.exists())
        assertTrue(platformDefDir.resolve("$FRAMEWORK_NAME.def").exists())
    }

    @Test
    fun `test interop framework for IOS task`() {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(interopFrameworkTaskName)
            .withPluginClasspath()
            .build()

        val expectedTasks = listOf(
            result.task(":$initTaskName"),
            result.task(":$createPackageSwiftTaskName"),
            result.task(":$generateXcodeTaskName"),
            result.task(":$buildFrameworkTaskName"),
            result.task(":$generateDefFileTaskName"),
            result.task(":$interopFrameworkTaskName"),
        )

        // Checking participating tasks
        assertArrayEquals(expectedTasks, result.tasks)
        checkTasksOutcome(result.tasks, TaskOutcome.SUCCESS)

        // TODO: check classes dir...
        val interopDir = testProjectDir.root
            .resolve("build")
            .resolve("classes")
            .resolve("kotlin")
            .resolve("iosX64")
            .resolve("main")
        assertTrue(interopDir.exists())
        assertTrue(interopDir.resolve("single-platform-test-project-cinterop-$FRAMEWORK_NAME.klib").exists())
    }

    /**
     * Double command call for one platform with one dependency.
     * UP-TO-DATE checking
     */
    @Test
    fun `test initialize IOS swift project task with UP-TO-DATE check`() {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(initTaskName)
            .withPluginClasspath()
            .build()

        val sameResult = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(initTaskName)
            .withPluginClasspath()
            .build()

        val expectedTasks = listOf(
            result.task(":$initTaskName"),
        )

        // Checking participating tasks
        assertArrayEquals(expectedTasks, result.tasks)
        assertArrayEquals(expectedTasks, sameResult.tasks)

        checkTasksOutcome(result.tasks, TaskOutcome.SUCCESS)
        checkTasksOutcome(sameResult.tasks, TaskOutcome.UP_TO_DATE)
    }

    @Test
    fun `test create package swift IOS task with UP-TO-DATE check`() {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(createPackageSwiftTaskName)
            .withPluginClasspath()
            .build()

        val sameResult = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(createPackageSwiftTaskName)
            .withPluginClasspath()
            .build()

        val expectedTasks = listOf(
            result.task(":$initTaskName"),
            result.task(":$createPackageSwiftTaskName"),
        )

        // Checking participating tasks
        assertArrayEquals(expectedTasks, result.tasks)
        assertArrayEquals(expectedTasks, sameResult.tasks)

        checkTasksOutcome(result.tasks, TaskOutcome.SUCCESS)
        checkTasksOutcome(sameResult.tasks, TaskOutcome.UP_TO_DATE)
    }

    @Test
    fun `test generate Xcode project IOS task with UP-TO-DATE check`() {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(generateXcodeTaskName)
            .withPluginClasspath()
            .build()

        val sameResult = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(generateXcodeTaskName)
            .withPluginClasspath()
            .build()

        val expectedTasks = listOf(
            result.task(":$initTaskName"),
            result.task(":$createPackageSwiftTaskName"),
            result.task(":$generateXcodeTaskName"),
        )

        // Checking participating tasks
        assertArrayEquals(expectedTasks, result.tasks)
        assertArrayEquals(expectedTasks, sameResult.tasks)

        checkTasksOutcome(result.tasks, TaskOutcome.SUCCESS)
        checkTasksOutcome(sameResult.tasks, TaskOutcome.UP_TO_DATE)
    }

    @Test
    fun `test build File framework for IOS task with UP-TO-DATE check`() {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(buildFrameworkTaskName)
            .withPluginClasspath()
            .build()

        val sameResult = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(buildFrameworkTaskName)
            .withPluginClasspath()
            .build()

        val expectedTasks = listOf(
            result.task(":$initTaskName"),
            result.task(":$createPackageSwiftTaskName"),
            result.task(":$generateXcodeTaskName"),
            result.task(":$buildFrameworkTaskName"),
        )

        // Checking participating tasks
        assertArrayEquals(expectedTasks, result.tasks)
        assertArrayEquals(expectedTasks, sameResult.tasks)

        checkTasksOutcome(result.tasks, TaskOutcome.SUCCESS)
        checkTasksOutcome(sameResult.tasks, TaskOutcome.UP_TO_DATE)
    }

    @Test
    fun `test generate def file IOS task with UP-TO-DATE check`() {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(generateDefFileTaskName)
            .withPluginClasspath()
            .build()

        val sameResult = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(generateDefFileTaskName)
            .withPluginClasspath()
            .build()

        val expectedTasks = listOf(
            result.task(":$initTaskName"),
            result.task(":$createPackageSwiftTaskName"),
            result.task(":$generateXcodeTaskName"),
            result.task(":$buildFrameworkTaskName"),
            result.task(":$generateDefFileTaskName"),
        )

        // Checking participating tasks
        assertArrayEquals(expectedTasks, result.tasks)
        assertArrayEquals(expectedTasks, sameResult.tasks)

        checkTasksOutcome(result.tasks, TaskOutcome.SUCCESS)
        checkTasksOutcome(sameResult.tasks, TaskOutcome.UP_TO_DATE)
    }

    @Test
    fun `test interop framework for IOS task with UP-TO-DATE check`() {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(interopFrameworkTaskName)
            .withPluginClasspath()
            .build()

        val sameResult = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(generateDefFileTaskName)
            .withPluginClasspath()
            .build()

        val expectedTasks = listOf(
            result.task(":$initTaskName"),
            result.task(":$createPackageSwiftTaskName"),
            result.task(":$generateXcodeTaskName"),
            result.task(":$buildFrameworkTaskName"),
            result.task(":$generateDefFileTaskName"),
            result.task(":$interopFrameworkTaskName"),
        )

        // Checking participating tasks
        assertArrayEquals(expectedTasks, result.tasks)
        assertArrayEquals(expectedTasks, sameResult.tasks)

        checkTasksOutcome(result.tasks, TaskOutcome.SUCCESS)
        checkTasksOutcome(sameResult.tasks, TaskOutcome.UP_TO_DATE)
    }

    @Test
    fun `test create package swift task after init spm project task`() {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(initTaskName, createPackageSwiftTaskName)
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.UP_TO_DATE, result.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, result.task(":$createPackageSwiftTaskName")?.outcome)
    }

    private fun initializeTaskOutput(family: Family): String = """
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

    private fun File.toSpmBuildDir() = this.resolve("build").resolve(SwiftPackageBuildDirs.ROOT_DIRECTORY)



    private companion object {
        private const val TEMPLATE_GRADLE_SETTINGS = "/single-platform/settings.gradle.kts"
        private const val TEMPLATE_GRADLE_BUILD = "/single-platform/build.gradle.kts"

        private const val FRAMEWORK_NAME = "Files"
    }
}