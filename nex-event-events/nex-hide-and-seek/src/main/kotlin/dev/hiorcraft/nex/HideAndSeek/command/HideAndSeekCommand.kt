@file:Suppress("SpellCheckingInspection")

package dev.hiorcraft.nex.hideandseek.command

import com.mojang.brigadier.arguments.StringArgumentType
import dev.hiorcraft.nex.hideandseek.game.HideAndSeekGame
import dev.hiorcraft.nex.hideandseek.game.GameState
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player

@Suppress("UnstableApiUsage")
object HideAndSeekCommand {

    fun register(commands: Commands, game: HideAndSeekGame) {
        commands.register(
            Commands.literal("has")
                .then(
                    Commands.literal("join")
                        .executes { ctx ->
                            val player = ctx.source.sender as? Player ?: return@executes 0
                            game.join(player)
                            1
                        }
                )
                .then(
                    Commands.literal("leave")
                        .executes { ctx ->
                            val player = ctx.source.sender as? Player ?: return@executes 0
                            if (player !in game.players) {
                                player.sendMessage(Component.text("Du bist nicht im Spiel!", NamedTextColor.RED))
                                return@executes 0
                            }
                            game.leave(player)
                            1
                        }
                )
                .then(
                    Commands.literal("start")
                        .requires { it.sender.hasPermission("hideandseek.admin") }
                        .executes { ctx ->
                            val sender = ctx.source.sender
                            if (game.state != GameState.WAITING) {
                                sender.sendMessage(Component.text("Das Spiel läuft bereits!", NamedTextColor.RED))
                                return@executes 0
                            }
                            game.start()
                            1
                        }
                )
                .then(
                    Commands.literal("stop")
                        .requires { it.sender.hasPermission("hideandseek.admin") }
                        .executes { ctx ->
                            val sender = ctx.source.sender
                            if (game.state == GameState.WAITING) {
                                sender.sendMessage(Component.text("Kein Spiel aktiv!", NamedTextColor.RED))
                                return@executes 0
                            }
                            game.forceEnd()
                            1
                        }
                )
                .then(
                    Commands.literal("status")
                        .executes { ctx ->
                            val sender = ctx.source.sender
                            sender.sendMessage(
                                Component.text("Status: ", NamedTextColor.YELLOW)
                                    .append(Component.text(game.state.name, NamedTextColor.GOLD))
                            )
                            sender.sendMessage(
                                Component.text("Spieler: ${game.players.size} | Verstecker: ${game.hiders.size} | Sucher: ${game.seeker?.name ?: "-"}", NamedTextColor.GRAY)
                            )
                            if (game.isManualSeekerSelectionEnabled) {
                                sender.sendMessage(
                                    Component.text(
                                        "Gewählter Sucher: ${game.selectedSeekerName ?: "-"}",
                                        NamedTextColor.GRAY
                                    )
                                )
                            }
                            1
                        }
                )
                .then(
                    Commands.literal("seeker")
                        .requires { it.sender.hasPermission("hideandseek.admin") }
                        .then(
                            Commands.argument("player", StringArgumentType.word())
                                .executes { ctx ->
                                    val sender = ctx.source.sender
                                    if (!game.isManualSeekerSelectionEnabled) {
                                        sender.sendMessage(
                                            Component.text(
                                                "Der Modus 'COMMAND' muss in der Config gesetzt sein.",
                                                NamedTextColor.RED
                                            )
                                        )
                                        return@executes 0
                                    }
                                    if (game.state != GameState.WAITING) {
                                        sender.sendMessage(Component.text("Sucher kann nur vor dem Start gesetzt werden.", NamedTextColor.RED))
                                        return@executes 0
                                    }
                                    val input = StringArgumentType.getString(ctx, "player")
                                    if (input.equals("clear", ignoreCase = true)) {
                                        game.clearSelectedSeeker()
                                        sender.sendMessage(Component.text("Ausgewählter Sucher wurde entfernt.", NamedTextColor.YELLOW))
                                        return@executes 1
                                    }
                                    val target = Bukkit.getPlayerExact(input)
                                    if (target == null || target !in game.players) {
                                        sender.sendMessage(
                                            Component.text(
                                                "Spieler nicht gefunden oder nicht im Spiel. Nutze /has join zuerst.",
                                                NamedTextColor.RED
                                            )
                                        )
                                        return@executes 0
                                    }
                                    if (!game.selectSeeker(target)) {
                                        sender.sendMessage(Component.text("Sucher konnte nicht gesetzt werden.", NamedTextColor.RED))
                                        return@executes 0
                                    }
                                    sender.sendMessage(
                                        Component.text("Sucher wurde auf ${target.name} gesetzt.", NamedTextColor.GREEN)
                                    )
                                    1
                                }
                        )
                )
                .build(),
            "Hide and Seek Befehl",
            listOf("hideandseek", "hns")
        )
    }
}
