package io.github.m1.liquibase.changegenerator.plugin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.KotlinModule
import net.swiftzer.semver.SemVer
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import java.io.File

@Suppress("MaxLineLength")
abstract class LiquibaseChangeTask : DefaultTask() {
    companion object {
        const val PAD_LENGTH: Int = 3
        const val CHANGELOG_VERSION_POSITION: Int = 2
    }

    @get:Internal
    protected val pathExtension: ChangeGeneratorPluginExtension = project.changeGeneratorPluginExtension

    @get:Input
    val changeLogFilePath: String = pathExtension.changeLogFilePath

    @get:Input
    var migrationsFolder: String = pathExtension.migrationsFolder

    @get:Input
    var rootPath: String = pathExtension.rootPath

    @Input
    var yamlMapper: ObjectMapper =
        ObjectMapper(YAMLFactory().configure(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR, true)).registerModule(
            KotlinModule.Builder().build()
        )

    /**
     * Creates a new changeset item with sensible defaults
     *
     * @param changeLogVersion The version of the changeLog
     * @param changeSetVersion The version of the changeSet
     * @param migrationName The name of the migration
     * @param testData Contains whether this changeset is a testdata changeset
     *
     * @return The new changeSet item
     */
    fun defaultChangeSetItem(
        changeLogVersion: SemVer,
        changeSetVersion: Int = 0,
        migrationName: String,
        testData: Boolean = false,
    ): ChangeSetItem {
        val paddedChangeSetVersion = this.padVersion(changeSetVersion)
        val sqlFile =
            "$migrationsFolder/$changeLogVersion/$changeLogVersion-${paddedChangeSetVersion}__$migrationName.sql"
        val rollBack =
            "$migrationsFolder/$changeLogVersion/rollbacks/$changeLogVersion-${paddedChangeSetVersion}__$migrationName.sql"
        val changeSet = ChangeSetItem(
            id = "$changeLogVersion-$paddedChangeSetVersion",
            author = "FirstName LastName <firstname.lastname@arabesque.com>",
            context = "master",
            labels = mutableListOf("example_changeset", "remove_these"),
            comment = "This is an example migration",
            logicalFilePath = "$changeLogVersion-${paddedChangeSetVersion}__$migrationName.sql",
            changes = listOf(
                ChangeSetChanges(sqlFile = ChangeSetChangesSQLFilePath(path = sqlFile)),
                ChangeSetChanges(tagDatabase = ChangeSetChangesTagDatabase(tag = changeLogVersion.toString())),
            ),
            rollback = listOf(
                ChangeSetChanges(sqlFile = ChangeSetChangesSQLFilePath(path = rollBack)),
            )
        )

        if (testData) {
            changeSet.id = "${changeSet.id}-testdata"
            changeSet.logicalFilePath = "$changeLogVersion-$paddedChangeSetVersion-testdata__$migrationName.sql"
            changeSet.context = "testdata"
            changeSet.labels.add("testdata")
            changeSet.comment = "This is an example testdata migration"
            changeSet.changes = listOf(
                ChangeSetChanges(sqlFile = ChangeSetChangesSQLFilePath(path = "$migrationsFolder/$changeLogVersion/testdata/$changeLogVersion-${paddedChangeSetVersion}__$migrationName.sql")),
                ChangeSetChanges(tagDatabase = ChangeSetChangesTagDatabase(tag = changeLogVersion.toString())),
            )
            changeSet.rollback = listOf()
        }

        return changeSet
    }

    /**
     * Pads a single unit version with 0s
     *
     * @param version A single unit version
     *
     * @return The zero padded version
     */
    fun padVersion(version: Int): String {
        return version.toString().padStart(PAD_LENGTH, '0')
    }

    /**
     * Gets the latest changelog version
     *
     * @param changeLog The changelog to scan for the latest change version.
     *
     * @return The version of the latest change
     */
    fun getLatestChangelogVersion(changeLog: ChangeLogMaster): SemVer {
        val latestFile = changeLog.databaseChangeLog.last().include.file
        val strs = latestFile.split("/").toTypedArray()

        return SemVer.parse(strs[CHANGELOG_VERSION_POSITION].split("-")[0])
    }

    /**
     * Returns the generated migration folder path.
     *
     * @return The generated migrations folder path
     */
    fun generateMigrationsFolderPath(): String {
        return "$rootPath/$migrationsFolder"
    }

    /**
     * Writes a migration file
     *
     * @param version The version of the changelog
     * @param changeVersion The version of the changeset
     * @param changeName The name of the changeset
     */
    fun writeMigration(version: SemVer, changeVersion: Int = 1, changeName: String = "changeset_example") {
        val migration =
            File("${this.generateMigrationsFolderPath()}/$version/$version-${this.padVersion(changeVersion)}__$changeName.sql")
        migration.parentFile.mkdirs()
        migration.writeText("-- This is an auto-generated example migration, please remove this.\nCREATE TABLE example_table \n(\n\thello_world VARCHAR(100)\n);")
        logger.lifecycle("Migration: $migration created")
    }

    /**
     * Writes a testdata migration file
     *
     * @param version The version of the changelog
     * @param changeVersion The version of the changeset
     * @param changeName The name of the changeset
     */
    fun writeTestdataMigration(version: SemVer, changeVersion: Int = 1, changeName: String = "changeset_example") {
        val migration =
            File("${this.generateMigrationsFolderPath()}/$version/testdata/$version-${this.padVersion(changeVersion)}__$changeName.sql")
        migration.parentFile.mkdirs()
        migration.writeText("-- This is an auto-generated example testdata insert, please remove this.\nINSERT INTO example_table (hello_world) \nVALUES ('hello_world');")
        logger.lifecycle("Testdata migration: $migration created")
    }

    /**
     * Writes a rollback file
     *
     * @param version The version of the changelog
     * @param changeVersion The version of the changeset
     * @param changeName The name of the changeset
     */
    fun writeRollback(version: SemVer, changeVersion: Int = 1, changeName: String = "changeset_example") {
        val rollbackExample =
            File("${this.generateMigrationsFolderPath()}/$version/rollbacks/$version-${this.padVersion(changeVersion)}__$changeName.sql")
        rollbackExample.parentFile.mkdirs()
        rollbackExample.writeText("-- This is an auto-generated example rollback, please remove this.\nDROP TABLE example_table;")
        logger.lifecycle("Rollback: $rollbackExample created")
    }
}
