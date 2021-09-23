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
