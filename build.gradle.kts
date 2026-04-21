import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("io.papermc.paperweight.patcher") version "2.0.0-beta.19"
}

paperweight {
    upstreams.paper {
        ref = providers.gradleProperty("paperRef")

        patchFile {
            path = "paper-server/build.gradle.kts"
            // Spout start - Project setup - Set up Paperweight
            outputFile = file("spout-server/build.gradle.kts")
            patchFile = file("spout-server/build.gradle.kts.patch")
            // Spout end - Project setup - Set up Paperweight
        }
        patchFile {
            path = "paper-api/build.gradle.kts"
            // Spout start - Project setup - Set up Paperweight
            outputFile = file("spout-api/build.gradle.kts")
            patchFile = file("spout-api/build.gradle.kts.patch")
            // Spout end - Project setup - Set up Paperweight
        }
        patchDir("paperApi") {
            upstreamPath = "paper-api"
            excludes = setOf("build.gradle.kts")
            patchesDir = file("spout-api/paper-patches") // Spout - Project setup - Set up Paperweight
            outputDir = file("paper-api")
        }
    }
}

val paperMavenPublicUrl = "https://repo.papermc.io/repository/maven-public/"

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    repositories {
        mavenCentral()
        maven(paperMavenPublicUrl)
    }

    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
        options.isFork = true
        // Spout start - Project setup - Hide annoying compilation warnings
        options.compilerArgs.addAll(
            listOf(
                "-Xlint:-dep-ann",
                "-Xlint:-deprecation",
                "-Xlint:-module",
                "-Xlint:-removal",
            )
        )
        // Spout end - Project setup - Hide annoying compilation warnings
    }
    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }
    tasks.withType<Test> {
        testLogging {
            showStackTraces = true
            exceptionFormat = TestExceptionFormat.FULL
            events(TestLogEvent.STANDARD_OUT)
        }
    }

    extensions.configure<PublishingExtension> {
        repositories {
            /*
            maven("https://repo.papermc.io/repository/maven-snapshots/") {
                name = "paperSnapshots"
                credentials(PasswordCredentials::class)
            }
             */
        }
    }
}
