rootProject.name = "kotlin-result"

include("example", "coroutines")

pluginManagement {
    repositories {
        maven("https://dl.bintray.com/kotlin/kotlinx" )
        gradlePluginPortal()
    }
}
