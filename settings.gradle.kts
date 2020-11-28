rootProject.name = "kotlin-result"

include(
    "example",
    "kotlin-result",
    "kotlin-result-coroutines",
    "benchmarks"
)

pluginManagement {
    repositories {
        maven("https://dl.bintray.com/kotlin/kotlinx")
        gradlePluginPortal()
    }
}
