package org.zoldater.kotlin.gradle.spm.plugin

import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.util.ConfigureUtil
import org.zoldater.kotlin.gradle.spm.entity.Product
import org.zoldater.kotlin.gradle.spm.entity.Target
import org.zoldater.kotlin.gradle.spm.entity.impl.*
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class KotlinSpmExtension @Inject constructor(private val project: Project) {

    private val platformsManagerContainer = project.container(PlatformManager.SwiftPackageManager::class.java)

    @get:Nested
    val platformsManager: List<PlatformManager.SwiftPackageManager>
        get() = platformsManagerContainer.toList()

    fun ios(version: String, configure: PlatformManager.PlatformIosManager.() -> Unit) {
        val iosManager = PlatformManager.PlatformIosManager(version, project).apply(configure)
        platformsManagerContainer.add(iosManager)
    }

    fun ios(version: String, configure: Closure<*>) = ios(version) {
        ConfigureUtil.configure(configure, this)
    }

    fun tvos(version: String, configure: PlatformManager.PlatformTvosManager.() -> Unit) {
        val tvosManager = PlatformManager.PlatformTvosManager(version, project).apply(configure)
        platformsManagerContainer.add(tvosManager)
    }

    fun tvos(version: String, configure: Closure<*>) = ios(version) {
        ConfigureUtil.configure(configure, this)
    }

    fun macos(version: String, configure: PlatformManager.PlatformMacosManager.() -> Unit) {
        val macosManager = PlatformManager.PlatformMacosManager(version, project).apply(configure)
        platformsManagerContainer.add(macosManager)
    }

    fun macos(version: String, configure: Closure<*>) = ios(version) {
        ConfigureUtil.configure(configure, this)
    }

    fun watchos(version: String, configure: PlatformManager.PlatformWatchosManager.() -> Unit) {
        val watchosManager = PlatformManager.PlatformWatchosManager(version, project).apply(configure)
        platformsManagerContainer.add(watchosManager)
    }

    fun watchos(version: String, configure: Closure<*>) = ios(version) {
        ConfigureUtil.configure(configure, this)
    }

}
