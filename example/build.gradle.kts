plugins {
    java
    id("io.github.m1.liquibase-change-generator")
}

val liquibaseRootPath = "./db"
val liquibaseMigrationsFolder = "migrations"
val liquibaseChangelog = "changelog.yml"
val liquibaseChangeLogFilePath = "$liquibaseRootPath/changelog.yml"

liquibaseChangeGenerator {
    group = "liquibase"
    description = "Generates a new changeset"
}
