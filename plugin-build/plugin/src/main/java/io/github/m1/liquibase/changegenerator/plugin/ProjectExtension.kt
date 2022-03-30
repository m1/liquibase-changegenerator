package io.github.m1.liquibase.changegenerator.plugin

import org.gradle.api.Project

/**
 * Inject the ChangeGeneratorPluginExtension into the gradle Project
 *
 * @see ChangeGeneratorPluginExtension
 * @see Project
 */
val Project.changeGeneratorPluginExtension: ChangeGeneratorPluginExtension
    get() = extensions.getByType(ChangeGeneratorPluginExtension::class.java)
