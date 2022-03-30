package io.github.m1.liquibase.changegenerator.plugin

import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

/**
 * Represents a newChangeSet task
 *
 * @property changeSetName Input to change the default changeset name.
 * @property withTestdata Input to generate this changeset with testdata
 */
abstract class NewChangeSetTask : LiquibaseChangeTask() {
    init {
        /**
         * The gradle plugin task group
         */
        group = EXTENSION_NAME

        /**
         * The description for this gradle task
         */
        description = "Generates a liquibase changeset"
    }

    companion object {
        /**
         * The name of this gradle task
         */
        const val TASK_NAME: String = "newChangeSet"
    }

    /**
     * Optional input for the name of the changeset to generate
     */
    @Optional
    @set:Option(option = "changeSetName", description = "The name of this changeset")
    @get:Input
    var changeSetName: String? = "changeset_example"

    /**
     * Optional input to generate this changeset with testdata
     */
    @Optional
    @set:Option(option = "withTestdata", description = "Generates a testdata migration")
    @get:Input
    var withTestdata: Boolean? = false

    /**
     * The entrypoint for this task
     */
    @TaskAction
    @Suppress("MaxLineLength")
    fun action() {
        val file = File(changeLogFilePath)
        if (!file.exists()) {
            throw GradleException("no changelog master file exists at: $changeLogFilePath, run `gradle newChangeLog` to start a new project")
        }

        val changeLog = yamlMapper.readValue(file, ChangeLogMaster::class.java)
        val latestVersion = this.getLatestChangelogVersion(changeLog)
        val changeLogMigrationFile =
            File("${this.generateMigrationsFolderPath()}/$latestVersion/$latestVersion-changelog.yml")
        if (!changeLogMigrationFile.exists()) {
            throw GradleException("no changelog migration file exists at: $changeLogMigrationFile, run `gradle newChangeLog` to start a new project")
        }

        val changeLogMigration = yamlMapper.readValue(changeLogMigrationFile, ChangeLogMigration::class.java)
        val latestChangeSet = changeLogMigration.databaseChangeLog.last().changeSet.id
        val changeSetStrs = latestChangeSet.split("-").toTypedArray()
        val newChangeSetVersion = changeSetStrs[1].toInt() + 1
        val changeSetItem = defaultChangeSetItem(
            latestVersion,
            newChangeSetVersion,
            changeSetName!!
        )

        changeLogMigration.databaseChangeLog.add(ChangeSet(changeSetItem))

        if (withTestdata!!) {
            val changeSetTestdata = defaultChangeSetItem(
                latestVersion,
                newChangeSetVersion,
                changeSetName!!,
                testData = true
            )
            changeLogMigration.databaseChangeLog.add(ChangeSet(changeSetTestdata))
        }

        yamlMapper.writeValue(changeLogMigrationFile, changeLogMigration)
        logger.lifecycle("New changeset: ${this.padVersion(newChangeSetVersion)} written to $changeLogMigrationFile")

        this.writeMigration(latestVersion, newChangeSetVersion, changeSetName!!)
        this.writeRollback(latestVersion, newChangeSetVersion, changeSetName!!)

        if (withTestdata!!) {
            this.writeTestdataMigration(latestVersion, newChangeSetVersion, changeSetName!!)
        }
    }
}
