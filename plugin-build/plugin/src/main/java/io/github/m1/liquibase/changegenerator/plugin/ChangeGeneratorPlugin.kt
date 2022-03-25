package io.github.m1.liquibase.changegenerator.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

const val EXTENSION_NAME = "liquibaseChangeGenerator"

abstract class ChangeGeneratorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Add the 'template' extension object
        project.extensions.create(EXTENSION_NAME, ChangeGeneratorPlugin::class.java)
        // Add a task that uses configuration from the extension object
        project.tasks.register(NewChangeLogTask.TASK_NAME, NewChangeLogTask::class.java) {
            println("testing")
            // it.tag.set(extension.tag)
            // it.message.set(extension.message)
            // it.outputFile.set(extension.outputFile)
        }
    }
}
