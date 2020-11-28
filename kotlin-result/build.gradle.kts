description = "A Result monad for modelling success or failure operations."

plugins {
    `maven-publish`
    kotlin("multiplatform")
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

    }
}

publishing {
    publications.withType<MavenPublication> {
        pom {
            description.set(project.description)
        }
    }
}
