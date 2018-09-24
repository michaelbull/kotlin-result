import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "io.ktor.server.netty.DevelopmentEngine"
}

repositories {
    maven(url = "http://dl.bintray.com/kotlin/ktor")
    maven(url = "https://dl.bintray.com/kotlin/kotlinx")
}

dependencies {
    implementation(project(":"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("ch.qos.logback:logback-classic:${ext["logbackVersion"]}")
    implementation("io.ktor:ktor-server-core:${ext["ktorVersion"]}")
    implementation("io.ktor:ktor-server-netty:${ext["ktorVersion"]}")
    implementation("io.ktor:ktor-gson:${ext["ktorVersion"]}")
}

val compileKotlin by tasks.existing(KotlinCompile::class)
val compileTestKotlin by tasks.existing(KotlinCompile::class)

compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

kotlin {
    experimental.coroutines = Coroutines.ENABLE
}
