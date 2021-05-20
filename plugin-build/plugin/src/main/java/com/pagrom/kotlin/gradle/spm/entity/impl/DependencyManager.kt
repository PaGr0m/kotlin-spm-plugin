package com.pagrom.kotlin.gradle.spm.entity.impl

import com.pagrom.kotlin.gradle.spm.entity.DependencyMarker
import com.pagrom.kotlin.gradle.spm.entity.impl.DependencyManager.Package
import org.gradle.api.Named
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

@DependencyMarker
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
        @Input val dependencyName: String = url,
    ) : Named {
        override fun getName(): String = dependencyName

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
