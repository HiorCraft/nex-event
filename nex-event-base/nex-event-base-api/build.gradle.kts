plugins {
    id("dev.slne.surf.api.gradle.core")
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = "dev.hiorcraft.nex"
            artifactId = "nex-event-base-api"
            version = "1.0.0"
        }
    }
}