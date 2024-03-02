plugins {
    kotlin("multiplatform")
}

kotlin {
    jvmToolchain(8)

    jvm()

    js(IR) {
        browser()
        nodejs()
    }

    linuxX64()

    mingwX64()

    macosArm64()
    macosX64()

    iosArm64()
    iosSimulatorArm64()
    iosX64()

    tvosArm64()
    tvosSimulatorArm64()
    tvosX64()

    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()
    watchosX64()

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

tasks.withType<Jar> {
    from(rootDir.resolve("LICENSE")) {
        into("META-INF")
    }
}
