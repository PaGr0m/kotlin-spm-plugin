package org.zoldater.kotlin.gradle.spm.entity.impl

import org.gradle.api.Named
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.jetbrains.kotlin.konan.target.Family
import org.zoldater.kotlin.gradle.spm.entity.impl.SupportedPlatformManager.SupportedPlatform

/**
 * @see [SupportedPlatform](https://docs.swift.org/package-manager/PackageDescription/PackageDescription.html#supportedplatform)
 * @see [SupportedPlatform](https://github.com/apple/swift-package-manager/blob/main/Documentation/PackageDescription.md#supportedplatform)
 */
class SupportedPlatformManager {
    val platforms = mutableListOf<SupportedPlatform>()

    fun macOS(version: String) {
        val platform = SupportedPlatform(Family.OSX, version)
        platforms.add(platform)
    }

    fun iOS(version: String) {
        val platform = SupportedPlatform(Family.IOS, version)
        platforms.add(platform)
    }

    fun tvOS(version: String) {
        val platform = SupportedPlatform(Family.TVOS, version)
        platforms.add(platform)
    }

    fun watchOS(version: String) {
        val platform = SupportedPlatform(Family.WATCHOS, version)
        platforms.add(platform)
    }

    data class SupportedPlatform(
        @Internal val type: Family,
        @Input val version: String
    ) : Named {
        @Input
        override fun getName(): String = "${type.name} $version"
    }
}
