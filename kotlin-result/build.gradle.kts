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

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(kotlin("test"))
            }
        }

        val jvmBenchmark by getting {
            dependsOn(jvmMain)
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx.benchmark.runtime-jvm:${Versions.kotlinBenchmark}")
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
