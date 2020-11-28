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

sourceSets.create("benchmark")

benchmark {
    targets {
        register("jvmBenchmark")
    }
}

kotlin {
    jvm {
        withJava()

        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    sourceSets {
        all {
            languageSettings.apply {
                useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
            }
        }

        val jvmBenchmark by getting {
            dependencies {
                implementation(project(":kotlin-result"))
                implementation("org.jetbrains.kotlinx:kotlinx.benchmark.runtime:${Versions.kotlinBenchmark}")
            }
        }
    }
}
