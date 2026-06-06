plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.hiorcraft.nex.base.paper.PaperMain")
    generateLibraryLoader(false)
    foliaSupported(true)
    authors.add("hiorcraft")
    withSurfRedis()
}

dependencies {
    api(projects.nexEventBase.nexEventBaseCore)
}
