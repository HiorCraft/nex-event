package de.danilo.nex.Warzone

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {

    override fun onEnable() {

        val manager = server.pluginManager


        logger.info("nex-Warzone has started.")
    }

    override fun onDisable() {
        logger.info("by <3")

    }
}
