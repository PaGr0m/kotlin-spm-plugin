package com.pagrom.kotlin.gradle.spm.entity.impl

import com.pagrom.kotlin.gradle.spm.entity.Target
import com.pagrom.kotlin.gradle.spm.entity.TargetDependency
import com.pagrom.kotlin.gradle.spm.entity.TargetMarker
import com.pagrom.kotlin.gradle.spm.utils.StringUnaryPlusContainer
import groovy.lang.Closure
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.util.ConfigureUtil

/**
 * @see [Target](https://github.com/apple/swift-package-manager/blob/main/Documentation/PackageDescription.md#target)
 * @see [Target](https://docs.swift.org/package-manager/PackageDescription/PackageDescription.html#target)
 */
@TargetMarker
class TargetManager {
    val targets = mutableListOf<Target>()

    fun target(name: String, configure: RegularTarget.() -> Unit) {
        val target = RegularTarget(name).apply(configure)
        targets.add(target)
    }

    fun target(name: String, configure: Closure<*>) = target(name) {
        ConfigureUtil.configure(configure, this)
    }

    fun executableTarget(name: String, configure: ExecutableTarget.() -> Unit) {
        val target = ExecutableTarget(name).apply(configure)
        targets.add(target)
    }

    fun executableTarget(name: String, configure: Closure<*>) = executableTarget(name) {
        ConfigureUtil.configure(configure, this)
    }

    fun testTarget(name: String, configure: TestTarget.() -> Unit) {
        val target = TestTarget(name).apply(configure)
        targets.add(target)
    }

    fun testTarget(name: String, configure: Closure<*>) = testTarget(name) {
        ConfigureUtil.configure(configure, this)
    }

    fun systemLibrary(name: String, configure: SystemLibrary.() -> Unit) {
        val library = SystemLibrary(name).apply(configure)
        targets.add(library)
    }

    fun systemLibrary(name: String, configure: Closure<*>) = systemLibrary(name) {
        ConfigureUtil.configure(configure, this)
    }

    fun binaryTarget(name: String, path: String) {
        val binaryTarget = BinaryTarget(name, path)
        targets.add(binaryTarget)
    }

    abstract class AbstractTarget(private val name: String) : Target {
        override fun getName(): String = name

        @Nested
        var dependencies = mutableListOf<TargetDependency>()

        @Input
        @Optional
        var path: String? = null

        @Nested
        var excludePaths = mutableListOf<String>()

        @Nested
        @Optional
        var sourceFiles = mutableListOf<String>()

        @Nested
        @Optional
        var resources = mutableListOf<ResourceBlock.Resource>()

        fun dependencies(configure: TargetDependencyManager.() -> Unit) {
            val targetDependencyBlock = TargetDependencyManager().apply(configure)
            dependencies.addAll(targetDependencyBlock.targetDependencies)
        }

        fun exclude(configure: ExcludePath.() -> Unit) {
            val exclude = ExcludePath().apply(configure)
            excludePaths.addAll(exclude.container)
        }

        fun sources(configure: SourceFile.() -> Unit) {
            val sources = SourceFile().apply(configure)
            sourceFiles.addAll(sources.container)
        }

        fun resources(configure: ResourceBlock.() -> Unit) {
            val resource = ResourceBlock().apply(configure)
            resources.addAll(resource.resources)
        }

        class ResourceBlock {
            var resources = mutableListOf<Resource>()

            fun process(path: String) {
                val resource = Resource(path)
                resources.add(resource)
            }

            fun copy(path: String) {
                val resource = Resource(path)
                resources.add(resource)
            }

            class Resource(
                @Input val path: String
            )
        }
    }

    class RegularTarget(name: String) : AbstractTarget(name)

    class ExecutableTarget(name: String) : AbstractTarget(name)

    class TestTarget(name: String) : AbstractTarget(name)

    class SystemLibrary(
        private val name: String,
        @Input @Optional var path: String? = null,
        @Input @Optional var pkgConfig: String? = null
    ) : Target {
        override fun getName(): String = name
    }

    class BinaryTarget(
        private val name: String,
        @Input val url: String
    ) : Target {
        override fun getName(): String = name
    }

    class ExcludePath : StringUnaryPlusContainer()

    class SourceFile : StringUnaryPlusContainer()
}
