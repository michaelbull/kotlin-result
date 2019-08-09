import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

description = "A Result monad for modelling success or failure operations."

plugins {
    `maven-publish`
    kotlin("jvm") version ("1.3.41")
    id("org.jetbrains.dokka") version ("0.9.18")
    id("com.github.ben-manes.versions") version ("0.21.0")
    id("com.jfrog.bintray") version ("1.8.4")
    id("net.researchgate.release") version ("2.8.1")
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
    }

    plugins.withType<KotlinPluginWrapper> {
        tasks.withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "1.8"
        }
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

fun BintrayExtension.pkg(configure: BintrayExtension.PackageConfig.() -> Unit) {
    pkg(delegateClosureOf(configure))
}

val dokka by tasks.existing(DokkaTask::class) {
    sourceDirs = sourceSets["main"].kotlin.srcDirs
    outputFormat = "javadoc"
    outputDirectory = "$buildDir/docs/javadoc"
    kotlinTasks(::defaultKotlinTasks)
}

val javadocJar by tasks.registering(Jar::class) {
    group = LifecycleBasePlugin.BUILD_GROUP
    description = "Assembles a jar archive containing the Javadoc API documentation."
    archiveClassifier.set("javadoc")
    dependsOn(dokka)
    from(dokka.get().outputDirectory)
}

val sourcesJar by tasks.registering(Jar::class) {
    group = LifecycleBasePlugin.BUILD_GROUP
    description = "Assembles a jar archive containing the main classes with sources."
    archiveClassifier.set("sources")
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

val bintrayUser: String? by project
val bintrayKey: String? by project

bintray {
    user = bintrayUser
    key = bintrayKey
    setPublications("mavenJava")

    pkg {
        repo = "maven"
        name = project.name
        desc = project.description
        websiteUrl = "https://github.com/michaelbull/kotlin-result"
        issueTrackerUrl = "https://github.com/michaelbull/kotlin-result/issues"
        vcsUrl = "git@github.com:michaelbull/kotlin-result.git"
        githubRepo = "michaelbull/kotlin-result"
        setLicenses("ISC")
    }
}

val bintrayUpload by tasks.existing(BintrayUploadTask::class) {
    dependsOn("build")
    dependsOn("generatePomFileForMavenJavaPublication")
    dependsOn(sourcesJar)
    dependsOn(javadocJar)
}

tasks.named("afterReleaseBuild") {
    dependsOn(bintrayUpload)
}
