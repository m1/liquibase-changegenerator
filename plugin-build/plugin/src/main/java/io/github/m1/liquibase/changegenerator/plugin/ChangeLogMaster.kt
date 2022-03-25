package io.github.m1.liquibase.changegenerator.plugin

/**
 * Represents a master ChangeLog
 *
 * @property databaseChangeLog Contains the list of ChangeLog items.
 */
data class ChangeLogMaster(
    val databaseChangeLog: MutableList<ChangeLogItem>,
)

/**
 * Represents a master ChangeLogItem
 *
 * @property include The includeItems
 */
data class ChangeLogItem(
    val include: IncludeItem,
)

/**
 * Represents a master IncludeItem
 *
 * @property file The file to include
 */
data class IncludeItem(
    val file: String,
)
