package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.bundling.Zip
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import org.zoldater.kotlin.gradle.spm.swiftPackageBuildDirs
import java.io.File

@CacheableTask
abstract class ArchiveXCFramework : Zip() {
    @InputFile
    val xcFramework: Provider<File> = project.objects.property(File::class.java)

    init {
        description = ""
        group = KotlinSpmPlugin.TASK_GROUP

        archiveFileName.set(xcFramework.map { "${it.name}.zip" })
        destinationDirectory.set(project.swiftPackageBuildDirs.root)
    }
}