package com.pagrom.kotlin.gradle.spm.tasks

import com.pagrom.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import com.pagrom.kotlin.gradle.spm.swiftPackageBuildDirs
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.konan.target.Family

abstract class CleanSwiftPackageProjectTask : DefaultTask() {
    init {
        description = "Clean swift package project in build directory"
        group = KotlinSpmPlugin.TASK_GROUP
    }

    @Nested
    val platformFamily: Property<Family> = project.objects.property(Family::class.java)

    @TaskAction
    fun action() {
        project.swiftPackageBuildDirs.platformRoot(platformFamily.get()).deleteRecursively()
    }
}
