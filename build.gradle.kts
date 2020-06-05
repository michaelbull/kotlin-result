import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.dokka.gradle.DokkaTask

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

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlinx")
    }
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
    annotation("org.openjdk.jmh.annotations.BenchmarkMode")
}

// creating this sourceSet and specifying jvm { withJava() } will create a
// "jvmBenchmark" sourceSet we can access from kotlin mpp extension
sourceSets.create("benchmark")

benchmark {
    targets {
        register("jvmBenchmark")
    }
}

kotlin {
    jvm {
        withJava()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(kotlin("test"))
            }
        }

        val jvmBenchmark by getting {
            dependsOn(jvmMain)
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx.benchmark.runtime-jvm:0.2.0-dev-8")
            }
        }

        all {
            languageSettings.apply {
                useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
            }
        }

    }

    jvm().compilations.all {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    jvm {
        mavenPublication {
            artifact(javadocJar.get())
        }
    }
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

    publications.withType<MavenPublication> {
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

signing {
    useGpgCmd()
    sign(publishing.publications)
}

tasks.afterReleaseBuild {
    dependsOn(tasks.publish)
}
