plugins {
    `maven-publish`
    signing
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
                val ossrhUsername: String? by project
                val ossrhPassword: String? by project

                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }

    publications.withType<MavenPublication> {
        val targetName = this@withType.name

        artifact(tasks.register("${targetName}JavadocJar", Jar::class) {
            group = LifecycleBasePlugin.BUILD_GROUP
            description = "Assembles a jar archive containing the Javadoc API documentation of target '$targetName'."
            archiveClassifier.set("javadoc")
            archiveAppendix.set(targetName)
        })

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

                contributor {
                    name.set("Sebastian Kappen")
                    url.set("https://github.com/Nimelrian")
                }

                contributor {
                    name.set("Dmitry Suzdalev")
                    url.set("https://github.com/dimsuz")
                }

                contributor {
                    name.set("Berik Visschers")
                    url.set("https://visschers.nu/")
                }

                contributor {
                    name.set("Matthew Nelson")
                    url.set("https://matthewnelson.io/")
                }

                contributor {
                    name.set("Matthias Geisler")
                    url.set("https://github.com/bitPogo")
                }

                contributor {
                    name.set("Kirill Zhukov")
                    url.set("https://github.com/kirillzh")
                }

                contributor {
                    name.set("Peter Cunderlik")
                    url.set("https://github.com/peter-cunderlik-kmed")
                }

                contributor {
                    name.set("YuitoSato")
                    url.set("https://github.com/YuitoSato")
                }

                contributor {
                    name.set("Dmitry Bock")
                    url.set("https://github.com/Jhabkin")
                }

                contributor {
                    name.set("Eichisanden")
                    url.set("https://blog.a-1.dev/")
                }

                contributor {
                    name.set("Ryoko Hirai")
                    url.set("https://github.com/rhirai-line")
                }

                contributor {
                    name.set("Hoang Chung")
                    url.set("https://github.com/hoangchungk53qx1")
                }

                contributor {
                    name.set("Daiji")
                    url.set("https://daiji256.github.io/")
                }

                contributor {
                    name.set("Yusuke Katsuragawa")
                    url.set("https://kaleidot.net/")
                }
            }

            scm {
                connection.set("scm:git:https://github.com/michaelbull/kotlin-result")
                developerConnection.set("scm:git:git@github.com:michaelbull/kotlin-result.git")
                url.set("https://github.com/michaelbull/kotlin-result")
            }

            issueManagement {
                system.set("GitHub Issues")
                url.set("https://github.com/michaelbull/kotlin-result/issues")
            }

            ciManagement {
                system.set("GitHub Actions")
                url.set("https://github.com/michaelbull/kotlin-result/actions")
            }
        }
    }
}

signing {
    val signingKeyId: String? by project // must be the last 8 digits of the key
    val signingKey: String? by project
    val signingPassword: String? by project

    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(publishing.publications)
}
