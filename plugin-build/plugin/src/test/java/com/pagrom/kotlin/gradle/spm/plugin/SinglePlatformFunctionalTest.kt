package com.pagrom.kotlin.gradle.spm.plugin

import com.pagrom.kotlin.gradle.spm.SwiftPackageBuildDirs
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.jetbrains.kotlin.konan.target.Family
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class SinglePlatformFunctionalTest {
    @Rule
    @JvmField
    val testProjectDir: TemporaryFolder = TemporaryFolder()

    private val templateSettingsFile = this::class.java.getResource(TEMPLATE_GRADLE_SETTINGS)!!.readText()
    private val templateBuildFile = this::class.java.getResource(TEMPLATE_GRADLE_BUILD)!!.readText()

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
        val resultInitTask = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(initTaskName)
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, resultInitTask.task(":$initTaskName")?.outcome)
        val packageSwiftFile = testProjectDir.root
            .toSpmBuildDir()
            .resolve("${Family.IOS}")
            .resolve("Package.swift")
        assertTrue(packageSwiftFile.exists())

        val packageSwiftContentBeforeTask = packageSwiftFile.readText()

        val resultCreatePackageSwiftTask = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(createPackageSwiftTaskName)
            .withPluginClasspath()
            .build()

        val expectedTasks = listOf(
            resultCreatePackageSwiftTask.task(":$initTaskName"),
            resultCreatePackageSwiftTask.task(":$createPackageSwiftTaskName"),
        )

        val packageSwiftContentAfterTask = packageSwiftFile.readText()

        // Checking participating tasks
        assertArrayEquals(expectedTasks, resultCreatePackageSwiftTask.tasks)
        assertEquals(TaskOutcome.UP_TO_DATE, resultCreatePackageSwiftTask.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, resultCreatePackageSwiftTask.task(":$createPackageSwiftTaskName")?.outcome)

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

        // Checking participating tasks
        assertArrayEquals(listOf(result.task(":$initTaskName")), result.tasks)
        assertArrayEquals(listOf(sameResult.task(":$initTaskName")), sameResult.tasks)

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

        // Checking participating tasks
        assertArrayEquals(
            listOf(
                result.task(":$initTaskName"),
                result.task(":$createPackageSwiftTaskName"),
            ),
            result.tasks
        )
        assertArrayEquals(
            listOf(
                sameResult.task(":$initTaskName"),
                sameResult.task(":$createPackageSwiftTaskName"),
            ),
            sameResult.tasks
        )

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

        // Checking participating tasks
        assertArrayEquals(
            listOf(
                result.task(":$initTaskName"),
                result.task(":$createPackageSwiftTaskName"),
                result.task(":$generateXcodeTaskName"),
            ),
            result.tasks
        )
        assertArrayEquals(
            listOf(
                sameResult.task(":$initTaskName"),
                sameResult.task(":$createPackageSwiftTaskName"),
                sameResult.task(":$generateXcodeTaskName"),
            ),
            sameResult.tasks
        )

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

        // Checking participating tasks
        assertArrayEquals(
            listOf(
                result.task(":$initTaskName"),
                result.task(":$createPackageSwiftTaskName"),
                result.task(":$generateXcodeTaskName"),
                result.task(":$buildFrameworkTaskName"),
            ),
            result.tasks
        )
        assertArrayEquals(
            listOf(
                sameResult.task(":$initTaskName"),
                sameResult.task(":$createPackageSwiftTaskName"),
                sameResult.task(":$generateXcodeTaskName"),
                sameResult.task(":$buildFrameworkTaskName"),
            ),
            sameResult.tasks
        )

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

        // Checking participating tasks
        assertArrayEquals(
            listOf(
                result.task(":$initTaskName"),
                result.task(":$createPackageSwiftTaskName"),
                result.task(":$generateXcodeTaskName"),
                result.task(":$buildFrameworkTaskName"),
                result.task(":$generateDefFileTaskName"),
            ),
            result.tasks
        )
        assertArrayEquals(
            listOf(
                sameResult.task(":$initTaskName"),
                sameResult.task(":$createPackageSwiftTaskName"),
                sameResult.task(":$generateXcodeTaskName"),
                sameResult.task(":$buildFrameworkTaskName"),
                sameResult.task(":$generateDefFileTaskName"),
            ),
            sameResult.tasks
        )

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
            .withArguments(interopFrameworkTaskName)
            .withPluginClasspath()
            .build()

        // Checking participating tasks
        assertArrayEquals(
            listOf(
                result.task(":$initTaskName"),
                result.task(":$createPackageSwiftTaskName"),
                result.task(":$generateXcodeTaskName"),
                result.task(":$buildFrameworkTaskName"),
                result.task(":$generateDefFileTaskName"),
                result.task(":$interopFrameworkTaskName"),
            ),
            result.tasks
        )
        assertArrayEquals(
            listOf(
                sameResult.task(":$initTaskName"),
                sameResult.task(":$createPackageSwiftTaskName"),
                sameResult.task(":$generateXcodeTaskName"),
                sameResult.task(":$buildFrameworkTaskName"),
                sameResult.task(":$generateDefFileTaskName"),
                sameResult.task(":$interopFrameworkTaskName"),
            ),
            sameResult.tasks
        )

        checkTasksOutcome(result.tasks, TaskOutcome.SUCCESS)
        checkTasksOutcome(sameResult.tasks, TaskOutcome.UP_TO_DATE)
    }

    @Test
    fun `test create package swift task after init spm project task`() {
        val resultInitTask = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(initTaskName)
            .withPluginClasspath()
            .build()
        assertEquals(TaskOutcome.SUCCESS, resultInitTask.task(":$initTaskName")?.outcome)

        val resultCreatePackageSwiftTask = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(createPackageSwiftTaskName)
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.UP_TO_DATE, resultCreatePackageSwiftTask.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, resultCreatePackageSwiftTask.task(":$createPackageSwiftTaskName")?.outcome)
    }

    /**
     * Cacheable test
     */
    @Test
    fun `test cache task`() {
        val resultInitTask = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(initTaskName)
            .withPluginClasspath()
            .build()

        val resultInitTaskBeforeClean = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(initTaskName, "--build-cache")
            .withPluginClasspath()
            .build()

        GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("clean")
            .withPluginClasspath()
            .build()

        val resultInitTaskAfterClean = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(initTaskName, "--build-cache")
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, resultInitTask.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, resultInitTaskBeforeClean.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.FROM_CACHE, resultInitTaskAfterClean.task(":$initTaskName")?.outcome)
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