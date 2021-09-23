description = "Benchmarks for kotlin-result."

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlinx.benchmark")
    id("org.jetbrains.kotlin.plugin.allopen")
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
    annotation("org.openjdk.jmh.annotations.BenchmarkMode")
}

benchmark {
    targets {
        register("jvm")
        // TODO: enable js benchmarking once https://github.com/Kotlin/kotlinx-benchmark/issues/28 is fixed.
        //  register("js")
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
                implementation(project(":kotlin-result-coroutines"))
                implementation("org.jetbrains.kotlinx:kotlinx.benchmark.runtime:${Versions.kotlinBenchmark}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}")
            }
        }
    }
}
