plugins {
    `maven-publish`
    id("kotlin-conventions")
    id("publish-conventions")
}

kotlin {
    explicitApi()

    compilerOptions {
        optIn.add("kotlinx.coroutines.ExperimentalCoroutinesApi")
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.coroutines.core)
                api(project(":kotlin-result"))
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.coroutines.test)
            }
        }
    }
}
