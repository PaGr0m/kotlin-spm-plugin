package org.zoldater.kotlin.gradle.spm.plugin

import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.util.ConfigureUtil
import org.jetbrains.kotlin.gradle.plugin.spm.entity.Product
import org.jetbrains.kotlin.gradle.plugin.spm.entity.Target
import org.jetbrains.kotlin.gradle.plugin.spm.entity.impl.DependencyManager
import org.jetbrains.kotlin.gradle.plugin.spm.entity.impl.ProductManager
import org.jetbrains.kotlin.gradle.plugin.spm.entity.impl.SupportedPlatformManager
import org.jetbrains.kotlin.gradle.plugin.spm.entity.impl.TargetManager
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class KotlinSpmExtension @Inject constructor(project: Project) {

    @Input
    lateinit var name: String
//    var name: Property<String> = project.objects.property(String::class.java)

    private val platformsContainer = project.container(SupportedPlatformManager.SupportedPlatform::class.java)
    private val productsContainer = project.container(Product::class.java)
    private val dependenciesContainer = project.container(DependencyManager.Package::class.java)
    private val targetsContainer = project.container(Target::class.java)

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
