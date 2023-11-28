import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinNpmInstallTask

description = "A Result monad for modelling success or failure operations."

plugins {
    `maven-publish`
    kotlin("multiplatform")
}

kotlin {
    jvmToolchain(8)

    // Additional targets not currently supported by coroutines
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX64()
    androidNativeX86()

    linuxArm64()

    @OptIn(org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl::class)
    wasmJs {
        binaries.executable()
        nodejs()
    }

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.contracts.ExperimentalContracts")
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test-junit"))
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

/* https://youtrack.jetbrains.com/issue/KT-63014/Running-tests-with-wasmJs-in-1.9.20-requires-Chrome-Canary#focus=Comments-27-8321383.0-0 */
rootProject.the<NodeJsRootExtension>().apply {
    nodeVersion = "21.0.0-v8-canary202309143a48826a08"
    nodeDownloadBaseUrl = "https://nodejs.org/download/v8-canary"
}

rootProject.tasks.withType<KotlinNpmInstallTask>().configureEach {
    args.add("--ignore-engines")
}
