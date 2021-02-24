package org.zoldater.kotlin.gradle.spm.plugin

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class KotlinSpmPluginTest {

    @Test
    fun `plugin is applied correctly to the project`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.zoldater.kotlin.gradle.spm.plugin")

        assert(project.tasks.getByName("spmExample") is KotlinSpmExampleTask)
    }
}
