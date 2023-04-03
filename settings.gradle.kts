rootProject.name = "kotlin-result"

include(
    "benchmarks",
    "example",
    "kotlin-result",
    "kotlin-result-coroutines"
)

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}
