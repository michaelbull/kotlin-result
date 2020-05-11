import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ossrhUsername: String? by ext
val ossrhPassword: String? by ext

description = "A Result monad for modelling success or failure operations."

plugins {
    `maven-publish`
    signing
    kotlin("jvm") version "1.3.72"
    id("org.jetbrains.dokka") version "0.10.1"
    id("com.github.ben-manes.versions") version "0.28.0"
    id("net.researchgate.release") version "2.8.1"
    id("kotlinx.benchmark") version "0.2.0-dev-7"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.3.70"
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlinx")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xuse-experimental=kotlin.contracts.ExperimentalContracts")
        }
    }
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
    annotation("org.openjdk.jmh.annotations.BenchmarkMode")
}

sourceSets.create("benchmark") {
    java {
        srcDir("src/benchmarks/kotlin")
    }
}

val benchmarkImplementation by configurations

benchmark {
    targets {
        register("benchmark")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("junit:junit:4.13")
    testImplementation(kotlin("test-common"))
    testImplementation(kotlin("test-annotations-common"))
    testImplementation(kotlin("test-junit"))
    testImplementation(kotlin("test"))

    benchmarkImplementation(sourceSets.main.get().output + sourceSets.main.get().runtimeClasspath)
    benchmarkImplementation("org.jetbrains.kotlinx:kotlinx.benchmark.runtime-jvm:0.2.0-dev-7")
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        listOf("alpha", "beta", "rc", "cr", "m", "eap", "pr").any {
            candidate.version.contains(it, ignoreCase = true)
        }
    }
}

val dokka by tasks.existing(DokkaTask::class) {
    outputFormat = "javadoc"
    outputDirectory = "$buildDir/docs/javadoc"
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
    repositories {
        maven {
            if (project.version.toString().endsWith("SNAPSHOT")) {
                setUrl("https://oss.sonatype.org/content/repositories/snapshots")
            } else {
                setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            }

            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }

    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(javadocJar.get())
            artifact(sourcesJar.get())

            pom {
                name.set(project.name)
                description.set(project.description)
                url.set("https://github.com/michaelbull/kotlin-result")
                inceptionYear.set("2017")

                licenses {
                    license {
                        name.set("ISC License")
                        url.set("https://opensource.org/licenses/isc-license.txt")
                    }
                }

                developers {
                    developer {
                        name.set("Michael Bull")
                        url.set("https://www.michael-bull.com")
                    }
                }

                contributors {
                    contributor {
                        name.set("Kevin Herron")
                        url.set("https://github.com/kevinherron")
                    }

                    contributor {
                        name.set("Markus Padourek")
                        url.set("https://github.com/Globegitter")
                    }

                    contributor {
                        name.set("Tristan Hamilton")
                        url.set("https://github.com/Munzey")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/michaelbull/kotlin-result")
                    developerConnection.set("scm:git:git@github.com:michaelbull/kotlin-result.git")
                    url.set("https://github.com/michaelbull/kotlin-result")
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/michaelbull/kotlin-result/issues")
                }

                ciManagement {
                    system.set("GitHub")
                    url.set("https://github.com/michaelbull/kotlin-result/actions?query=workflow%3Aci")
                }
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}

tasks.afterReleaseBuild {
    dependsOn(tasks.publish)
}
