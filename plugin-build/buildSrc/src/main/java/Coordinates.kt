object PluginCoordinates {
    const val ID = "com.m1c.liquibase.changegenerator.plugin"
    const val GROUP = "com.m1c.liquibase.changegenerator"
    const val VERSION = "0.0.1"
    const val IMPLEMENTATION_CLASS = "com.m1c.liquibase.changegenerator.plugin.TemplatePlugin"
}

object PluginBundle {
    const val VCS = "https://github.com/m1/liquibase-changegenerator"
    const val WEBSITE = "https://github.com/m1/liquibase-changegenerator"
    const val DESCRIPTION = "An empty Gradle plugin created from a template"
    const val DISPLAY_NAME = "An empty Gradle Plugin from a template"
    val TAGS = listOf(
        "plugin",
        "gradle",
        "sample",
        "template"
    )
}
