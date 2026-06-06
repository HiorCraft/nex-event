package dev.hiorcraft.nex.base.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.hiorcraft.nex.base.core.loader.redisLoader
import dev.hiorcraft.nex.base.paper.command.eventServerCommand
import dev.hiorcraft.nex.base.paper.config.EventServerConfigHolder
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override fun onEnable() {
        redisLoader.connect()
        eventServerCommand()
    }

    override suspend fun onDisableAsync() {
        redisLoader.disconnect()
    }
}

val eventServerConfigHolder = EventServerConfigHolder()
val eventServerConfig get() = eventServerConfigHolder.config