package io.github.m1.liquibase.changegenerator.plugin

import net.swiftzer.semver.SemVer
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

/**
 * Represents a newChangeLog task
 *
 * @property releaseVersion Input to release a specific version
 * @property releaseMajor Input to release a new major version
 * @property releaseMinor Input to release a new minor version
 * @property releasePatch Input to release a new patch version
 * @property logger The class logger
 */
@Suppress("MaxLineLength")
abstract class NewChangeLogTask : LiquibaseChangeTask() {
    init {
        description = "Just a sample template task"
        group = EXTENSION_NAME
    }

    companion object {
        const val TASK_NAME: String = "newChangeLog"
    }

    @Optional
    @set:Option(option = "releaseVersion", description = "The new version to release")
    @get:Input
    var releaseVersion: String? = null

    @Optional
    @set:Option(option = "releaseMajor", description = "Bumps the major version")
    @get:Input
    var releaseMajor: Boolean? = null

    @Optional
    @set:Option(option = "releaseMinor", description = "Bumps the minor version")
    @get:Input
    var releaseMinor: Boolean? = null

    @Optional
    @set:Option(option = "releasePatch", description = "Bumps the patch version")
    @get:Input
    var releasePatch: Boolean? = null

    /**
     * Entrypoint for this task
     */
    @TaskAction
    fun action() {
        println(releaseVersion)
        return
        // val changeLogVersion = this.generateChangelogVersion()
        //
        // this.generateTemplates(changeLogVersion)
    }

    /**
     * Generates a new changelog version.
     *
     * @throws IllegalArgumentException If invalid version passed
     *
     * @return The new changeLog version
     */
    @Suppress("UnusedPrivateMember")
    private fun generateChangelogVersion(): SemVer {
        var releaseVersionSemver: SemVer? = null
        if (releaseVersion != null) {
            releaseVersionSemver = SemVer.parse(releaseVersion!!)
        }

        val file = File(changeLogFile)
        if (file.exists()) {
            return this.generateChangeLogVersionExists(file, releaseVersionSemver)
        }

        return this.generateChangeLogVersionNoExists(file, releaseVersionSemver)
    }

    /**
     * Generates a new changelog version if a master changeLog exists
     *
     * @param file The master changelog
     * @param version The release version
     *
     * @throws IllegalArgumentException If invalid version passed
     *
     * @return The new changeLog version
     */
    private fun generateChangeLogVersionExists(file: File, version: SemVer?): SemVer {
        val changeLog = this.yamlMapper.readValue(file, ChangeLogMaster::class.java)
        val latestVersion = this.getLatestChangelogVersion(changeLog)

        val releaseIsNull = releaseVersion == null &&
            releaseMajor == null &&
            releaseMinor == null &&
            releasePatch == null
        if (releaseIsNull) {
            logger.warn("no input variables set for release, pinning as patch release")
            releasePatch = true
        }

        val newVersion = if (version != null && version <= latestVersion) {
            throw IllegalArgumentException("releaseVersion must be greater than the version in the master changelog.yml")
        } else if (version == null && (releaseMajor != null && releaseMajor!!)) {
            SemVer(major = latestVersion.major + 1, minor = 0, patch = 0)
        } else if (version == null && (releaseMinor != null && releaseMinor!!)) {
            SemVer(major = latestVersion.major, minor = latestVersion.minor + 1, patch = 0)
        } else {
            SemVer(major = latestVersion.major, minor = latestVersion.minor, patch = latestVersion.patch + 1)
        }

        logger.info("old version: $latestVersion, new version: $newVersion")
        val newChangeLogStr = "$migrationsFolder/$newVersion/$newVersion-changelog.yml"

        changeLog.databaseChangeLog.add(ChangeLogItem(IncludeItem(file = newChangeLogStr)))

        this.yamlMapper.writeValue(file, changeLog)
        logger.info("Wrote master changelog file: file")

        return newVersion
    }

    /**
     * Generates a new changelog version if no master changeLog exists
     *
     * @param file The master changelog
     * @param version The release version
     *
     * @throws IllegalArgumentException If invalid version passed
     *
     * @return The new changeLog version
     */
    private fun generateChangeLogVersionNoExists(file: File, version: SemVer?): SemVer {
        var newVersion: SemVer? = version
        file.parentFile.mkdirs()
        if (newVersion == null) {
            newVersion = SemVer(major = 0, minor = 0, patch = 1)
        }
        logger.warn("No changelog found, creating new version: $newVersion")
        val newChangeLog = "$migrationsFolder/$newVersion/$newVersion-changelog.yml"
        val changeLog = ChangeLogMaster(
            databaseChangeLog = arrayListOf(
                ChangeLogItem(IncludeItem(file = newChangeLog))
            )
        )
        this.yamlMapper.writeValue(file, changeLog)
        logger.info("Wrote master changelog file: file")

        return newVersion
    }

    /**
     * Generates the changelog templates.
     *
     * @param changeLogVersion The version of the changelog
     */
    @Suppress("UnusedPrivateMember")
    private fun generateTemplates(changeLogVersion: SemVer) {
        val migrationFolderVersion = File("$migrationsFolder/$changeLogVersion")
        logger.info("New migration folder: $migrationFolderVersion")

        if (migrationFolderVersion.exists()) {
            throw GradleException("new migration folder: $migrationFolderVersion already exists")
        }

        migrationFolderVersion.parentFile.mkdirs()
        migrationFolderVersion.mkdirs()

        this.writeMigration(changeLogVersion)
        this.writeRollback(changeLogVersion)
        this.writeTestdataMigration(changeLogVersion)

        val changeSetMigration = defaultChangeSetItem(changeLogVersion, 1, "changeset_example")
        val changeSetTestdata = defaultChangeSetItem(changeLogVersion, 1, "changeset_example", testData = true)
        val changeSetFile = File("$migrationsFolder/$changeLogVersion/$changeLogVersion-changelog.yml")

        val changeLogVersioned = ChangeLogMigration(
            databaseChangeLog = mutableListOf(
                ChangeSet(changeSet = changeSetMigration),
                ChangeSet(changeSet = changeSetTestdata)
            )
        )
        this.yamlMapper.writeValue(changeSetFile, changeLogVersioned)
    }
}
