package io.github.m1.liquibase.changegenerator.plugin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.KotlinModule
import net.swiftzer.semver.SemVer
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import java.io.File

@Suppress("MaxLineLength")
abstract class LiquibaseChangeTask : DefaultTask() {
    companion object {
        const val PAD_LENGTH: Int = 3
        const val CHANGELOG_VERSION_POSITION: Int = 3
    }

    @Input
    var changeLogFile: String = ""

    @Input
    var migrationsFolder: String = ""

    @Input
    var yamlMapper: ObjectMapper = ObjectMapper(
        YAMLFactory().configure(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR, true)
    ).registerModule(KotlinModule.Builder().build())

    // @get:Input
    // @get:Option(option = "message", description = "A message to be printed in the output file")
    // abstract val message: Property<String>
    //
    // @get:Input
    // @get:Option(option = "tag", description = "A Tag to be used for debug and in the output file")
    // @get:Optional
    // abstract val tag: Property<String>
    //
    // @get:OutputFile
    // abstract val outputFile: RegularFileProperty

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
            changeSet.rollback = listOf(
                ChangeSetChanges(sqlFile = ChangeSetChangesSQLFilePath(path = "$migrationsFolder/$changeLogVersion/testdata/rollbacks/$changeLogVersion-${paddedChangeSetVersion}__$migrationName.sql")),
            )
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
    private fun padVersion(version: Int): String {
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

        return SemVer.parse(strs[CHANGELOG_VERSION_POSITION])
    }

    /**
     * Writes a migration file
     *
     * @param version The version of the changelog
     * @param changeSetVersion The version of the changeset
     * @param changeSetName The name of the changeset
     */
    fun writeMigration(version: SemVer, changeSetVersion: Int = 1, changeSetName: String = "changeset_example") {
        val migration =
            File("$migrationsFolder/$version/$version-${this.padVersion(changeSetVersion)}__$changeSetName.sql")
        migration.writeText("-- This is an auto-generated example migration, please remove this.\nCREATE TABLE example_table \n(\n\thello_world VARCHAR(100)\n);")
        logger.info("migration: $migration created")
    }

    /**
     * Writes a testdata migration file
     *
     * @param version The version of the changelog
     * @param changeSetVersion The version of the changeset
     * @param changeSetName The name of the changeset
     */
    fun writeTestdataMigration(
        version: SemVer,
        changeSetVersion: Int = 1,
        changeSetName: String = "changeset_example",
    ) {
        val migration =
            File("$migrationsFolder/$version/testdata/$version-${this.padVersion(changeSetVersion)}__$changeSetName.sql")
        migration.parentFile.mkdirs()
        migration.writeText("-- This is an auto-generated example testdata insert, please remove this.\nINSERT INTO example_table (hello_world) \nVALUES ('hello_world');")
        logger.info("testdata migration: $migration created")

        val rollbackExample =
            File("$migrationsFolder/$version/testdata/rollbacks/$version-${this.padVersion(changeSetVersion)}__$changeSetName.sql")
        rollbackExample.parentFile.mkdirs()
        rollbackExample.writeText("-- This is an auto-generated example rollback, please remove this.\nTRUNCATE TABLE example_table;")
        logger.info("testdata rollback: $rollbackExample created")
    }

    /**
     * Writes a rollback file
     *
     * @param version The version of the changelog
     * @param changeSetVersion The version of the changeset
     * @param changeSetName The name of the changeset
     */
    fun writeRollback(version: SemVer, changeSetVersion: Int = 1, changeSetName: String = "changeset_example") {
        val rollbackExample =
            File("$migrationsFolder/$version/rollbacks/$version-${this.padVersion(changeSetVersion)}__$changeSetName.sql")
        rollbackExample.parentFile.mkdirs()
        rollbackExample.writeText("-- This is an auto-generated example rollback, please remove this.\nDROP TABLE example_table;")
        logger.info("rollback: $rollbackExample created")
    }
}
