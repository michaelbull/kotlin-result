import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
}

kotlin {
    compilerOptions {
        optIn.add("kotlin.contracts.ExperimentalContracts")
        freeCompilerArgs.addAll(
            "-Xexpect-actual-classes",
            "-Xreturn-value-checker=full",
        )
    }

    jvm()
    jvmToolchain(8)

    js {
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
    watchosSimulatorArm64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosArm64()

    /* https://kotlinlang.org/docs/native-target-support.html#tier-3 */

    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()
    mingwX64()
    watchosDeviceArm64()
    iosX64()

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
