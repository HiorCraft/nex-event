plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.hiorcraft.nex.HideAndSeek.PaperMain")
    generateLibraryLoader(false)
    foliaSupported(true)
    authors.add("hiorcraft")
}

dependencies {
    api(projects.nexEventBase.nexEventBasePaper)
}
