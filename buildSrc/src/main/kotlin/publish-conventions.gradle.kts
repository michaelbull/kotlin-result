import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform

plugins {
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Empty(),
            sourcesJar = true,
        )
    )

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
                name.set("Hoàng Anh Chung")
                url.set("https://github.com/hoangchungk53qx1")
            }

            contributor {
                name.set("Daiji Suzuki")
                url.set("https://daiji256.github.io/")
            }

            contributor {
                name.set("Yusuke Katsuragawa")
                url.set("https://kaleidot.net/")
            }

            contributor {
                name.set("Petrus Nguyễn Thái Học")
                url.set("https://hoc081098.github.io/profile/")
            }

            contributor {
                name.set("Alpha Ho")
                url.set("https://github.com/alphaho")
            }

            contributor {
                name.set("Raika Nakamura")
                url.set("https://github.com/nakamuraraika")
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
