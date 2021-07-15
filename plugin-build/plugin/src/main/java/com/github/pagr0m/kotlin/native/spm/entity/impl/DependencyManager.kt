package com.github.pagr0m.kotlin.native.spm.entity.impl

import com.github.pagr0m.kotlin.native.spm.entity.DependencyMarker
import org.gradle.api.Named
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

@DependencyMarker
class DependencyManager {
    val dependencies = mutableListOf<Package>()

    fun packages(url: String, version: String, name: String) {
        val dependency = Package(url, version, name)
        dependencies.add(dependency)
    }

    fun packages(url: String, version: String) {
        val dependency = Package(url, version)
        dependencies.add(dependency)
    }

    fun packages(path: String) {
        val dependency = Package(path)
        dependencies.add(dependency)
    }

    /**
     * Version definitions based on:
     * https://docs.swift.org/package-manager/PackageDescription/PackageDescription.html
     */
    fun fromVersion(version: String) = "from: \"$version\""

    fun exactVersion(version: String) = ".exact(\"$version\")"

    fun versionRange(minVersion: String, maxVersion: String) = "\"$minVersion\"..<\"$maxVersion\""

    fun versionClosedRange(minVersion: String, maxVersion: String) = "\"$minVersion\"...\"$maxVersion\""

    fun upToNextMajor(version: String) = ".upToNextMajor(from: \"$version\")"

    fun upToNextMinor(version: String) = ".upToNextMinor(from: \"$version\")"

    fun branch(branchName: String) = ".branch(\"$branchName\")"

    fun revision(ref: String) = ".revision(\"$ref\")"

    data class Package(
        @Input val url: String,
        @Input @Optional val version: String? = null,
        @Input val dependencyName: String = url
            .subSequence(url.lastIndexOf("/") + 1, url.length - ".git".length)
            .toString(),
    ) : Named {
        override fun getName(): String = dependencyName

        fun convertToPackageContent(): String {
            return """
                .package(
                    name: "$dependencyName",
                    url: "$url",
                    $version
                )
            """.trimIndent()
        }
    }
}
