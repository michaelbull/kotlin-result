import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper

val ossrhUsername: String? by ext
val ossrhPassword: String? by ext

description = "A Result monad for modelling success or failure operations."

plugins {
    base
    id("com.github.ben-manes.versions") version "0.29.0"

    kotlin("multiplatform") version "1.4.0" apply false
    id("kotlinx.benchmark") version "0.2.0-dev-20" apply false
    id("net.researchgate.release") version "2.8.1" apply false
    id("org.jetbrains.dokka") version "0.10.1" apply false
    id("org.jetbrains.kotlin.plugin.allopen") version "1.4.0" apply false
}

tasks.withType<DependencyUpdatesTask> {
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
        apply(plugin = "net.researchgate.release")
        apply(plugin = "org.gradle.signing")

        val afterReleaseBuild by tasks.existing(DefaultTask::class)
        val publish by tasks.existing(Task::class)

        afterReleaseBuild {
            dependsOn(publish)
        }

        plugins.withType<KotlinMultiplatformPluginWrapper> {
            apply(plugin = "org.jetbrains.dokka")

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

            configure<KotlinMultiplatformExtension> {
                jvm {
                    mavenPublication {
                        artifact(javadocJar.get())
                    }
                }
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
                useGpgCmd()
                sign(publications)
            }
        }
    }
}
