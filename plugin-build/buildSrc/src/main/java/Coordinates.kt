object PluginCoordinates {
    const val ID = "io.github.m1.liquibase-change-generator"
    const val GROUP = "io.github.m1.liquibase.changegenerator"
    const val VERSION = "0.0.2"
    const val IMPLEMENTATION_CLASS = "io.github.m1.liquibase.changegenerator.plugin.ChangeGeneratorPlugin"
}

object PluginBundle {
    const val VCS = "https://github.com/m1/liquibase-changegenerator"
    const val WEBSITE = "https://github.com/m1/liquibase-changegenerator"
    const val DESCRIPTION = "A Gradle plugin for generating liquibase migrations"
    const val DISPLAY_NAME = "Liquibase Gradle Change Generator Plugin"
    val TAGS = listOf(
        "liquibase",
        "migration",
        "database",
    )
}
