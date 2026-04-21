version = "1.0.0-SNAPSHOT"

// Uncomment the lines below to include the dev bundle
// (after having published it by calling the script at gradle-bin/refreshTestPluginDevBundle)
// plugins { id("io.papermc.paperweight.userdev") version "2.0.0-beta.19" }
// dependencies { paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT", "spout.server.paper") }

repositories {
    mavenLocal()
}

dependencies {
    compileOnly(project(":spout-api"))
}

tasks.processResources {
    val props = mapOf(
        "version" to project.version,
        "apiversion" to "\"${rootProject.providers.gradleProperty("apiVersion").get()}\"",
    )
    inputs.properties(props)
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}
