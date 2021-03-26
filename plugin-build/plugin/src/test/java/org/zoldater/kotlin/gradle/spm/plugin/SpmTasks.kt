package org.zoldater.kotlin.gradle.spm.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class SpmTasks {
    @Rule
    @JvmField
    val testProjectDir: TemporaryFolder = TemporaryFolder()

    private lateinit var settingsFile: File
    private lateinit var buildFile: File

    private val templateSettingsFile = this::class.java.getResource(TEMPLATE_GRADLE_SETTINGS).readText()
    private val templateBuildFile = this::class.java.getResource(TEMPLATE_GRADLE_BUILD).readText()

    private lateinit var project: Project


//    private val project = ProjectBuilder.builder().build()

    //    init {
    @Before
    fun setup() {
        settingsFile = testProjectDir.newFile("settings.gradle.kts")
        buildFile = testProjectDir.newFile("build.gradle.kts")

        settingsFile.writeText(templateSettingsFile)
        buildFile.writeText(templateBuildFile)

        project = ProjectBuilder.builder()
            .withProjectDir(testProjectDir.root)
            .build()

        project.pluginManager.apply(PLUGIN_PACKAGE)
    }

    @Test
    fun `checking task dependency - spm graph`() {
        println("Start")
        println(project.tasks.joinToString(" "))
        println("Finish")


//        val graphTask = listOf(
//            project.tasks.getByName("${KotlinSpmPlugin.INITIALIZE_SWIFT_PACKAGE_PROJECT_TASK_NAME}${Family.IOS}"),
//            project.tasks.getByName(KotlinSpmPlugin.CREATE_PACKAGE_SWIFT_FILE_TASK_NAME),
//            project.tasks.getByName(KotlinSpmPlugin.GENERATE_XCODE_TASK_NAME),
//            project.tasks.getByName(KotlinSpmPlugin.BUILD_FRAMEWORK_TASK_NAME),
//            project.tasks.getByName(KotlinSpmPlugin.GENERATE_DEF_FILE_TASK_NAME),
//            project.tasks.getByName(KotlinSpmPlugin.INTEROP_FRAMEWORK_TASK_NAME),
//        )

//        val depends: MutableList<Task> = mutableListOf()
//        for (task in graphTask) {
//            assertEquals(
//                setOf(depends),
//                task.taskDependencies.getDependencies(task)
//            )
//        }

//        // Arrange
//        val spmGraphTask = project.tasks.getByName(KotlinSpmPlugin.INTEROP_FRAMEWORK_TASK_NAME)
//        val spmImportTask = project.tasks.getByName(KotlinSpmPlugin.SPM_IMPORT_TASK_NAME)
//
//        // Assert
//        assertEquals(
//            setOf(spmImportTask),
//            spmGraphTask.taskDependencies.getDependencies(spmGraphTask)
//        )
    }

    private companion object {
        private const val PLUGIN_PACKAGE = "org.zoldater.kotlin.gradle.spm.plugin"
        private const val TEMPLATE_GRADLE_SETTINGS = "/single-platform/settings.gradle.kts"
        private const val TEMPLATE_GRADLE_BUILD = "/single-platform/build.gradle.kts"
    }
}
