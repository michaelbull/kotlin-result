rootProject.name = "kotlin-result"

include("example", "coroutines", "core")

pluginManagement {
    repositories {
        maven("https://dl.bintray.com/kotlin/kotlinx")
        gradlePluginPortal()
    }
}
