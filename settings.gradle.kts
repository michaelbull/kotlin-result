rootProject.name = "kotlin-result"

include(
    "benchmarks",
    "example",
    "kotlin-result",
    "kotlin-result-coroutines"
)

pluginManagement {
    repositories {
        // https://github.com/Kotlin/kotlinx-benchmark/issues/42
        mavenCentral()
        gradlePluginPortal()
    }
}
