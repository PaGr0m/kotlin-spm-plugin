package org.zoldater.kotlin.gradle.spm.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class KotlinSpmExampleTask : DefaultTask() {

    init {
        description = "Just a sample template task"

        // Don't forget to set the group here.
        // group = BasePlugin.BUILD_GROUP
    }

    @TaskAction
    fun hello() {
        println("Hello future SPM plugin!")

    }
}
