package org.zoldater.kotlin.gradle.spm

import org.gradle.api.Project
import org.jetbrains.kotlin.konan.target.Family
import java.io.File

class SwiftPackageBuildDirs(
    private val project: Project,
    val family: Family
) {
    val platformName = "platform${family}"

    val root: File
        get() = project.buildDir.resolve(platformName)

    val swiftPackageFile: File
        get() = root.resolve("Package.swift")

    val release: File
        get() = root.resolve("build/Release")

    val def: File
        get() = root.resolve("def")
}