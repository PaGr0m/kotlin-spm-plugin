package org.zoldater.kotlin.gradle.spm

import java.io.File
import java.util.concurrent.TimeUnit

class SwiftPackageCLICommand {
    companion object {
        fun initializeProject(workingDir: File) {
            "swift package init".runCommand(workingDir)
        }

        fun generateXcodeProject(workingDir: File) {
            "swift package generate-xcodeproj".runCommand(workingDir)
        }

        fun generateFrameworks(workingDir: File, targetName: String) {
            "xcodebuild build -target $targetName".runCommand(workingDir)
        }

        private fun String.runCommand(workingDir: File) {
            ProcessBuilder(*split(" ").toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
                .waitFor(60, TimeUnit.MINUTES)
        }
    }
}