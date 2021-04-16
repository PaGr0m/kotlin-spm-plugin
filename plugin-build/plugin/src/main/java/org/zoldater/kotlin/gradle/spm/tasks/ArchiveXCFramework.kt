package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.bundling.Zip
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import org.zoldater.kotlin.gradle.spm.swiftPackageBuildDirs
import java.io.File

//@CacheableTask
abstract class ArchiveXCFramework : Zip() {
    @InputDirectory
    val xcFramework: Property<File> = project.objects.property(File::class.java)

//    @InputDirectory
//    val xcFramework: File = project.swiftPackageBuildDirs.xcFrameworkDir()
//        .resolve("all.xcframework")

    init {
        description = ""
        group = KotlinSpmPlugin.TASK_GROUP

        archiveBaseName.set(xcFramework.map { it.name })
        destinationDirectory.set(project.swiftPackageBuildDirs.xcArchiveDir())

//        archiveFileName.set(xcFramework.map { "${it.name}.zip" })
//        archiveFileName.set()


//        destinationDirectory.set(project.swiftPackageBuildDirs.xcArchiveDir())
    }
}