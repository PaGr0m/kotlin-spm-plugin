package org.zoldater.kotlin.gradle.spm.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.zoldater.kotlin.gradle.spm.tasks.*

abstract class KotlinSpmPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val spmExtension = project.extensions.create(SPM_EXTENSION_NAME, KotlinSpmExtension::class.java, project)

        // Task registration (order cannot be changed)
        registerGenerateManifestTask(project)
        registerResolveDependencyTask(project)
        registerBuildDependencyTask(project)
        registerInteropFrameworkTask(project)
        registerLinkKlibTask(project)
        registerSpmImportTask(project)
        registerGraphTask(project) // FIXME: tmp
        registerGenerateSwiftPackage(project)
        registerGenerateXcode(project)
    }

    private fun registerGenerateManifestTask(
        project: Project
    ) {
        project.tasks.register(GENERATE_MANIFEST_TASK_NAME, SpmGenerateManifestTask::class.java) {
            it.doFirst { println(GENERATE_MANIFEST_TASK_NAME) }
        }
    }

    private fun registerResolveDependencyTask(
        project: Project
    ) {
        val generateManifestTask = project.tasks.named(GENERATE_MANIFEST_TASK_NAME, SpmGenerateManifestTask::class.java)

        project.tasks.register(RESOLVE_DEPENDENCY_TASK_NAME, SpmResolveDependencyTask::class.java) {
            it.dependsOn(generateManifestTask)

            it.doFirst { println(RESOLVE_DEPENDENCY_TASK_NAME) }
        }
    }

    private fun registerBuildDependencyTask(
        project: Project
    ) {
        val resolveDependencyTask = project.tasks.named(
            RESOLVE_DEPENDENCY_TASK_NAME,
            SpmResolveDependencyTask::class.java
        )

        project.tasks.register(BUILD_DEPENDENCY_TASK_NAME, SpmBuildDependencyTask::class.java) {
            it.dependsOn(resolveDependencyTask)

            it.doFirst { println(BUILD_DEPENDENCY_TASK_NAME) }
        }
    }

    private fun registerInteropFrameworkTask(
        project: Project
    ) {
        val buildDependencyTask = project.tasks.named(BUILD_DEPENDENCY_TASK_NAME, SpmBuildDependencyTask::class.java)

        project.tasks.register(INTEROP_FRAMEWORK_TASK_NAME, SpmInteropFrameworkTask::class.java) {
            it.dependsOn(buildDependencyTask)

            it.doFirst { println(INTEROP_FRAMEWORK_TASK_NAME) }
        }
    }

    private fun registerLinkKlibTask(
        project: Project
    ) {
        val interopFrameworkTask = project.tasks.named(INTEROP_FRAMEWORK_TASK_NAME, SpmInteropFrameworkTask::class.java)

        project.tasks.register(LINK_KLIB_TASK_NAME, SpmLinkKlibTask::class.java) {
            it.dependsOn(interopFrameworkTask)

            it.doFirst { println(LINK_KLIB_TASK_NAME) }
        }
    }

    private fun registerSpmImportTask(
        project: Project
    ) {
        val linkKlibTask = project.tasks.named(LINK_KLIB_TASK_NAME, SpmLinkKlibTask::class.java)

        project.tasks.register(SPM_IMPORT_TASK_NAME, SpmImportTask::class.java) {
            it.dependsOn(linkKlibTask)

            it.doFirst { println(SPM_IMPORT_TASK_NAME) }
        }
    }

    private fun registerGenerateSwiftPackage(
        project: Project
    ) {
        project.tasks.register(SPM_GENERATE_SWIFT_PACKAGE, GenerateSwiftPackageTask::class.java) {
            it.doFirst { println(SPM_GENERATE_SWIFT_PACKAGE) }
        }
    }

    private fun registerGenerateXcode(
        project: Project
    ) {
        project.tasks.register(SPM_GENERATE_XCODE, GenerateXcodeProjectTask::class.java) {
            it.doFirst { println(SPM_GENERATE_XCODE) }
        }
    }

    // FIXME: tmp
    private fun registerGraphTask(
        project: Project
    ) {
        val spmImportTask = project.tasks.named(SPM_IMPORT_TASK_NAME, SpmImportTask::class.java)

        project.tasks.register(GRAPH_TASK_NAME, SpmGraphTask::class.java) {
            it.dependsOn(spmImportTask)

            it.doFirst { println(GRAPH_TASK_NAME) }
        }
    }

    companion object {
        const val SPM_EXTENSION_NAME = "spm"

        const val TASK_GROUP = "swift package manager"

        const val GRAPH_TASK_NAME = "spmGraphTask" // FIXME: temporary

        const val GENERATE_MANIFEST_TASK_NAME = "spmGenerateManifest"
        const val RESOLVE_DEPENDENCY_TASK_NAME = "spmResolveDependency"
        const val BUILD_DEPENDENCY_TASK_NAME = "spmBuildDependency"
        const val INTEROP_FRAMEWORK_TASK_NAME = "spmInteropFramework"
        const val LINK_KLIB_TASK_NAME = "spmLinkKlib"
        const val SPM_IMPORT_TASK_NAME = "spmImport"

        // TODO: Tested
        const val SPM_GENERATE_SWIFT_PACKAGE = "spmGenerateSwiftPackage"
        const val SPM_GENERATE_XCODE = "spmGenerateXcode"
    }
}
