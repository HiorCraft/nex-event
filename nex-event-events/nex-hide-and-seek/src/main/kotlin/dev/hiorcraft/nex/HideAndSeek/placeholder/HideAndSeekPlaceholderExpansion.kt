package dev.hiorcraft.nex.hideandseek.placeholder

import dev.hiorcraft.nex.hideandseek.game.HideAndSeekGame
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

class HideAndSeekPlaceholderExpansion(
    private val game: HideAndSeekGame
) : PlaceholderExpansion() {

    override fun getIdentifier(): String = "has"

    override fun getAuthor(): String = "hiorcraft"

    override fun getVersion(): String = game.plugin.pluginMeta.version

    override fun persist(): Boolean = true

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        return when (params.lowercase()) {
            "timer" -> game.timerSecondsRemaining.toString()
            "alive", "hiders_alive" -> game.aliveHidersCount.toString()
            "dead", "hiders_dead" -> game.deadHidersCount.toString()
            else -> null
        }
    }
}
