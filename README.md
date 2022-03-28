# liquibase-changegenerator

## Summary

Adds two tasks to liquibase via gradle for generating changelogs and changesets.

## Usage

Install using gradle:

```kotlin
// build.gradle.kts
plugins {
    java
    id("io.github.m1.liquibase-change-generator")
}

val liquibaseRoot = "./db"

/** The liquibase migrations folder */
val liquibaseMigrationsFolder = "$liquibaseRoot/migrations"

/** The master liquibase changelog */
val liquibaseChangeLogFile = "$liquibaseRoot/changelog.yml"

liquibaseChangeGenerator {
    migrationsFolder = liquibaseMigrationsFolder
    changeLogFile = liquibaseChangeLogFile
}
```

New gradle tasks:

### `gradle newChangeLog`

```shell
Detailed task information for newChangeLog

Path
     :newChangeLog

Type
     NewChangeLog (Build_gradle$NewChangeLog)

Options
     --releaseMajor     Bumps the major version

     --releaseMinor     Bumps the minor version

     --releasePatch     Bumps the patch version

     --releaseVersion     The new version to release

Description
     Generates a new changelog

Group
     liquibase

```

### `gradle newChangeSet `

```shell
Detailed task information for newChangeSet

Path
     :newChangeSet

Type
     NewChangeSet (Build_gradle$NewChangeSet)

Options
     --changeSetName     The name of this changeset

     --withTestdata     Generates a testdata migration

Description
     Generates a new changeset

Group
     liquibase
```
