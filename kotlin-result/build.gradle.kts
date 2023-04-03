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

    linuxArm32Hfp()
    linuxArm64()
    linuxMips32()
    linuxMipsel32()

    mingwX86()

    wasm32()

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.contracts.ExperimentalContracts")
            }
        }

        val commonMain by getting {

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

        val nativeMain by creating {
            dependsOn(commonMain)
        }

        val androidNativeMain by creating {
            dependsOn(nativeMain)
        }

        val mingwMain by creating {
            dependsOn(nativeMain)
        }

        val unixMain by creating {
            dependsOn(nativeMain)
        }

        val linuxMain by creating {
            dependsOn(unixMain)
        }

        val darwinMain by creating {
            dependsOn(unixMain)
        }

        val macosMain by creating {
            dependsOn(darwinMain)
        }

        val iosMain by getting {
            dependsOn(darwinMain)
        }

        val tvosMain by getting {
            dependsOn(darwinMain)
        }

        val watchosMain by getting {
            dependsOn(darwinMain)
        }

        // Android Native
        val androidNativeArm32Main by getting {
            dependsOn(androidNativeMain)
        }

        val androidNativeArm64Main by getting {
            dependsOn(androidNativeMain)
        }

        val androidNativeX64Main by getting {
            dependsOn(androidNativeMain)
        }

        val androidNativeX86Main by getting {
            dependsOn(androidNativeMain)
        }

        // Linux
        val linuxArm32HfpMain by getting {
            dependsOn(linuxMain)
        }

        val linuxArm64Main by getting {
            dependsOn(linuxMain)
        }

        val linuxMips32Main by getting {
            dependsOn(linuxMain)
        }

        val linuxMipsel32Main by getting {
            dependsOn(linuxMain)
        }

        val linuxX64Main by getting {
            dependsOn(linuxMain)
        }

        // Mingw
        val mingwX64Main by getting {
            dependsOn(mingwMain)
        }

        val mingwX86Main by getting {
            dependsOn(mingwMain)
        }

        // Darwin [ macOS ]
        val macosArm64Main by getting {
            dependsOn(macosMain)
        }

        val macosX64Main by getting {
            dependsOn(macosMain)
        }

        // Darwin [ iOS ]
        val iosArm32Main by getting {
            dependsOn(iosMain)
        }

        val iosArm64Main by getting {
            dependsOn(iosMain)
        }

        val iosX64Main by getting {
            dependsOn(iosMain)
        }

        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }

        // Darwin [ tvOS ]
        val tvosArm64Main by getting {
            dependsOn(tvosMain)
        }

        val tvosX64Main by getting {
            dependsOn(tvosMain)
        }

        val tvosSimulatorArm64Main by getting {
            dependsOn(tvosMain)
        }

        // Darwin [ watchOS ]
        val watchosArm32Main by getting {
            dependsOn(watchosMain)
        }

        val watchosArm64Main by getting {
            dependsOn(watchosMain)
        }

        val watchosX64Main by getting {
            dependsOn(watchosMain)
        }

        val watchosX86Main by getting {
            dependsOn(watchosMain)
        }

        val watchosSimulatorArm64Main by getting {
            dependsOn(watchosMain)
        }

        val wasm32Main by getting {
            dependsOn(nativeMain)
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
