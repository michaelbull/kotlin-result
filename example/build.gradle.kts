import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
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

kotlin {
    experimental.coroutines = Coroutines.ENABLE
}
