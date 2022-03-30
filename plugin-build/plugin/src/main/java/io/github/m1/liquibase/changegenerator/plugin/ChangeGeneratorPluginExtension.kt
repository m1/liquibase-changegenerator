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

    /**
     * The root path for the migrations and master changelog
     */
    var rootPath: String = "./db"

    /**
     * The path for the liquibase master changelog
     */
    var changeLogFilePath: String = "./db/changelog.yml"

    /**
     * The name of the migrations folder
     */
    var migrationsFolder: String = "migrations"
}
