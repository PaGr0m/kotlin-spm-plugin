package org.zoldater.kotlin.gradle.spm

import org.gradle.api.Project
import org.jetbrains.kotlin.konan.target.Family
import java.io.File

class SwiftPackageBuildDirs(private val project: Project) {
    val root: File
        get() = project.buildDir.resolve("swiftPackageManager")

    val swiftPackageFile: File
        get() = root.resolve("Package.swift")

    val release: File
        get() = root.resolve("build/Release")

    val def: File
        get() = root.resolve("def")

    fun pathToPlatformRoot(family: Family): File = root.resolve(family.name)
}

val Project.swiftPackageBuildDirs: SwiftPackageBuildDirs
    get() = SwiftPackageBuildDirs(this)