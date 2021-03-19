package org.zoldater.kotlin.gradle.spm.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.konan.target.Family
import org.zoldater.kotlin.gradle.spm.SwiftPackageBuildDirs
import org.zoldater.kotlin.gradle.spm.entity.impl.PlatformManager
import org.zoldater.kotlin.gradle.spm.tasks.*

abstract class KotlinSpmPlugin : Plugin<Project> {
    // FIXME: вынести buildDirs в отдельную сущность

    override fun apply(project: Project) = with(project) {
        pluginManager.withPlugin(MULTIPLATFORM_EXTENSION_NAME) {
            val spmExtension = project.extensions.create(
                SPM_EXTENSION_NAME,
                KotlinSpmExtension::class.java, project
            )

            registerSpmCleanTask(project, spmExtension)

            // Graph task registration (order should not be changed)
            registerInitializeSwiftPackageProjectTask(project, spmExtension)
            registerCreateSwiftPackageFileTask(project, spmExtension)
            registerGenerateXcodeTask(project)
            registerBuildFrameworksTask(project)
            registerGenerateDefFileTask(project)
        }
    }

    private fun registerSpmCleanTask(
        project: Project,
        spmExtension: KotlinSpmExtension,
    ) {
        project.tasks.register(
            CLEAN_SWIFT_PACKAGE_PROJECT_TASK_NAME,
            CleanSwiftPackageProjectTask::class.java
        ) { task ->
            task.buildDirs = spmExtension.platformsManager.map { SwiftPackageBuildDirs(project, it.family) }
        }
    }

    private fun registerInitializeSwiftPackageProjectTask(
        project: Project,
        spmExtension: KotlinSpmExtension,
    ) {
        project.tasks.register(
            INITIALIZE_SWIFT_PACKAGE_PROJECT_TASK_NAME,
            InitializeSwiftPackageProjectTask::class.java
        ) { task ->
            task.buildDirs = spmExtension.platformsManager.map { SwiftPackageBuildDirs(project, it.family) }
        }
    }

    private fun registerCreateSwiftPackageFileTask(
        project: Project,
        spmExtension: KotlinSpmExtension,
    ) {
        val initializeSwiftPackageProjectTask = project.tasks.named(
            INITIALIZE_SWIFT_PACKAGE_PROJECT_TASK_NAME,
            InitializeSwiftPackageProjectTask::class.java
        )

        project.tasks.register(
            CREATE_PACKAGE_SWIFT_FILE_TASK_NAME,
            CreateSwiftPackageFileTask::class.java
        ) { task ->
            task.dependencies = spmExtension.platformsManager.map { platform ->
                when (platform) {
                    is PlatformManager.PlatformIosManager -> {
                        Family.IOS to Pair(platform.platformVersion, platform.dependencies)
                    }
                    is PlatformManager.PlatformTvosManager -> {
                        Family.TVOS to Pair(platform.platformVersion, platform.dependencies)
                    }
                    is PlatformManager.PlatformMacosManager -> {
                        Family.OSX to Pair(platform.platformVersion, platform.dependencies)
                    }
                    is PlatformManager.PlatformWatchosManager -> {
                        Family.WATCHOS to Pair(platform.platformVersion, platform.dependencies)
                    }
                    else -> throw Exception("Create Package.swift task error") // TODO: rework
                }
            }.toMap()

            task.buildDirs = initializeSwiftPackageProjectTask.get().buildDirs
            task.dependsOn(initializeSwiftPackageProjectTask)
        }
    }

    private fun registerGenerateXcodeTask(project: Project) {
        val createSwiftPackageFileTask = project.tasks.named(
            CREATE_PACKAGE_SWIFT_FILE_TASK_NAME,
            CreateSwiftPackageFileTask::class.java
        )

        project.tasks.register(
            GENERATE_XCODE_TASK_NAME,
            GenerateXcodeTask::class.java
        ) { task ->
            task.buildDirs = createSwiftPackageFileTask.get().buildDirs
            task.dependsOn(createSwiftPackageFileTask)
        }
    }

    private fun registerBuildFrameworksTask(project: Project) {
        val generateXcodeTask = project.tasks.named(
            GENERATE_XCODE_TASK_NAME,
            GenerateXcodeTask::class.java
        )

        project.tasks.register(
            BUILD_FRAMEWORK_TASK_NAME,
            BuildFrameworksTask::class.java
        ) { task ->
            task.buildDirs = generateXcodeTask.get().buildDirs
            task.dependsOn(generateXcodeTask)
        }
    }

    private fun registerGenerateDefFileTask(project: Project) {
        val buildFrameworksTask = project.tasks.named(
            BUILD_FRAMEWORK_TASK_NAME,
            BuildFrameworksTask::class.java
        )

        project.tasks.register(
            GENERATE_DEF_FILE_TASK_NAME,
            GenerateDefFileTask::class.java
        ) { task ->
            task.buildDirs = buildFrameworksTask.get().buildDirs
            task.dependsOn(buildFrameworksTask)
        }
    }

    companion object {
        const val MULTIPLATFORM_EXTENSION_NAME = "kotlin-multiplatform"
        const val SPM_EXTENSION_NAME = "spm"
        const val TASK_GROUP = "swift package manager"

//        const val INTEROP_FRAMEWORK_TASK_NAME = "spmInteropFramework"

        const val INITIALIZE_SWIFT_PACKAGE_PROJECT_TASK_NAME = "initializeSwiftPackageProject"
        const val CREATE_PACKAGE_SWIFT_FILE_TASK_NAME = "createPackageSwiftFile"
        const val GENERATE_XCODE_TASK_NAME = "generateXcode"
        const val BUILD_FRAMEWORK_TASK_NAME = "buildFrameworks"
        const val GENERATE_DEF_FILE_TASK_NAME = "generateDefFile"
        const val CLEAN_SWIFT_PACKAGE_PROJECT_TASK_NAME = "cleanSwiftPackageProject"
    }
}
