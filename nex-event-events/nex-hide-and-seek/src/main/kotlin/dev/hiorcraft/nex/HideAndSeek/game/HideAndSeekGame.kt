package dev.hiorcraft.nex.HideAndSeek.game

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.time.Duration

class HideAndSeekGame(val plugin: JavaPlugin) {

    var state: GameState = GameState.WAITING
        private set

    val players = mutableSetOf<Player>()
    val hiders = mutableSetOf<Player>()
    var seeker: Player? = null

    private val hideSeconds = 30
    private val seekSeconds = 180
    private var task: BukkitRunnable? = null

    private var gameWorld: World? = null
    private var originalBorderSize = 60_000_000.0

    val glowPearlKey = NamespacedKey(plugin, "glow_pearl")
    val elytraPearlKey = NamespacedKey(plugin, "elytra_pearl")
    var seekerElytraActive = false

    fun join(player: Player): Boolean {
        if (state != GameState.WAITING) {
            player.sendMessage(Component.text("Das Spiel läuft bereits!", NamedTextColor.RED))
            return false
        }
        if (!players.add(player)) {
            player.sendMessage(Component.text("Du bist bereits im Spiel!", NamedTextColor.RED))
            return false
        }
        broadcast(Component.text("${player.name} ist beigetreten! (${players.size} Spieler)", NamedTextColor.GREEN))
        return true
    }

    fun leave(player: Player) {
        if (!players.remove(player)) return
        hiders.remove(player)
        resetPlayerScale(player)

        if (seeker == player) {
            seeker = null
            if (state == GameState.HIDING || state == GameState.SEEKING) {
                broadcast(Component.text("Der Sucher hat das Spiel verlassen. Das Spiel wird beendet.", NamedTextColor.RED))
                forceEnd()
                return
            }
        }

        broadcast(Component.text("${player.name} hat das Spiel verlassen.", NamedTextColor.YELLOW))

        if (state == GameState.SEEKING && hiders.isEmpty()) {
            endWithSeekerWin()
        }
    }

    fun start(): Boolean {
        if (state != GameState.WAITING) return false
        if (players.size < 2) {
            broadcast(Component.text("Mindestens 2 Spieler werden benötigt!", NamedTextColor.RED))
            return false
        }

        val picked = players.random()
        seeker = picked
        hiders.addAll(players.filter { it != picked })

        state = GameState.HIDING
        applyHiderScale()

        broadcast(
            Component.text("Das Spiel beginnt! ", NamedTextColor.GOLD)
                .append(Component.text(picked.name, NamedTextColor.RED))
                .append(Component.text(" ist der Sucher!", NamedTextColor.GOLD))
        )

        picked.showTitle(
            Title.title(
                Component.text("Du bist der Sucher!", NamedTextColor.RED),
                Component.text("Warte $hideSeconds Sekunden..."),
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500))
            )
        )

        hiders.forEach { hider ->
            hider.showTitle(
                Title.title(
                    Component.text("Versteck dich!", NamedTextColor.GREEN),
                    Component.text("Du hast $hideSeconds Sekunden!"),
                    Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500))
                )
            )
        }

        startHidingPhase()
        return true
    }

    private fun applyHiderScale() {
        hiders.forEach { hider ->
            hider.getAttribute(Attribute.GENERIC_SCALE)?.baseValue = 0.75
        }
    }

    private fun resetPlayerScale(player: Player) {
        player.getAttribute(Attribute.GENERIC_SCALE)?.baseValue = 1.0
    }

    private fun giveSeekingItems() {
        val s = seeker ?: return
        s.inventory.clear()
        s.inventory.setItem(0, createSword())
        s.inventory.setItem(1, createGlowPearl())
        s.inventory.setItem(2, createElytraPearl())
        s.inventory.heldItemSlot = 0
    }

    private fun createSword(): ItemStack {
        val sword = ItemStack(Material.IRON_SWORD)
        val meta = sword.itemMeta
        meta.displayName(Component.text("Sucher-Schwert", NamedTextColor.RED))
        sword.itemMeta = meta
        return sword
    }

    private fun createGlowPearl(): ItemStack {
        val pearl = ItemStack(Material.ENDER_PEARL)
        val meta = pearl.itemMeta
        meta.displayName(Component.text("Leuchtperle", NamedTextColor.GOLD))
        meta.lore(listOf(Component.text("Alle Verstecker leuchten 10s!", NamedTextColor.GRAY)))
        meta.persistentDataContainer.set(glowPearlKey, PersistentDataType.BOOLEAN, true)
        pearl.itemMeta = meta
        return pearl
    }

    private fun createElytraPearl(): ItemStack {
        val pearl = ItemStack(Material.ENDER_PEARL)
        val meta = pearl.itemMeta
        meta.displayName(Component.text("Elytraperle", NamedTextColor.AQUA))
        meta.lore(listOf(Component.text("Einmalige Elytra!", NamedTextColor.GRAY)))
        meta.persistentDataContainer.set(elytraPearlKey, PersistentDataType.BOOLEAN, true)
        pearl.itemMeta = meta
        return pearl
    }

    fun useGlowPearl() {
        hiders.forEach { hider ->
            hider.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 200, 0))
        }
        broadcast(Component.text("Alle Verstecker leuchten für 10 Sekunden!", NamedTextColor.GOLD))
    }

    fun useElytraPearl(s: Player) {
        seekerElytraActive = true
        s.inventory.chestplate = ItemStack(Material.ELYTRA)
        s.velocity = s.velocity.add(Vector(0.0, 2.5, 0.0))
        s.sendMessage(Component.text("Elytra aktiviert! Drücke Leertaste zum Gleiten!", NamedTextColor.AQUA))
        s.sendMessage(Component.text("Die Elytra wird nach der Landung entfernt.", NamedTextColor.GRAY))
    }

    fun onSeekerLanded() {
        if (!seekerElytraActive) return
        seekerElytraActive = false
        seeker?.inventory?.chestplate = null
        seeker?.sendMessage(Component.text("Elytra entfernt.", NamedTextColor.GRAY))
    }

    private fun startBorderShrink() {
        val world = seeker?.world ?: return
        gameWorld = world
        val border = world.worldBorder
        originalBorderSize = border.size
        border.center = world.spawnLocation
        border.size = 200.0
        border.setSize(30.0, seekSeconds.toLong())
        broadcast(Component.text("Die Map schrumpft! Bleibt innerhalb der Grenze!", NamedTextColor.RED))
    }

    private fun resetBorder() {
        val world = gameWorld ?: return
        world.worldBorder.size = originalBorderSize
        gameWorld = null
    }

    private fun startHidingPhase() {
        var remaining = hideSeconds
        task = object : BukkitRunnable() {
            override fun run() {
                if (remaining <= 0) {
                    cancel()
                    startSeekingPhase()
                    return
                }
                if (remaining <= 10 || remaining % 10 == 0) {
                    seeker?.sendActionBar(
                        Component.text("Suche beginnt in ${remaining}s", NamedTextColor.RED)
                    )
                }
                remaining--
            }
        }
        task!!.runTaskTimer(plugin, 0L, 20L)
    }

    private fun startSeekingPhase() {
        state = GameState.SEEKING
        giveSeekingItems()
        startBorderShrink()

        seeker?.showTitle(
            Title.title(
                Component.text("Los!", NamedTextColor.RED),
                Component.text("Finde alle Verstecker!"),
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(2), Duration.ofMillis(500))
            )
        )

        broadcast(
            Component.text("Die Suche beginnt! ", NamedTextColor.RED)
                .append(Component.text("${seeker?.name}", NamedTextColor.GOLD))
                .append(Component.text(" sucht jetzt!", NamedTextColor.RED))
        )

        var remaining = seekSeconds
        task = object : BukkitRunnable() {
            override fun run() {
                if (remaining <= 0) {
                    cancel()
                    endWithHiderWin()
                    return
                }
                if (remaining % 30 == 0 || remaining <= 10) {
                    broadcast(
                        Component.text("Noch ${remaining}s | Verstecker übrig: ${hiders.size}", NamedTextColor.YELLOW)
                    )
                }
                remaining--
            }
        }
        task!!.runTaskTimer(plugin, 0L, 20L)
    }

    fun findHider(hider: Player) {
        if (!hiders.remove(hider)) return
        resetPlayerScale(hider)

        hider.sendMessage(Component.text("Du wurdest gefunden!", NamedTextColor.RED))
        broadcast(
            Component.text("${hider.name} wurde gefunden! ", NamedTextColor.RED)
                .append(Component.text("Noch ${hiders.size} Verstecker übrig.", NamedTextColor.YELLOW))
        )

        if (hiders.isEmpty()) {
            endWithSeekerWin()
        }
    }

    private fun endWithSeekerWin() {
        task?.cancel()
        state = GameState.ENDED
        resetBorder()

        broadcast(
            Component.text("${seeker?.name} hat gewonnen! ", NamedTextColor.GOLD)
                .append(Component.text("Alle Verstecker wurden gefunden!", NamedTextColor.YELLOW))
        )

        scheduleReset()
    }

    private fun endWithHiderWin() {
        state = GameState.ENDED
        resetBorder()

        broadcast(
            Component.text("Die Verstecker haben gewonnen! ", NamedTextColor.GREEN)
                .append(Component.text("Die Zeit ist abgelaufen!", NamedTextColor.YELLOW))
        )

        scheduleReset()
    }

    fun forceEnd() {
        task?.cancel()
        state = GameState.ENDED
        resetBorder()
        broadcast(Component.text("Das Spiel wurde beendet.", NamedTextColor.RED))
        scheduleReset()
    }

    private fun scheduleReset() {
        object : BukkitRunnable() {
            override fun run() {
                players.forEach { resetPlayerScale(it) }
                seeker?.inventory?.clear()
                players.clear()
                hiders.clear()
                seeker = null
                seekerElytraActive = false
                state = GameState.WAITING
            }
        }.runTaskLater(plugin, 100L)
    }

    private fun broadcast(message: Component) {
        players.forEach { it.sendMessage(message) }
    }
}