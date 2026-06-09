plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.hiorcraft.nex.hideandseek.PaperMain")
    generateLibraryLoader(false)
    foliaSupported(true)
    authors.add("hiorcraft")
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    api(projects.nexEventBase.nexEventBasePaper)
    compileOnly("me.clip:placeholderapi:2.11.6")
}
