package org.zoldater.kotlin.gradle.spm.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

const val EXTENSION_NAME = "spm"
const val TASK_NAME = "spmExample"

abstract class KotlinSpmPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Add the 'template' extension object
        val spmExtension = project.extensions.create(EXTENSION_NAME, KotlinSpmExtension::class.java, project)

        // Add a task that uses configuration from the extension object
        registerDummyTask(project, spmExtension)
    }

    private fun registerDummyTask(
        project: Project,
        spmExtension: KotlinSpmExtension
    ) {
        project.tasks.register(TASK_NAME, KotlinSpmExampleTask::class.java) {
            it.doFirst {
                println("Begin")
                println("Dependencies: ${spmExtension.dependencies}")
                println("Products: ${spmExtension.products}")
            }
            it.doLast { println("End") }
        }
    }
}
