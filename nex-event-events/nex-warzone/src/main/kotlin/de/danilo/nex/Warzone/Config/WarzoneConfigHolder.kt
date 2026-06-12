package de.danilo.nex.Warzone.Config

import dev.slne.surf.api.core.config.manager.SpongeConfigManager
import dev.slne.surf.api.core.config.surfConfigApi
import de.danilo.nex.Warzone.plugin

class WarzoneConfigHolder {

    private val configManager: SpongeConfigManager<warzoneConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            warzoneConfig::class.java,
            plugin.dataPath,
            "lobby.yml"
        )
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(
            warzoneConfig::class.java
        )
        reload()
    }

    fun reload() {
        configManager.reloadFromFile()
    }

    val WarzoneConfigHolder get() = configManager.config
}
