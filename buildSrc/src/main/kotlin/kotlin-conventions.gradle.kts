import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8

plugins {
    kotlin("multiplatform")
}

kotlin {
    compilerOptions {
        optIn.add("kotlin.contracts.ExperimentalContracts")
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    jvm {
        compilerOptions {
            jvmTarget.set(JVM_1_8)
        }
    }

    js(IR) {
        browser()
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        binaries.executable()
        nodejs()
    }

    /* https://kotlinlang.org/docs/native-target-support.html#tier-1 */

    macosArm64()
    iosSimulatorArm64()
    iosArm64()

    /* https://kotlinlang.org/docs/native-target-support.html#tier-2 */

    linuxX64()
    linuxArm64()
    macosX64()
    iosX64()
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()

    /* https://kotlinlang.org/docs/native-target-support.html#tier-3 */

    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()
    mingwX64()
    watchosDeviceArm64()

    sourceSets {
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

tasks.withType<Jar> {
    manifest {
        attributes(mapOf("SPDX-License-Identifier" to "ISC"))
    }

    from(rootDir.resolve("LICENSE")) {
        into("META-INF")
    }
}
