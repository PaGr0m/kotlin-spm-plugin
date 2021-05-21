package com.github.pagr0m.kotlin.native.spm.tasks

import com.github.pagr0m.kotlin.native.spm.plugin.KotlinSpmPlugin
import com.github.pagr0m.kotlin.native.spm.swiftPackageBuildDirs
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.bundling.Zip
import java.io.File

@CacheableTask
abstract class ArchiveXCFramework : Zip() {
    @InputDirectory
    @PathSensitive(PathSensitivity.ABSOLUTE)
    val xcFramework: Property<File> = project.objects.property(File::class.java)

    init {
        description = "Creating an archive for the XCFramework"
        group = KotlinSpmPlugin.TASK_GROUP

        archiveBaseName.set(xcFramework.map { it.name })
        destinationDirectory.set(project.swiftPackageBuildDirs.xcArchiveDir())
    }
}
