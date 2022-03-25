pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
enableFeaturePreview("VERSION_CATALOGS")
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = ("io.github.m1.liquibase.changegenerator")

include(":plugin")
