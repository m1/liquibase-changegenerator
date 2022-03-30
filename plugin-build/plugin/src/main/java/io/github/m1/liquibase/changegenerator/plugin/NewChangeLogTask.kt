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
        /**
         * The name of this gradle task group
         */
        group = EXTENSION_NAME

        /**
         * The description for this gradle task
         */
        description = "Generates a liquibase changelog"
    }

    companion object {
        /**
         * The name of this gradle task
         */
        const val TASK_NAME: String = "newChangeLog"
    }

    /**
     * Input to release a specific version
     */
    @Optional
    @set:Option(option = "releaseVersion", description = "The new version to release")
    @get:Input
    var releaseVersion: String? = null

    /**
     * Input to release a new major version
     */
    @Optional
    @set:Option(option = "releaseMajor", description = "Bumps the major version")
    @get:Input
    var releaseMajor: Boolean? = null

    /**
     * Input to release a new minor version
     */
    @Optional
    @set:Option(option = "releaseMinor", description = "Bumps the minor version")
    @get:Input
    var releaseMinor: Boolean? = null

    /**
     * Input to release a new patch version
     */
    @Optional
    @set:Option(option = "releasePatch", description = "Bumps the patch version")
    @get:Input
    var releasePatch: Boolean? = null

    /**
     * Entrypoint for this task
     */
    @TaskAction
    fun action() {
        val changeLogVersion = this.generateChangelogVersion()
        this.generateTemplates(changeLogVersion)
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

        val file = File(changeLogFilePath)

        return if (file.exists()) {
            this.generateChangeLogVersionExists(file, releaseVersionSemver)
        } else {
            this.generateChangeLogVersionNoExists(file, releaseVersionSemver)
        }
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
            logger.lifecycle("No input variables set for release, pinning as patch release")
            releasePatch = true
        }

        val newVersion = if (version != null && version <= latestVersion) {
            throw IllegalArgumentException("ReleaseVersion must be greater than the version in the master changelog.yml")
        } else if (version == null && (releaseMajor != null && releaseMajor!!)) {
            SemVer(major = latestVersion.major + 1, minor = 0, patch = 0)
        } else if (version == null && (releaseMinor != null && releaseMinor!!)) {
            SemVer(major = latestVersion.major, minor = latestVersion.minor + 1, patch = 0)
        } else {
            SemVer(major = latestVersion.major, minor = latestVersion.minor, patch = latestVersion.patch + 1)
        }

        logger.lifecycle("Old version: $latestVersion, new version: $newVersion")
        val newChangeLogStr = "$migrationsFolder/$newVersion/$newVersion-changelog.yml"

        changeLog.databaseChangeLog.add(ChangeLogItem(IncludeItem(file = newChangeLogStr)))

        this.yamlMapper.writeValue(file, changeLog)
        logger.lifecycle("Wrote master changelog file: $file")

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

        logger.lifecycle("No changelog found, creating new version: $newVersion")
        val newChangeLog = "$migrationsFolder/$newVersion/$newVersion-changelog.yml"
        val changeLog = ChangeLogMaster(
            databaseChangeLog = arrayListOf(
                ChangeLogItem(IncludeItem(file = newChangeLog))
            )
        )
        this.yamlMapper.writeValue(file, changeLog)
        logger.lifecycle("Wrote master changelog file: $file")

        return newVersion
    }

    /**
     * Generates the changelog templates.
     *
     * @param changeLogVersion The version of the changelog
     */
    @Suppress("UnusedPrivateMember")
    private fun generateTemplates(changeLogVersion: SemVer) {
        val migrationFolderVersion = File("${this.generateMigrationsFolderPath()}/$changeLogVersion")
        logger.lifecycle("New migration folder: $migrationFolderVersion")

        if (migrationFolderVersion.exists()) {
            throw GradleException("new migration folder: $migrationFolderVersion already exists")
        }

        migrationFolderVersion.parentFile.mkdirs()
        migrationFolderVersion.mkdirs()

        this.writeMigration(changeLogVersion)
        this.writeRollback(changeLogVersion)
        this.writeTestdataMigration(changeLogVersion)

        val changeLogVersioned = ChangeLogMigration(
            databaseChangeLog = mutableListOf(
                ChangeSet(changeSet = defaultChangeSetItem(changeLogVersion, 1, "changeset_example")),
                ChangeSet(changeSet = defaultChangeSetItem(changeLogVersion, 1, "changeset_example", testData = true))
            )
        )

        val changeSetFile = File(
            "${this.generateMigrationsFolderPath()}/$changeLogVersion/$changeLogVersion-changelog.yml"
        )
        this.yamlMapper.writeValue(changeSetFile, changeLogVersioned)
    }
}
