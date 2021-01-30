import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    maven(url = "http://dl.bintray.com/kotlin/ktor")
    maven(url = "https://dl.bintray.com/kotlin/kotlinx")
}

dependencies {
    implementation(project(":kotlin-result"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("ch.qos.logback:logback-classic:${Versions.logback}")
    implementation("io.ktor:ktor-server-core:${Versions.ktor}")
    implementation("io.ktor:ktor-server-netty:${Versions.ktor}")
    implementation("io.ktor:ktor-gson:${Versions.ktor}")
}

tasks.withType(KotlinCompile::class.java).all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
