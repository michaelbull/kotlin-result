import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    `maven-publish`
    id("kotlin-conventions")
    id("publish-conventions")
}

kotlin {
    explicitApi()

    // Additional targets not currently supported by coroutines
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX64()
    androidNativeX86()

    linuxArm64()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        binaries.executable()
        nodejs()
    }
}
