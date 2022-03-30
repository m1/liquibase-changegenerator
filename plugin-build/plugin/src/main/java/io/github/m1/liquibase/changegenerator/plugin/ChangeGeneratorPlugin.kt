package io.github.m1.liquibase.changegenerator.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * The global name of this extension
 */
const val EXTENSION_NAME = "liquibaseChangeGenerator"

/**
 * The class for this plugin, implements the tasks and the base extension
 *
 * @see ChangeGeneratorPlugin
 * @see NewChangeLogTask
 * @see NewChangeSetTask
 */
abstract class ChangeGeneratorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(EXTENSION_NAME, ChangeGeneratorPluginExtension::class.java)
        project.tasks.register(NewChangeLogTask.TASK_NAME, NewChangeLogTask::class.java)
        project.tasks.register(NewChangeSetTask.TASK_NAME, NewChangeSetTask::class.java)
    }
}
