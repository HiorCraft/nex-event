package dev.hiorcraft.nex.HideAndSeek.listener

import dev.hiorcraft.nex.HideAndSeek.game.GameState
import dev.hiorcraft.nex.HideAndSeek.game.HideAndSeekGame
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityToggleGlideEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.persistence.PersistentDataType

class HideAndSeekListener(private val game: HideAndSeekGame) : Listener {

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (event.player in game.players) {
            game.leave(event.player)
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        if (game.state != GameState.SEEKING) return

        val attacker = event.damager as? Player ?: return
        val victim = event.entity as? Player ?: return

        if (attacker != game.seeker) return
        if (victim !in game.hiders) return

        event.isCancelled = true
        game.findHider(victim)
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (game.state != GameState.SEEKING) return
        val player = event.player
        if (player != game.seeker) return
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return

        val item = player.inventory.itemInMainHand
        val meta = item.itemMeta ?: return
        val pdc = meta.persistentDataContainer

        when {
            pdc.has(game.glowPearlKey, PersistentDataType.BOOLEAN) -> {
                event.isCancelled = true
                player.inventory.setItemInMainHand(null)
                game.useGlowPearl()
            }
            pdc.has(game.elytraPearlKey, PersistentDataType.BOOLEAN) -> {
                event.isCancelled = true
                player.inventory.setItemInMainHand(null)
                game.useElytraPearl(player)
            }
        }
    }

    @EventHandler
    fun onEntityToggleGlide(event: EntityToggleGlideEvent) {
        if (game.state != GameState.SEEKING) return
        val player = event.entity as? Player ?: return
        if (player != game.seeker) return
        if (!event.isGliding && game.seekerElytraActive) {
            game.onSeekerLanded()
        }
    }
}