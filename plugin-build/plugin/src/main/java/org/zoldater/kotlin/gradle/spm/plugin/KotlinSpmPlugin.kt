package org.zoldater.kotlin.gradle.spm.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.konan.target.Family
import org.zoldater.kotlin.gradle.spm.SwiftPackageBuildDirs
import org.zoldater.kotlin.gradle.spm.entity.impl.DependencyManager
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
            registerGenerateXcodeTask(project, spmExtension)
            registerGenerateFrameworksTask(project, spmExtension)
            registerGenerateDefFileTask(project, spmExtension)
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
            task.buildDirs = getPlatformBuildDirs(project, spmExtension)
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
            task.buildDirs = getPlatformBuildDirs(project, spmExtension)
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
            val platformVersions = mutableMapOf<Family, String>()
            val platformDependencies = mutableMapOf<Family, List<DependencyManager.Package>>()

            spmExtension.platformsManager.map { platform ->
                when (platform) {
                    is PlatformManager.PlatformIosManager -> {
                        platformVersions[Family.IOS] = platform.platformVersion
                        platformDependencies[Family.IOS] = platform.dependencies
                    }
                    is PlatformManager.PlatformTvosManager -> {
                        platformVersions[Family.TVOS] = platform.platformVersion
                        platformDependencies[Family.TVOS] = platform.dependencies
                    }
                    is PlatformManager.PlatformMacosManager -> {
                        platformVersions[Family.OSX] = platform.platformVersion
                        platformDependencies[Family.OSX] = platform.dependencies
                    }
                    is PlatformManager.PlatformWatchosManager -> {
                        platformVersions[Family.WATCHOS] = platform.platformVersion
                        platformDependencies[Family.WATCHOS] = platform.dependencies
                    }
                    else -> throw Exception("Create Package.swift task error") // TODO: rework
                }
            }

            task.platformVersion = platformVersions
            task.dependencies = platformDependencies
            task.buildDirs = getPlatformBuildDirs(project, spmExtension)
            task.dependsOn(initializeSwiftPackageProjectTask)
        }
    }

    private fun registerGenerateXcodeTask(
        project: Project,
        spmExtension: KotlinSpmExtension,
    ) {
        val createSwiftPackageFileTask = project.tasks.named(
            CREATE_PACKAGE_SWIFT_FILE_TASK_NAME,
            CreateSwiftPackageFileTask::class.java
        )

        project.tasks.register(
            GENERATE_XCODE_TASK_NAME,
            GenerateXcodeTask::class.java
        ) { task ->
            task.buildDirs = getPlatformBuildDirs(project, spmExtension)
            task.dependsOn(createSwiftPackageFileTask)
        }
    }

    private fun registerGenerateFrameworksTask(
        project: Project,
        spmExtension: KotlinSpmExtension,
    ) {
        val generateXcodeTask = project.tasks.named(
            GENERATE_XCODE_TASK_NAME,
            GenerateXcodeTask::class.java
        )

        project.tasks.register(
            BUILD_FRAMEWORK_TASK_NAME,
            BuildFrameworksTask::class.java
        ) { task ->
            task.buildDirs = getPlatformBuildDirs(project, spmExtension)
            task.dependsOn(generateXcodeTask)
        }
    }

    private fun registerGenerateDefFileTask(
        project: Project,
        spmExtension: KotlinSpmExtension,
    ) {
        val buildFrameworksTask = project.tasks.named(
            BUILD_FRAMEWORK_TASK_NAME,
            BuildFrameworksTask::class.java
        )

        project.tasks.register(
            GENERATE_DEF_FILE_TASK_NAME,
            GenerateDefFileTask::class.java
        ) { task ->
            task.buildDirs = getPlatformBuildDirs(project, spmExtension)
            task.dependsOn(buildFrameworksTask)
        }
    }

    private fun getPlatformBuildDirs(
        project: Project,
        spmExtension: KotlinSpmExtension,
    ): List<SwiftPackageBuildDirs> {
        return spmExtension.platformsManager.map { platform ->
            when (platform) {
                is PlatformManager.PlatformIosManager -> Family.IOS
                is PlatformManager.PlatformTvosManager -> Family.TVOS
                is PlatformManager.PlatformMacosManager -> Family.OSX
                is PlatformManager.PlatformWatchosManager -> Family.WATCHOS
                else -> throw Exception("TODO") // TODO
            }
        }.map { SwiftPackageBuildDirs(project, it) }
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
