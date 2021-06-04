package com.github.pagr0m.kotlin.native.spm.entity.impl

import com.github.pagr0m.kotlin.native.spm.entity.SupportedProductMarker
import org.gradle.api.Named
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.jetbrains.kotlin.konan.target.Family

@SupportedProductMarker
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
