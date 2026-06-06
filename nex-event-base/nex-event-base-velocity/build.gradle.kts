plugins {
    id("dev.slne.surf.api.gradle.velocity")
}

velocityPluginFile {
    main = "dev.hiorcraft.nex.base.velocity.VelocityMain"
    authors = listOf("hiorcraft")
}

dependencies {
    api(projects.nexEventBase.nexEventBaseCore)
}
