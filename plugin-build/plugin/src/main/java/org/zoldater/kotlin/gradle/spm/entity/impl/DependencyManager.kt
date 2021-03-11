package org.zoldater.kotlin.gradle.spm.entity.impl

import org.gradle.api.Named
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.zoldater.kotlin.gradle.spm.entity.impl.DependencyManager.Package

/**
 * @see [Package.Dependency](https://docs.swift.org/package-manager/PackageDescription/PackageDescription.html#package-dependency)
 * @see [Package.Dependency](https://github.com/apple/swift-package-manager/blob/main/Documentation/PackageDescription.md#package-dependency)
 */
class DependencyManager {
    val dependencies = mutableListOf<Package>()

    fun `package`(url: String, version: String, name: String) {
        val dependency = Package(url, version, name)
        dependencies.add(dependency)
    }

    fun `package`(url: String, version: String) {
        val dependency = Package(url, version)
        dependencies.add(dependency)
    }

    fun `package`(path: String) {
        val dependency = Package(path)
        dependencies.add(dependency)
    }

    data class Package(
        @Input val url: String,
        @Input @Optional val version: String? = null,
        @Input @Optional val dependencyName: String? = null,
    ) : Named {
        override fun getName(): String = url

        fun convertToPackageContent(): String {
            return """
                .package(
                    name: "$dependencyName",
                    url: "$url",
                    from: "$version"
                )
            """.trimIndent()
        }
    }
}
