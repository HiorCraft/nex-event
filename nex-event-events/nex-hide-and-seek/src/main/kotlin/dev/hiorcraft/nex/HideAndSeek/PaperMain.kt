package dev.hiorcraft.nex.hideandseek

import dev.hiorcraft.nex.hideandseek.command.HideAndSeekCommand
import dev.hiorcraft.nex.hideandseek.command.WorldCommand
import dev.hiorcraft.nex.hideandseek.game.HideAndSeekGame
import dev.hiorcraft.nex.hideandseek.listener.HideAndSeekListener
import dev.hiorcraft.nex.hideandseek.placeholder.HideAndSeekPlaceholderExpansion
import dev.hiorcraft.nex.hideandseek.world.WorldMapManager
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin

@Suppress("UnstableApiUsage")
class PaperMain : JavaPlugin() {

    lateinit var game: HideAndSeekGame
        private set
    lateinit var worldMapManager: WorldMapManager
        private set

    override fun onEnable() {
        saveDefaultConfig()
        worldMapManager = WorldMapManager(this)
        worldMapManager.loadFromConfig()

        game = HideAndSeekGame(this, worldMapManager)

        server.pluginManager.registerEvents(HideAndSeekListener(game), this)

        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            HideAndSeekCommand.register(event.registrar(), game)
            WorldCommand.register(event.registrar(), worldMapManager)
        }

        if (server.pluginManager.getPlugin("PlaceholderAPI") != null) {
            HideAndSeekPlaceholderExpansion(game).register()
        }

        logger.info("Hide and Seek Plugin gestartet!")
    }

    override fun onDisable() {
        if (game.state != dev.hiorcraft.nex.hideandseek.game.GameState.WAITING) {
            game.forceEnd()
        }
        logger.info("Hide and Seek Plugin gestoppt!")
    }
}
