package org.zoldater.kotlin.gradle.spm.plugin

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.zoldater.kotlin.gradle.spm.entity.impl.PlatformManager
import org.zoldater.kotlin.gradle.spm.tasks.*

abstract class KotlinSpmPlugin : Plugin<Project> {

    override fun apply(project: Project) = project.pluginManager.withPlugin(MULTIPLATFORM_PLUGIN_NAME) {
        val multiplatformExtension = project.extensions.getByName(KOTLIN_PROJECT_EXTENSION_NAME)
                as KotlinMultiplatformExtension
        val spmExtension = project.extensions.create(SPM_EXTENSION_NAME, KotlinSpmExtension::class.java, project)

        val availablePlatforms = spmExtension.platformsManagerContainer

        // Graph task registration (order should not be changed)
        registerInitializeSwiftPackageProjectTask(project, availablePlatforms)
        registerCreateSwiftPackageFileTask(project, availablePlatforms)
        registerGenerateXcodeTask(project, availablePlatforms)
        registerBuildFrameworksTask(project, availablePlatforms)
        registerGenerateDefFileTask(project, availablePlatforms)

        registerInteropFrameworkTask(project, availablePlatforms, multiplatformExtension)

        registerSpmCleanTask(project, availablePlatforms)
    }

    private fun registerSpmCleanTask(
        project: Project,
        platforms: NamedDomainObjectContainer<PlatformManager.SwiftPackageManager>,
    ) {
        platforms.all { platform ->
            project.tasks.register(
                "$CLEAN_SWIFT_PACKAGE_PROJECT_TASK_NAME${platform.family}",
                CleanSwiftPackageProjectTask::class.java
            ) { task ->
                task.platformFamily.set(platform.family)
            }
        }
    }

    private fun registerInitializeSwiftPackageProjectTask(
        project: Project,
        platforms: NamedDomainObjectContainer<PlatformManager.SwiftPackageManager>,
    ) {
        platforms.all {
            project.tasks.register(
                "initProject_${it.family}",
                InitializeSwiftPackageProjectTask::class.java
            ) { task ->
                task.platformFamily.set(it.family)
            }
        }
    }

    private fun registerCreateSwiftPackageFileTask(
        project: Project,
        platforms: NamedDomainObjectContainer<PlatformManager.SwiftPackageManager>,
    ) {
        platforms.all { platform ->
            val initProjectTask = project.tasks.named(
                "initProject_${platform.family}",
                InitializeSwiftPackageProjectTask::class.java
            )

            project.tasks.register(
                "createPackageSwift_${platform.family}",
                CreateSwiftPackageFileTask::class.java
            ) { task ->
                task.platformFamily.set(platform.family)
                task.platformVersion.set(platform.version)
                task.platformDependencies.addAll(platform.dependencies)

                task.dependsOn(initProjectTask)
            }
        }
    }

    private fun registerGenerateXcodeTask(
        project: Project,
        platforms: NamedDomainObjectContainer<PlatformManager.SwiftPackageManager>,
    ) {
        platforms.all { platform ->
            val createPackageSwiftFileTask = project.tasks.named(
                "createPackageSwift_${platform.family}",
                CreateSwiftPackageFileTask::class.java
            )

            project.tasks.register(
                "generateXcodeProject_${platform.family}",
                GenerateXcodeTask::class.java
            ) { task ->
                task.platformFamily.set(platform.family)

                task.dependsOn(createPackageSwiftFileTask)
            }
        }
    }

    private fun registerBuildFrameworksTask(
        project: Project,
        platforms: NamedDomainObjectContainer<PlatformManager.SwiftPackageManager>,
    ) {
        platforms.all { platform ->
            val generateXcodeTask = project.tasks.named(
                "generateXcodeProject_${platform.family}",
                GenerateXcodeTask::class.java
            )

            platform.dependenciesContainer.all { dependency ->
                project.tasks.register(
                    "buildFramework_${platform.family}_${dependency.dependencyName}",
                    BuildFrameworksTask::class.java
                ) { task ->
                    task.platformFamily.set(platform.family)
                    task.platformDependency.set(dependency.dependencyName)

                    task.dependsOn(generateXcodeTask)
                }
            }
        }
    }

    private fun registerGenerateDefFileTask(
        project: Project,
        platforms: NamedDomainObjectContainer<PlatformManager.SwiftPackageManager>,
    ) {
        platforms.all { platform ->
            platform.dependenciesContainer.all { dependency ->
                val buildFrameworkTask = project.tasks.named(
                    "buildFramework_${platform.family}_${dependency.dependencyName}",
                    BuildFrameworksTask::class.java
                )

                project.tasks.register(
                    "generateDefFile_${platform.family}_${dependency.dependencyName}",
                    GenerateDefFileTask::class.java
                ) { task ->
                    task.platformFamily.set(platform.family)
                    task.platformDependency.set(dependency.dependencyName)

                    task.dependsOn(buildFrameworkTask)
                }
            }
        }
    }

    @Suppress("UnstableApiUsage")
    private fun registerInteropFrameworkTask(
        project: Project,
        platforms: NamedDomainObjectContainer<PlatformManager.SwiftPackageManager>,
        multiplatformExtension: KotlinMultiplatformExtension,
    ) {
        multiplatformExtension.supportedTargets().all { mppTarget ->
            platforms.all { platform ->
                val family = platform.family
                if (family == mppTarget.konanTarget.family) {
                    platform.dependenciesContainer.all { dependency ->
                        val defFileTask = project.tasks.named(
                            "generateDefFile_${family}_${dependency.dependencyName}",
                            GenerateDefFileTask::class.java
                        )
                        mppTarget.compilations.getByName(KotlinCompilation.MAIN_COMPILATION_NAME).cinterops.create(
                            dependency.dependencyName
                        ) { interop ->
                            val interopTask = project.tasks.getByPath(interop.interopProcessingTaskName)
                            interopTask.dependsOn(defFileTask)
                            interopTask.group = TASK_GROUP

                            interop.defFileProperty.set(defFileTask.flatMap { it.outputDefFile })
                            interop.packageName = "spm.${dependency.dependencyName}"
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val KOTLIN_PROJECT_EXTENSION_NAME = "kotlin"
        const val MULTIPLATFORM_PLUGIN_NAME = "kotlin-multiplatform"
        const val SPM_EXTENSION_NAME = "spm"
        const val TASK_GROUP = "swift package manager"

        // TODO: вернуть константы
        const val INITIALIZE_SWIFT_PACKAGE_PROJECT_TASK_NAME = "initializeSwiftPackageProject"
        const val CREATE_PACKAGE_SWIFT_FILE_TASK_NAME = "createPackageSwiftFile"
        const val GENERATE_XCODE_TASK_NAME = "generateXcode"
        const val BUILD_FRAMEWORK_TASK_NAME = "buildFrameworks"
        const val GENERATE_DEF_FILE_TASK_NAME = "generateDefFile"
        const val CLEAN_SWIFT_PACKAGE_PROJECT_TASK_NAME = "cleanSwiftPackageProject"
        const val INTEROP_FRAMEWORK_TASK_NAME = "interopFramework"

        private fun KotlinMultiplatformExtension.supportedTargets() = targets
            .withType(KotlinNativeTarget::class.java)
            .matching { it.konanTarget.family.isAppleFamily }
    }
}
