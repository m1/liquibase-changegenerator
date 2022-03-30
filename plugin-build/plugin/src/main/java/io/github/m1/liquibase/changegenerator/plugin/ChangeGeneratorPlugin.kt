package io.github.m1.liquibase.changegenerator.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

const val EXTENSION_NAME = "liquibaseChangeGenerator"

abstract class ChangeGeneratorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(EXTENSION_NAME, ChangeGeneratorPluginExtension::class.java)
        project.tasks.register(NewChangeLogTask.TASK_NAME, NewChangeLogTask::class.java)
        project.tasks.register(NewChangeSetTask.TASK_NAME, NewChangeSetTask::class.java)
    }
}
