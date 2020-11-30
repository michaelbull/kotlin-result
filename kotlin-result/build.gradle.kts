description = "A Result monad for modelling success or failure operations."

plugins {
    `maven-publish`
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.allopen")
    id("kotlinx.benchmark")
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

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(kotlin("test"))
            }
        }

        val jvmBenchmark by getting {
            dependsOn(jvmMain)
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx.benchmark.runtime:${Versions.kotlinBenchmark}")
            }
        }

        val jsMain by getting

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

publishing {
    publications.withType<MavenPublication> {
        pom {
            description.set(project.description)
        }
    }
}
