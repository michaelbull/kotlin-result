plugins {
    id("org.jetbrains.kotlinx.benchmark")
    id("org.jetbrains.kotlin.plugin.allopen")
    id("kotlin-conventions")
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
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":kotlin-result-coroutines"))
                implementation(libs.kotlin.benchmark.runtime)
                implementation(libs.kotlin.coroutines.core)
            }
        }
    }
}
