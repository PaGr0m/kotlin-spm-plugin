package org.zoldater.kotlin.gradle.spm.plugin

import org.gradle.testfixtures.ProjectBuilder

class SpmTasks {
    private val project = ProjectBuilder.builder().build()

    init {
        project.pluginManager.apply(PLUGIN_PACKAGE)
    }

    private companion object {
        private const val PLUGIN_PACKAGE = "org.zoldater.kotlin.gradle.spm.plugin"
    }
}
