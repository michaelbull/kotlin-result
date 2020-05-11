rootProject.name = "kotlin-result"

include("example")

pluginManagement {
    repositories {
        maven("https://dl.bintray.com/kotlin/kotlinx" )
        gradlePluginPortal()
    }
}
