package com.pagrom.kotlin.gradle.spm.entity.impl

import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.util.ConfigureUtil
import org.jetbrains.kotlin.konan.target.Family
import com.pagrom.kotlin.gradle.spm.entity.PlatformMarker
import com.pagrom.kotlin.gradle.spm.entity.Product
import com.pagrom.kotlin.gradle.spm.entity.Target
import javax.inject.Inject

@PlatformMarker
class PlatformManager {
    class PlatformIosManager(
        override val version: String,
        project: Project,
    ) : SwiftPackageManager(project) {
        override val family: Family get() = Family.IOS
    }

    class PlatformMacosManager(
        override val version: String,
        project: Project,
    ) : SwiftPackageManager(project) {
        override val family: Family get() = Family.OSX
    }

    class PlatformTvosManager(
        override val version: String,
        project: Project,
    ) : SwiftPackageManager(project) {
        override val family: Family get() = Family.TVOS
    }

    class PlatformWatchosManager(
        override val version: String,
        project: Project,
    ) : SwiftPackageManager(project) {
        override val family: Family get() = Family.WATCHOS
    }

    // FIXME: Can't create sealed class
    abstract class SwiftPackageManager @Inject constructor(project: Project) {
        abstract val version: String
        abstract val family: Family

        @Input
        val name: String = family.name

        private val platformsContainer = project.container(SupportedPlatformManager.SupportedPlatform::class.java)
        private val productsContainer = project.container(Product::class.java)
        private val targetsContainer = project.container(Target::class.java)

        @Nested
        val dependenciesContainer = project.container(DependencyManager.Package::class.java)

        @get:Nested
        val platforms: List<SupportedPlatformManager.SupportedPlatform>
            get() = platformsContainer.toList()

        @get:Nested
        val products: List<Product>
            get() = productsContainer.toList()

        @get:Nested
        val dependencies: List<DependencyManager.Package>
            get() = dependenciesContainer.toList()

        @get:Nested
        val targets: List<Target>
            get() = targetsContainer.toList()

        fun platforms(configure: SupportedPlatformManager.() -> Unit) {
            val supportedPlatform = SupportedPlatformManager().apply(configure)
            platformsContainer.addAll(supportedPlatform.platforms)
        }

        fun platforms(configure: Closure<*>) = platforms {
            ConfigureUtil.configure(configure, this)
        }

        fun products(configure: ProductManager.() -> Unit) {
            val productBlock = ProductManager().apply(configure)
            productsContainer.addAll(productBlock.products)
        }

        fun products(configure: Closure<*>) = products {
            ConfigureUtil.configure(configure, this)
        }

        fun dependencies(configure: DependencyManager.() -> Unit) {
            val dependencyBlock = DependencyManager().apply(configure)
            dependenciesContainer.addAll(dependencyBlock.dependencies)
        }

        fun dependencies(configure: Closure<*>) = dependencies {
            ConfigureUtil.configure(configure, this)
        }

        fun targets(configure: TargetManager.() -> Unit) {
            val targetBlock = TargetManager().apply(configure)
            targetsContainer.addAll(targetBlock.targets)
        }

        fun targets(configure: Closure<*>) = targets {
            ConfigureUtil.configure(configure, this)
        }
    }
}