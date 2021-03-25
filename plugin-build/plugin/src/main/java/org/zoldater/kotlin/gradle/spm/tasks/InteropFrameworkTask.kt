package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin

abstract class InteropFrameworkTask : DefaultTask() {

    init {
        description = ""
        group = KotlinSpmPlugin.TASK_GROUP
    }

    /*
        Headers from framework

     */

    @TaskAction
    fun action() {

    }
}