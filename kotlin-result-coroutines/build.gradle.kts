description = "Extensions for using kotlin-result with kotlinx-coroutines."

plugins {
    `maven-publish`
    kotlin("multiplatform")
}

kotlin {
    sourceSets {
        all {
            languageSettings.apply {
                useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
            }
        }

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}")
                implementation(project(":kotlin-result"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(kotlin("test"))
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

        val nativeTest by creating {
            dependsOn(commonTest)
        }

        val linuxX64Test by getting {
            dependsOn(nativeTest)
        }

        val mingwX64Test by getting {
            dependsOn(nativeTest)
        }

        val macosX64Test by getting {
            dependsOn(nativeTest)
        }

        val iosX64Test by getting {
            dependsOn(nativeTest)
        }

        val iosArm64Test by getting {
            dependsOn(nativeTest)
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
