pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://reposilite.slne.dev/releases")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.slne.surf.api.gradle.settings") version "+"
}

rootProject.name = "nex-event"

include("nex-event-base")
include("nex-event-base:nex-event-base-api")
include("nex-event-base:nex-event-base-core")
include("nex-event-base:nex-event-base-velocity")
include("nex-event-base:nex-event-base-paper")

listOf(
    "nex-hide-and-seek"
).forEach { event ->
    include("nex-event-events:$event")
}