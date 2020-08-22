rootProject.name = "kotlin-result"

include("example", "coroutines", "kotlin-result")

pluginManagement {
    repositories {
        maven("https://dl.bintray.com/kotlin/kotlinx")
        gradlePluginPortal()
    }
}
