package io.github.m1.liquibase.changegenerator.plugin

import org.gradle.api.Project

val Project.changeGeneratorPluginExtension: ChangeGeneratorPluginExtension
    get() = extensions.getByType(ChangeGeneratorPluginExtension::class.java)
