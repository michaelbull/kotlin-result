import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import groovy.lang.Closure
import org.gradle.api.internal.HasConvention
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

description = "A Result monad for modelling success or failure operations."

plugins {
    `maven-publish`
    kotlin("jvm") version ("1.2.71")
    id("org.jetbrains.dokka") version ("0.9.17")
    id("com.github.ben-manes.versions") version ("0.20.0")
    id("com.jfrog.bintray") version ("1.8.4")
    id("net.researchgate.release") version ("2.7.0")
}

allprojects {
    repositories {
        mavenCentral()
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation("junit:junit:4.12")
    testImplementation(kotlin("test-common"))
    testImplementation(kotlin("test-annotations-common"))
    testImplementation(kotlin("test-junit"))
    testImplementation(kotlin("test"))
}

val SourceSet.kotlin: SourceDirectorySet
    get() = withConvention(KotlinSourceSet::class) { kotlin }

val compileKotlin by tasks.existing(KotlinCompile::class)
val compileTestKotlin by tasks.existing(KotlinCompile::class)
val dokka by tasks.existing(DokkaTask::class)

compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

dokka {
    kotlinTasks(closureOf<Any?> { emptyList() })
    sourceDirs = sourceSets["main"].kotlin.srcDirs
    outputFormat = "javadoc"
    outputDirectory = "$buildDir/docs/javadoc"
}

val javadocJar by tasks.registering(Jar::class) {
    group = LifecycleBasePlugin.BUILD_GROUP
    description = "Assembles a jar archive containing the Javadoc API documentation."
    classifier = "javadoc"
    dependsOn(dokka)
    from(dokka.get().outputDirectory)
}

val sourcesJar by tasks.registering(Jar::class) {
    group = LifecycleBasePlugin.BUILD_GROUP
    description = "Assembles a jar archive containing the main classes with sources."
    classifier = "sources"
    from(sourceSets["main"].allSource)
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(javadocJar.get())
            artifact(sourcesJar.get())
        }
    }
}

bintray {
    user = project.findProperty("bintrayUser")?.toString() ?: ""
    key = project.findProperty("bintrayKey")?.toString() ?: ""
    setPublications("mavenJava")

    pkg(closureOf<BintrayExtension.PackageConfig> {
        repo = "maven"
        name = "kotlin-result"
        vcsUrl = "git@github.com:michaelbull/kotlin-result.git"
        setLicenses("ISC")
    })
}

val build by tasks.existing
val generatePomFileForMavenJavaPublication by tasks.existing(GenerateMavenPom::class)
val bintrayUpload by tasks.existing(BintrayUploadTask::class)

bintrayUpload {
    dependsOn(build)
    dependsOn(generatePomFileForMavenJavaPublication)
    dependsOn(sourcesJar)
    dependsOn(javadocJar)
}

val afterReleaseBuild by tasks.existing

afterReleaseBuild {
    dependsOn(bintrayUpload)
}
