rootProject.name = "kotlin-result"

include(
    "example",
    "kotlin-result",
    "kotlin-result-coroutines"
)

pluginManagement {
    repositories {
        maven("https://dl.bintray.com/kotlin/kotlinx")
        gradlePluginPortal()
    }
}
