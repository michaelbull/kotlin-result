description = "Extensions for using kotlin-result with kotlinx-coroutines."

plugins {
    `maven-publish`
    kotlin("multiplatform")
}

kotlin {
    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.contracts.ExperimentalContracts")
            }
        }

        commonMain {
            dependencies {
                implementation(libs.kotlin.coroutines.core)
                api(project(":kotlin-result"))
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(libs.kotlin.coroutines.test)
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(kotlin("test"))
            }
        }

        jsTest {
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
