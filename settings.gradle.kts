pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("net.swiftzer.semver:semver:1.2.0")
    }
}



dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "liquibase-changegenerator"

include(":example")
includeBuild("plugin-build")
