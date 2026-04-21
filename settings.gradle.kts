pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// Spout start - Project setup - Check for Git
if (!file(".git").exists()) {
    val errorText = """
        
        =====================[ ERROR ]=====================
         The Spout project directory is not a properly cloned Git repository.
         
         In order to build Spout from source you must clone
         the Spout repository using Git, not download a code
         zip from GitHub.
         
         Built Spout jars are available for download at
         https://github.com/FiddleMC/Spout-Paper-server/actions
         
         See https://github.com/PaperMC/Paper/blob/master/CONTRIBUTING.md
         for further information on building and modifying Paper forks.
        ===================================================
    """.trimIndent()
    error(errorText)
}
// Spout end - Project setup - Check for Git

// Spout start - Set up Gradle project
rootProject.name = "spout-paper-server"

include("spout-api")
include("spout-server")
// Spout end - Set up Gradle project

optionalInclude("test-plugin")

fun optionalInclude(name: String, op: (ProjectDescriptor.() -> Unit)? = null) {
    val settingsFile = file("$name.settings.gradle.kts")
    if (!settingsFile.exists()) {
        settingsFile.writeText(
            """
            // Comment to disable the '$name' project
            include(":$name")

            """.trimIndent()
        )
    }
    apply(from = settingsFile)
    findProject(":$name")?.let { op?.invoke(it) }
}
