package dev.hiorcraft.nex.base.paper.config

import dev.slne.surf.api.core.config.manager.SpongeConfigManager
import dev.slne.surf.api.core.config.surfConfigApi
import dev.hiorcraft.nex.base.paper.plugin

class EventServerConfigHolder {
    private val configManager: SpongeConfigManager<EventServerConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            EventServerConfig::class.java,
            plugin.dataPath,
            "config.yml"
        )
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(
            EventServerConfig::class.java
        )
        reload()
    }

    fun reload() {
        configManager.reloadFromFile()
    }

    val eventServerConfig get() = configManager.config
}
