package io.github.m1.liquibase.changegenerator.plugin

import org.gradle.api.Project
import javax.inject.Inject

/**
 * Represents the plugin extension
 *
 * @property rootPath The root path
 * @property changeLogFilePath The path for the changelog
 * @property migrationsFolder The path for the migrations folder
 */
@Suppress("UnnecessaryAbstractClass")
abstract class ChangeGeneratorPluginExtension @Inject constructor(project: Project) {
    private val objects = project.objects

    var rootPath: String = "./db"
    var changeLogFilePath: String = "./db/changelog.yml"
    var migrationsFolder: String = "migrations"
}
