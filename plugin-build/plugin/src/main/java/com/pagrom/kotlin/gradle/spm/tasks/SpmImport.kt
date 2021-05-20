package com.pagrom.kotlin.gradle.spm.tasks

import com.pagrom.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import org.gradle.api.DefaultTask

abstract class SpmImport : DefaultTask() {
    init {
        description = "Add all Swift Package dependencies"
        group = KotlinSpmPlugin.TASK_GROUP
    }
}