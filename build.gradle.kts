import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

val ossrhUsername: String? by ext
val ossrhPassword: String? by ext

description = "A Result monad for modelling success or failure operations."

plugins {
    `maven-publish`
    signing
    kotlin("multiplatform") version "1.3.72"
    id("org.jetbrains.dokka") version "0.10.1"
    id("com.github.ben-manes.versions") version "0.28.0"
    id("net.researchgate.release") version "2.8.1"
    id("kotlinx.benchmark") version "0.2.0-dev-8"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.3.72"
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        listOf("alpha", "beta", "rc", "cr", "m", "eap", "pr").any {
            candidate.version.contains(it, ignoreCase = true)
        }
    }
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlinx")
    }
}

configure(subprojects.filter { it.name != "example" }) {
    apply(plugin = "org.gradle.maven-publish")
    apply(plugin = "org.jetbrains.dokka")

    val subproject = this

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

        publications.withType<MavenPublication> {
            pom {
                name.set(subproject.name)
                description.set(subproject.description)
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
