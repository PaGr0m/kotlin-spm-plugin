package org.zoldater.kotlin.gradle.spm

import org.gradle.api.Project
import java.io.File

internal val Project.swiftPackageBuildDirs: SwiftPackageBuildDirs
    get() = SwiftPackageBuildDirs(this)

class SwiftPackageBuildDirs(val project: Project) {
    private val buildDir: File = project.buildDir

    val root: File
        get() = buildDir.resolve(DIRECTORY_SWIFT_PACKAGE)

    val framework: File
        get() = root.resolve(DIRECTORY_FRAMEWORK)

    val defs: File
        get() = root.resolve(DIRECTORY_DEF)

    companion object {
        private const val DIRECTORY_SWIFT_PACKAGE = "swift-package-manager"
        private const val DIRECTORY_FRAMEWORK = "framework"
        private const val DIRECTORY_DEF = "defs"
    }
}