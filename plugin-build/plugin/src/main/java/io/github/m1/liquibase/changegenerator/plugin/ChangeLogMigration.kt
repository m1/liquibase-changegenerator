package io.github.m1.liquibase.changegenerator.plugin

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * Represents a migration ChangeLog
 *
 * @property databaseChangeLog Contains the list of changeSets for this migration
 */
data class ChangeLogMigration(
    val databaseChangeLog: MutableList<ChangeSet>,
)

/**
 * Parent of a ChangeSet
 *
 * @property changeSet Contains the ChangeSetItem
 */
data class ChangeSet(
    val changeSet: ChangeSetItem,
)

/**
 * Represents a single ChangeSet
 *
 * @property id The ID of the changeSet
 * @property author The author of the changeSet, in `FirstName LastName <email@example.com>` format
 * @property labels The labels of the changeset
 * @property context The context of the changeset
 * @property comment Any additional information about the changeset
 * @property logicalFilePath The named file path for the changeset sql file.
 * @property changes The list of ChangeSetChanges
 * @property rollback The list of rollbacks to be executed
 */
data class ChangeSetItem(
    var id: String,

    val author: String,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val labels: MutableList<String> = mutableListOf(),

    var context: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var comment: String? = null,

    var logicalFilePath: String,

    var changes: List<ChangeSetChanges>,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var rollback: List<ChangeSetChanges> = emptyList(),
)

/**
 * Represents a single ChangeSetChange
 *
 * @property sqlFile The sql to execute
 * @property tagDatabase The new database tag
 */
data class ChangeSetChanges(
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val sqlFile: ChangeSetChangesSQLFilePath? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val tagDatabase: ChangeSetChangesTagDatabase? = null,
)

/**
 * Represents a single file path for a changeset change.
 *
 * @property path The path of the sql to execute
 */
data class ChangeSetChangesSQLFilePath(
    val path: String,
)

/**
 * Represents a single database tag for a changeset change.
 *
 * @property tag The new database tag
 */
data class ChangeSetChangesTagDatabase(
    val tag: String,
)
