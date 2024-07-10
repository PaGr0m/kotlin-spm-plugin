package com.github.pagr0m.kotlin.native.spm.tasks

import com.github.pagr0m.kotlin.native.spm.plugin.KotlinSpmPlugin
import com.github.pagr0m.kotlin.native.spm.swiftPackageBuildDirs

import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.jetbrains.kotlin.konan.target.Family
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@CacheableTask
abstract class BuildFrameworksTask : DefaultTask() {

    init {
        /**
         * Task description: Packages headers, modules, and binaries into a Framework.
         */
        description =
            "Build and assemble a framework from the Swift package, including headers and modules."
        group = KotlinSpmPlugin.TASK_GROUP
    }

    @Input
    val platformFamily: Property<Family> = project.objects.property(Family::class.java)

    @Input
    val platformDependencies: ListProperty<String> = project.objects.listProperty(String::class.java)

    @get:OutputDirectory
    val outputFrameworkDirectory: Provider<File>
        get() = platformFamily.map {
            project.swiftPackageBuildDirs.releaseDir(it)
                .resolve("Output")
        }

    @TaskAction
    fun buildXCFramework() {
        val configuration = "Release"
        val sdk = platformFamily.get().toSdk()

        buildFramework(configuration, sdk)
        platformDependencies.get().forEach { dependency ->
            copyBinary(dependency, configuration, sdk)
            copySwiftModules(dependency, configuration)
            copyHeaders(dependency, configuration)
            copyResources(dependency)
            createSymbolicLinks(dependency)
        }
    }

    private fun buildFramework(configuration: String, sdk: String) {
        val packageDirectory = project.swiftPackageBuildDirs.platformRoot(platformFamily.get())
        val family = platformFamily.get()
        val familyPlatform = platformFamily.get().toPlatform()
        val outputDirectory = outputFrameworkDirectory.get()

        project.exec {
            it.workingDir = packageDirectory
            it.commandLine(
                "xcodebuild", "build",
                "-scheme", family,
                "-destination", "generic/platform=$familyPlatform",
                "-sdk", sdk,
                "-configuration", configuration,
                "-derivedDataPath", project.swiftPackageBuildDirs.derivedDataPath(),
                "-quiet",
                "SKIP_INSTALL=NO",
                "BUILD_LIBRARY_FOR_DISTRIBUTION=YES",
                "OTHER_SWIFT_FLAGS=-no-verify-emitted-module-interface",
                "SYMROOT=${outputDirectory}"
            )
        }
    }

    private fun copyBinary(dependency: String, configuration: String, sdk: String) {
        val binaryPath = project.swiftPackageBuildDirs.releaseDir(platformFamily.get())
            .resolve("Output/Release-iphonesimulator/${dependency}.o")
        val outputBinaryDir = project.swiftPackageBuildDirs.releaseDir(platformFamily.get())
            .resolve("${dependency}.framework/Versions/A")
        outputBinaryDir.mkdirs()

        if (binaryPath.exists()) {
            binaryPath.copyTo(File(outputBinaryDir, dependency), overwrite = true)
        } else {
            // TODO: Check if SPM package contained a precompiled .framework i.e `GoogleCast.framework`
            throw RuntimeException("Binary not found at ${binaryPath.path}")
        }
    }

    private fun copySwiftModules(dependency: String, configuration: String) {
        val releaseDir = project.swiftPackageBuildDirs.releaseDir(platformFamily.get())
            .resolve("Output/Release-iphonesimulator")
        val swiftmoduleName = "${dependency}.swiftmodule"
        val swiftmodulePath = findSwiftModuleDirectory(releaseDir.toPath(), swiftmoduleName)

        val outputModulesDir = project.swiftPackageBuildDirs.releaseDir(platformFamily.get())
            .resolve("${dependency}.framework/Versions/A/Modules")
        outputModulesDir.mkdirs()

        if (swiftmodulePath != null) {
            swiftmodulePath.toFile().copyRecursively(outputModulesDir.resolve(swiftmoduleName), overwrite = true)
        } else {
            createModuleMap(outputModulesDir.toPath(), dependency)
        }
    }

    private fun createModuleMap(outputModulesDir: Path, dependency: String) {
        val moduleMapContent = """
            framework module $dependency {
                umbrella "Headers"
                export *
                module * { export * }
            }
        """.trimIndent()

        val moduleMapFile = outputModulesDir.resolve("module.modulemap").toFile()
        moduleMapFile.writeText(moduleMapContent)
    }

    private fun copyHeaders(dependency: String, configuration: String) {
        val headerPath = project.swiftPackageBuildDirs.derivedDataPath() // TODO: Use `toReleaseSdk`:
            .resolve("Build/Intermediates.noindex/${dependency}.build/${configuration}-iphonesimulator/${dependency}.build/Objects-normal/arm64/${dependency}-Swift.h")
        val outputHeadersDir = project.swiftPackageBuildDirs.releaseDir(platformFamily.get())
            .resolve("${dependency}.framework/Versions/A/Headers")
        outputHeadersDir.mkdirs()

        if (headerPath.exists()) {
            headerPath.copyTo(File(outputHeadersDir, "${dependency}-Swift.h"), overwrite = true)
        } else {
            project.swiftPackageBuildDirs.derivedDataPath() // TODO: Support passing `packageName`
                .resolve("SourcePackages/checkouts/${dependency}/${dependency}")
                .walkTopDown()
                .filter { it.isFile && it.extension == "h" }
                .forEach { headerFile ->
                    val targetFile = outputHeadersDir.resolve(headerFile.name)
                    headerFile.copyTo(targetFile, overwrite = true)
                }
        }
    }

    private fun findSwiftModuleDirectory(basePath: Path, moduleName: String): Path? {
        return Files.walk(basePath)
            .filter { Files.isDirectory(it) && it.fileName.toString() == moduleName }
            .findFirst()
            .orElse(null)
    }

    private fun copyResources(dependency: String) {
        // This function is left as a placeholder since the original logic did not provide specifics.
        // Adjust the implementation here if you need to copy specific resources.
        val derivedDataPath = project.swiftPackageBuildDirs.derivedDataPath()
        val family = platformFamily.get()
        val bundleDir = File(derivedDataPath, "${family}_${family}.bundle")
        if (bundleDir.exists()) {
            bundleDir.copyRecursively(
                project.swiftPackageBuildDirs.releaseDir(platformFamily.get())
                    .resolve("${dependency}.framework"),
                overwrite = true
            )
        }
    }

    private fun createSymbolicLinks(dependency: String) {
        val baseDir = project.swiftPackageBuildDirs.releaseDir(platformFamily.get())
            .resolve("${dependency}.framework/Versions")
        val currentLink = baseDir.resolve("Current")
        val aDir = baseDir.resolve("A")

        Files.createSymbolicLink(currentLink.toPath(), aDir.toPath())

        val frameworkDir = project.swiftPackageBuildDirs.releaseDir(platformFamily.get())
            .resolve("${dependency}.framework")
        val binaryLink = frameworkDir.resolve(dependency)
        val headersLink = frameworkDir.resolve("Headers")
        val modulesLink = frameworkDir.resolve("Modules")
        val resourcesLink = frameworkDir.resolve("Resources")

        Files.createSymbolicLink(binaryLink.toPath(), aDir.resolve(dependency).toPath())
        Files.createSymbolicLink(headersLink.toPath(), aDir.resolve("Headers").toPath())
        Files.createSymbolicLink(modulesLink.toPath(), aDir.resolve("Modules").toPath())
        Files.createSymbolicLink(resourcesLink.toPath(), aDir.resolve("Resources").toPath())
    }

    private fun Family.toSdk(): String {
        return when (this) {
            Family.OSX -> "macosx"
            Family.IOS -> "iphonesimulator"
            else -> ""
        }
    }

    private fun Family.toPlatform(): String {
        return when (this) {
            Family.OSX -> "Mac"
            Family.IOS -> "iOS Simulator"
            else -> "Unknown"
        }
    }
}
