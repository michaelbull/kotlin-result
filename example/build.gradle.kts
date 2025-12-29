plugins {
    application
    kotlin("jvm")
}

application {
    mainClass.set("com.github.michaelbull.result.example.ApplicationKt")
}


kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xreturn-value-checker=check")
    }
}

dependencies {
    implementation(project(":kotlin-result"))
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.logback)
    implementation(libs.ktor.serialization.jackson)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.netty)
}
