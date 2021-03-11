package org.zoldater.kotlin.gradle.spm.plugin

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.zoldater.kotlin.gradle.spm.tasks.*

class SpmTasks {
    private val project = ProjectBuilder.builder().build()

    init {
        project.pluginManager.apply(PLUGIN_PACKAGE)
    }

    @Test
    fun `checking task getting`() {
        assertTrue(project.tasks.getByName(KotlinSpmPlugin.GENERATE_MANIFEST_TASK_NAME) is SpmGenerateManifestTask)
        assertTrue(project.tasks.getByName(KotlinSpmPlugin.RESOLVE_DEPENDENCY_TASK_NAME) is SpmResolveDependencyTask)
        assertTrue(project.tasks.getByName(KotlinSpmPlugin.BUILD_DEPENDENCY_TASK_NAME) is SpmBuildDependencyTask)
        assertTrue(project.tasks.getByName(KotlinSpmPlugin.INTEROP_FRAMEWORK_TASK_NAME) is SpmInteropFrameworkTask)
        assertTrue(project.tasks.getByName(KotlinSpmPlugin.LINK_KLIB_TASK_NAME) is SpmLinkKlibTask)
        assertTrue(project.tasks.getByName(KotlinSpmPlugin.SPM_IMPORT_TASK_NAME) is SpmImportTask)
        assertTrue(project.tasks.getByName(KotlinSpmPlugin.GRAPH_TASK_NAME) is SpmGraphTask)
    }

    @Test
    fun `checking task dependency - spm graph`() {
        // Arrange
        val spmGraphTask = project.tasks.getByName(KotlinSpmPlugin.GRAPH_TASK_NAME)
        val spmImportTask = project.tasks.getByName(KotlinSpmPlugin.SPM_IMPORT_TASK_NAME)

        // Assert
        assertEquals(
            setOf(spmImportTask),
            spmGraphTask.taskDependencies.getDependencies(spmGraphTask)
        )
    }

    @Test
    fun `checking task dependency - spm import`() {
        // Arrange
        val spmImportTask = project.tasks.getByName(KotlinSpmPlugin.SPM_IMPORT_TASK_NAME)
        val spmLinkKlibTask = project.tasks.getByName(KotlinSpmPlugin.LINK_KLIB_TASK_NAME)

        // Assert
        assertEquals(
            setOf(spmLinkKlibTask),
            spmImportTask.taskDependencies.getDependencies(spmImportTask)
        )
    }

    @Test
    fun `checking task dependency - link klib`() {
        // Arrange
        val spmLinkKlibTask = project.tasks.getByName(KotlinSpmPlugin.LINK_KLIB_TASK_NAME)
        val spmInteropFrameworkTask = project.tasks.getByName(KotlinSpmPlugin.INTEROP_FRAMEWORK_TASK_NAME)

        // Assert
        assertEquals(
            setOf(spmInteropFrameworkTask),
            spmLinkKlibTask.taskDependencies.getDependencies(spmLinkKlibTask)
        )
    }

    @Test
    fun `checking task dependency - interop framework`() {
        // Arrange
        val spmInteropFrameworkTask = project.tasks.getByName(KotlinSpmPlugin.INTEROP_FRAMEWORK_TASK_NAME)
        val spmBuildDependencyTask = project.tasks.getByName(KotlinSpmPlugin.BUILD_DEPENDENCY_TASK_NAME)

        // Assert
        assertEquals(
            setOf(spmBuildDependencyTask),
            spmInteropFrameworkTask.taskDependencies.getDependencies(spmInteropFrameworkTask)
        )
    }

    @Test
    fun `checking task dependency - resolve dependency`() {
        // Arrange
        val spmBuildDependencyTask = project.tasks.getByName(KotlinSpmPlugin.BUILD_DEPENDENCY_TASK_NAME)
        val spmResolveDependencyTask = project.tasks.getByName(KotlinSpmPlugin.RESOLVE_DEPENDENCY_TASK_NAME)

        // Assert
        assertEquals(
            setOf(spmResolveDependencyTask),
            spmBuildDependencyTask.taskDependencies.getDependencies(spmBuildDependencyTask)
        )
    }

    @Test
    fun `checking task dependency - generate manifest`() {
        // Arrange
        val spmResolveDependencyTask = project.tasks.getByName(KotlinSpmPlugin.RESOLVE_DEPENDENCY_TASK_NAME)
        val spmGenerateManifestTask = project.tasks.getByName(KotlinSpmPlugin.GENERATE_MANIFEST_TASK_NAME)

        // Assert
        assertEquals(
            setOf(spmGenerateManifestTask),
            spmResolveDependencyTask.taskDependencies.getDependencies(spmResolveDependencyTask)
        )
    }

    private companion object {
        private const val PLUGIN_PACKAGE = "org.zoldater.kotlin.gradle.spm.plugin"
    }
}
