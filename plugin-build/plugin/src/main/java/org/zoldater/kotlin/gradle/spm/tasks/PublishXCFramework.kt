package org.zoldater.kotlin.gradle.spm.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.org.eclipse.jgit.api.Git
import org.gradle.internal.impldep.org.eclipse.jgit.api.ListBranchCommand.ListMode
import org.gradle.internal.impldep.org.eclipse.jgit.lib.Ref
import org.zoldater.kotlin.gradle.spm.plugin.KotlinSpmPlugin
import java.io.File


abstract class PublishXCFramework : DefaultTask() {

    init {
        group = KotlinSpmPlugin.TASK_GROUP
        // TODO:
    }

    private val swiftPackageTemplateContent = this::class.java.getResource("data/publish-xcframework").toURI()

    @TaskAction
    fun action() {
        val gitDir = File(swiftPackageTemplateContent)

        Git.open(gitDir).use { git ->
            var call: List<Ref> = git.branchList().call()
            for (ref in call) {
                println("Branch: " + ref + " " + ref.name + " " + ref.objectId.name)
            }

            println("Now including remote branches:")
            call = git.branchList().setListMode(ListMode.ALL).call()
            for (ref in call) {
                println("Branch: " + ref + " " + ref.name + " " + ref.objectId.name)
            }
        }
    }
}