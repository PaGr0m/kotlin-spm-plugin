package org.zoldater.kotlin.gradle.spm

import org.gradle.api.Project
import org.jetbrains.kotlin.konan.target.Family
import java.io.File

class SwiftPackageBuildDirs(private val project: Project) {
    private val root: File
        get() = project.buildDir.resolve("swiftPackageManager")

    fun platformRoot(family: Family): File = root.resolve(family.name)

    fun packageSwiftFile(family: Family): File = platformRoot(family).resolve("Package.swift")

    fun packageResolvedFile(family: Family): File = platformRoot(family).resolve("Package.resolved")

    fun xcodeProjectFile(family: Family): File = platformRoot(family).resolve("${family}.xcodeproj")

    fun releaseDir(family: Family): File = platformRoot(family).resolve("build").resolve("Release")

    fun defsDir(family: Family): File = platformRoot(family).resolve("defs")
}

val Project.swiftPackageBuildDirs: SwiftPackageBuildDirs
    get() = SwiftPackageBuildDirs(this)