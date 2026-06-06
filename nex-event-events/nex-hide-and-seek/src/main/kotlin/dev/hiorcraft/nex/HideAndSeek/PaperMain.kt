package dev.hiorcraft.nex.HideAndSeek

import dev.hiorcraft.nex.HideAndSeek.command.HideAndSeekCommand
import dev.hiorcraft.nex.HideAndSeek.game.HideAndSeekGame
import dev.hiorcraft.nex.HideAndSeek.listener.HideAndSeekListener
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin

@Suppress("UnstableApiUsage")
class PaperMain : JavaPlugin() {

    lateinit var game: HideAndSeekGame
        private set

    override fun onEnable() {
        game = HideAndSeekGame(this)

        server.pluginManager.registerEvents(HideAndSeekListener(game), this)

        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            HideAndSeekCommand.register(event.registrar(), game)
        }

        logger.info("Hide and Seek Plugin gestartet!")
    }

    override fun onDisable() {
        if (game.state != dev.hiorcraft.nex.HideAndSeek.game.GameState.WAITING) {
            game.forceEnd()
        }
        logger.info("Hide and Seek Plugin gestoppt!")
    }
}
