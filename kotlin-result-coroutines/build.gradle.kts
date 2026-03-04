plugins {
    id("kotlin-conventions")
    id("publish-conventions")
}

kotlin {
    explicitApi()

    compilerOptions {
        optIn.add("kotlinx.coroutines.ExperimentalCoroutinesApi")
        optIn.add("com.github.michaelbull.result.annotation.UnsafeResultValueAccess")
        optIn.add("com.github.michaelbull.result.annotation.UnsafeResultErrorAccess")
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
