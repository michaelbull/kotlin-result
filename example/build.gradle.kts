import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
}

application {
    mainClass.set("com.github.michaelbull.result.example.ApplicationKt")
}

kotlin {
    jvmToolchain(8)
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
