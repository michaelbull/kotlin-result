import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel

plugins {
    alias(libs.plugins.versions)

    alias(libs.plugins.kotlin.benchmark) apply false
    alias(libs.plugins.kotlin.allopen) apply false
}

tasks.withType<DependencyUpdatesTask> {
    gradleReleaseChannel = GradleReleaseChannel.CURRENT.id

    rejectVersionIf {
        listOf("alpha", "beta", "rc", "cr", "m", "eap", "pr", "dev").any {
            candidate.version.contains(it, ignoreCase = true)
        }
    }
}
