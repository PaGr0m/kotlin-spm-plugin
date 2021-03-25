package org.zoldater.kotlin.gradle.spm

class SwiftPackageCLICommand {
    companion object {
        const val INITIALIZE_SWIFT_PACKAGE_PROJECT = "swift package init"
        const val GENERATE_XCODE_PROJECT = "swift package generate-xcodeproj"
        const val BUILD_XCODE_PROJECT = "xcodebuild build"

        fun String.toCommand() = this.split(" ").toTypedArray()
    }
}