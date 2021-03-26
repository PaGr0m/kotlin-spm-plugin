package org.zoldater.kotlin.gradle.spm.plugin

import junit.framework.Assert.*
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.jetbrains.kotlin.konan.target.Family
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

    private lateinit var settingsFile: File
    private lateinit var buildFile: File

    private val templateSettingsFile = this::class.java.getResource(TEMPLATE_GRADLE_SETTINGS).readText()
    private val templateBuildFile = this::class.java.getResource(TEMPLATE_GRADLE_BUILD).readText()

    private val initTaskName = "${KotlinSpmPlugin.INITIALIZE_SWIFT_PACKAGE_PROJECT_TASK_NAME}${Family.IOS}"
    private val createPackageSwiftTaskName = "${KotlinSpmPlugin.CREATE_PACKAGE_SWIFT_FILE_TASK_NAME}${Family.IOS}"
    private val generateXcodeTaskName = "${KotlinSpmPlugin.GENERATE_XCODE_TASK_NAME}${Family.IOS}"
    private val buildFrameworkTaskName = "${KotlinSpmPlugin.BUILD_FRAMEWORK_TASK_NAME}${Family.IOS}$FRAMEWORK_NAME"
    private val generateDefFileTaskName = "${KotlinSpmPlugin.GENERATE_DEF_FILE_TASK_NAME}${Family.IOS}$FRAMEWORK_NAME"
    private val interopFrameworkTaskName = "${KotlinSpmPlugin.INTEROP_FRAMEWORK_TASK_NAME}${Family.IOS}$FRAMEWORK_NAME"

    @Before
    fun setup() {
        settingsFile = testProjectDir.newFile("settings.gradle.kts")
        buildFile = testProjectDir.newFile("build.gradle.kts")

        settingsFile.writeText(templateSettingsFile)
        buildFile.writeText(templateBuildFile)
    }

    @Test
    fun `test initialize IOS swift project task`() {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(initTaskName)
            .withPluginClasspath()
            .build()

        assertTrue(result.output.contains(initializeTaskOutput(Family.IOS), true))
        assertEquals(TaskOutcome.SUCCESS, result.task(":$initTaskName")?.outcome)

        // TODO: add dir check
        val spmBuildDir = testProjectDir.root.toSpmBuildDir()
        assertTrue(spmBuildDir.exists())
    }

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

        assertEquals(TaskOutcome.SUCCESS, result.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$initTaskName")?.outcome)
    }

    @Test
    fun `test create package swift IOS task`() {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(createPackageSwiftTaskName)
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$createPackageSwiftTaskName")?.outcome)

        // TODO: add dir check
//        val spmBuildDir = testProjectDir.root.toSpmBuildDir()
//        assertTrue(spmBuildDir.exists())
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

        assertNotNull(result.task(":$initTaskName"))
        assertNotNull(result.task(":$createPackageSwiftTaskName"))

        assertEquals(TaskOutcome.SUCCESS, result.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$createPackageSwiftTaskName")?.outcome)

        assertNotNull(sameResult.task(":$initTaskName"))
        assertNotNull(sameResult.task(":$createPackageSwiftTaskName"))

        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$createPackageSwiftTaskName")?.outcome)
    }

    @Test
    fun `test generate Xcode project IOS task`() {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(generateXcodeTaskName)
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$createPackageSwiftTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$generateXcodeTaskName")?.outcome)
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
            .withArguments(createPackageSwiftTaskName)
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$createPackageSwiftTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$generateXcodeTaskName")?.outcome)

        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$createPackageSwiftTaskName")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$generateXcodeTaskName")?.outcome)
    }

    @Test
    fun `test build File framework for IOS task`() {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(buildFrameworkTaskName)
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$createPackageSwiftTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$generateXcodeTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$buildFrameworkTaskName")?.outcome)
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

        assertEquals(TaskOutcome.SUCCESS, result.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$createPackageSwiftTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$generateXcodeTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$buildFrameworkTaskName")?.outcome)

        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$createPackageSwiftTaskName")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$generateXcodeTaskName")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$buildFrameworkTaskName")?.outcome)
    }

    @Test
    fun `test generate def file IOS task`() {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(generateDefFileTaskName)
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$createPackageSwiftTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$generateXcodeTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$buildFrameworkTaskName")?.outcome)
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

        assertEquals(TaskOutcome.SUCCESS, result.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$createPackageSwiftTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$generateXcodeTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$buildFrameworkTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$generateDefFileTaskName")?.outcome)

        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$createPackageSwiftTaskName")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$generateXcodeTaskName")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$buildFrameworkTaskName")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$generateDefFileTaskName")?.outcome)
    }

    @Test
    fun `test interop framework for IOS task`() {
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments(interopFrameworkTaskName)
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$createPackageSwiftTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$generateXcodeTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$buildFrameworkTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$interopFrameworkTaskName")?.outcome)
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

        assertEquals(TaskOutcome.SUCCESS, result.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$createPackageSwiftTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$generateXcodeTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$buildFrameworkTaskName")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":$interopFrameworkTaskName")?.outcome)

        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$initTaskName")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$createPackageSwiftTaskName")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$generateXcodeTaskName")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$buildFrameworkTaskName")?.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, sameResult.task(":$generateDefFileTaskName")?.outcome)
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

    private fun File.toSpmBuildDir() = this.resolve(SwiftPackageBuildDirs.ROOT_DIRECTORY)

    private companion object {
        private const val TEMPLATE_GRADLE_SETTINGS = "/single-platform/settings.gradle.kts"
        private const val TEMPLATE_GRADLE_BUILD = "/single-platform/build.gradle.kts"

        private const val FRAMEWORK_NAME = "Files"
    }
}