package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin

abstract class SpmGraphTask : DefaultTask() {

    init {
//        description = TODO()
        group = KotlinSpmPlugin.TASK_GROUP
    }

    @TaskAction
    fun action() {

    }
}
