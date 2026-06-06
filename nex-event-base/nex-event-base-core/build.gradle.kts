plugins {
    id("dev.slne.surf.api.gradle.core")
}

dependencies {
    api(projects.nexEventBase.nexEventBaseApi)
}

surfCoreApi {
    withSurfRedis()
}
