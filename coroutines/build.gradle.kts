plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    sourceSets {
        all {
            languageSettings.apply {
                useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
            }
        }

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:1.3.8")
                implementation(rootProject)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:1.3.8")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.8")
            }
        }
    }
}

tasks {
    named<Test>("jvmTest") {
        filter {
            isFailOnNoMatchingTests = false
        }
    }
}

