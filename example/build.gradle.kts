import org.jetbrains.kotlin.gradle.dsl.Coroutines

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
    val ktorVersion = "1.0.0-beta-3"

    implementation(project(":"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("io.ktor:ktor-server-core:${ktorVersion}")
    implementation("io.ktor:ktor-server-netty:${ktorVersion}")
    implementation("io.ktor:ktor-gson:${ktorVersion}")
}

kotlin {
    experimental.coroutines = Coroutines.ENABLE
}
