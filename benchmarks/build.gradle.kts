description = "A module containing benchmarks for the other modules"

plugins {
    kotlin("multiplatform")
    id("kotlinx.benchmark") version Versions.kotlinBenchmark
    id("org.jetbrains.kotlin.plugin.allopen") version Versions.kotlin
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
    annotation("org.openjdk.jmh.annotations.BenchmarkMode")
}

benchmark {
    targets {
        register("jvm")
        register("js")
    }
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    js {
        nodejs()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":kotlin-result"))
                implementation("org.jetbrains.kotlinx:kotlinx.benchmark.runtime:${Versions.kotlinBenchmark}")
            }
        }
    }
}
