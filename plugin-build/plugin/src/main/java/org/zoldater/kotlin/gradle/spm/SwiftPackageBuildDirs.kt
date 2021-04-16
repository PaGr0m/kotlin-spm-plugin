package org.zoldater.kotlin.gradle.spm

import org.gradle.api.Project
import org.jetbrains.kotlin.konan.target.Family
import java.io.File

class SwiftPackageBuildDirs(private val project: Project) {
    val root: File
        get() = project.buildDir.resolve(ROOT_DIRECTORY)

    fun platformRoot(family: Family): File = root.resolve(family.name)

    fun packageSwiftFile(family: Family): File = platformRoot(family).resolve(PACKAGE_SWIFT_FILE)

    fun packageResolvedFile(family: Family): File = platformRoot(family).resolve(PACKAGE_SWIFT_RESOLVED_FILE)

    fun xcodeProjectFile(family: Family): File = platformRoot(family).resolve("${family}.$XCODEPROJECT_EXTENSION")

    fun releaseDir(family: Family): File = platformRoot(family).resolve(BUILD_DIRECTORY).resolve(RELEASE_DIRECTORY)

    fun defsDir(family: Family): File = platformRoot(family).resolve(DEF_DIRECTORY)

    companion object {
        const val ROOT_DIRECTORY = "swiftPackageManager"
        const val RELEASE_DIRECTORY = "Release"
        const val BUILD_DIRECTORY = "build"
        const val DEF_DIRECTORY = "defs"

        const val PACKAGE_SWIFT_FILE = "Package.swift"
        const val PACKAGE_SWIFT_RESOLVED_FILE = "Package.resolved"

        const val XCODEPROJECT_EXTENSION = "xcodeproj"
    }
}

val Project.swiftPackageBuildDirs: SwiftPackageBuildDirs
    get() = SwiftPackageBuildDirs(this)