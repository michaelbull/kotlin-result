import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper

val ossrhUsername: String? by project
val ossrhPassword: String? by project

val signingKeyId: String? by project // must be the last 8 digits of the key
val signingKey: String? by project
val signingPassword: String? by project

description = "A Result monad for modelling success or failure operations."

plugins {
    base
    id("com.github.ben-manes.versions") version Versions.versionsPlugin

    kotlin("multiplatform") version Versions.kotlin apply false
    id("org.jetbrains.kotlinx.benchmark") version Versions.kotlinBenchmark apply false
    id("org.jetbrains.dokka") version Versions.dokka apply false
    id("org.jetbrains.kotlin.plugin.allopen") version Versions.kotlin apply false
}

tasks.withType<DependencyUpdatesTask> {
    gradleReleaseChannel = GradleReleaseChannel.CURRENT.id

    rejectVersionIf {
        listOf("alpha", "beta", "rc", "cr", "m", "eap", "pr", "dev").any {
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

subprojects {
    plugins.withType<MavenPublishPlugin> {
        apply(plugin = "org.gradle.signing")

        plugins.withType<KotlinMultiplatformPluginWrapper> {
            apply(plugin = "org.jetbrains.dokka")

            val dokkaHtml by tasks.existing(DokkaTask::class)

            val javadocJar by tasks.registering(Jar::class) {
                group = LifecycleBasePlugin.BUILD_GROUP
                description = "Assembles a jar archive containing the Javadoc API documentation."
                archiveClassifier.set("javadoc")
                from(dokkaHtml)
            }

            configure<KotlinMultiplatformExtension> {
                explicitApi()

                jvm {
                    mavenPublication {
                        artifact(javadocJar.get())
                    }
                }

                js(BOTH) {
                    browser()
                    nodejs()
                }

                ios()
                linuxX64()
                mingwX64()
                macosX64()
            }
        }

        configure<PublishingExtension> {
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
                    name.set(project.name)
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

                        contributor {
                            name.set("Joseph Van der Wee")
                            url.set("https://github.com/jvanderwee")
                        }

                        contributor {
                            name.set("Gregory Inouye")
                            url.set("https://github.com/gregoryinouye")
                        }

                        contributor {
                            name.set("Thomas Oddsund")
                            url.set("https://github.com/oddsund")
                        }

                        contributor {
                            name.set("Jan Müller")
                            url.set("https://github.com/DerYeger")
                        }

                        contributor {
                            name.set("avently")
                            url.set("https://github.com/avently")
                        }

                        contributor {
                            name.set("gsteckman")
                            url.set("https://github.com/gsteckman")
                        }

                        contributor {
                            name.set("Mathias Guelton")
                            url.set("https://github.com/mguelton")
                        }

                        contributor {
                            name.set("Jordan Bergin")
                            url.set("https://github.com/MrBergin")
                        }

                        contributor {
                            name.set("Pablo Gonzalez Alonso")
                            url.set("https://pablisco.com/")
                        }

                        contributor {
                            name.set("Joseph Cooper")
                            url.set("https://grodin.github.io/")
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

            configure<SigningExtension> {
                useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
                sign(publications)
            }
        }
    }
}
