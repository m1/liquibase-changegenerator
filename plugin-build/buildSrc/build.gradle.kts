plugins {
    `kotlin-dsl`
}
repositories {
    mavenCentral()
    mavenLocal()
}

buildscript {
    repositories {
        mavenCentral()
        google()
        mavenLocal()
    }
    dependencies {
        classpath("net.swiftzer.semver:semver:1.2.0")
    }
}

dependencies {
    compileOnly("net.swiftzer.semver:semver:1.2.0")
    compileOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.2")
    compileOnly("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")
}
