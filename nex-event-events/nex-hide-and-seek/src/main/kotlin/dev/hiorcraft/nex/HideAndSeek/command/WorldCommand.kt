package dev.hiorcraft.nex.hideandseek.command

import com.mojang.brigadier.arguments.StringArgumentType
import dev.hiorcraft.nex.hideandseek.world.StoredLocation
import dev.hiorcraft.nex.hideandseek.world.WorldMapManager
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

@Suppress("UnstableApiUsage")
object WorldCommand {

    fun register(commands: Commands, worldMapManager: WorldMapManager) {
        commands.register(
            Commands.literal("world")
                .executes { ctx ->
                    val sender = ctx.source.sender
                    val mapNames = worldMapManager.allMaps.joinToString(", ") { it.name }
                    sender.sendMessage(
                        Component.text(
                            if (mapNames.isBlank()) "Keine Maps angelegt." else "Maps: $mapNames",
                            NamedTextColor.YELLOW
                        )
                    )
                    sender.sendMessage(
                        Component.text(
                            "Aktive Map: ${worldMapManager.activeMapName ?: "-"}",
                            NamedTextColor.GRAY
                        )
                    )
                    1
                }
                .then(
                    Commands.literal("create")
                        .requires { it.sender.hasPermission("hideandseek.admin") }
                        .then(
                            Commands.argument("name", StringArgumentType.word())
                                .executes { ctx ->
                                    val player = ctx.source.sender as? Player ?: return@executes 0
                                    val name = StringArgumentType.getString(ctx, "name")
                                    val created = worldMapManager.createMap(
                                        name = name,
                                        worldName = player.world.name,
                                        borderCenter = StoredLocation.fromLocation(player.location)
                                    )
                                    if (!created) {
                                        player.sendMessage(Component.text("Map existiert bereits.", NamedTextColor.RED))
                                        return@executes 0
                                    }
                                    player.sendMessage(Component.text("Map '$name' erstellt.", NamedTextColor.GREEN))
                                    1
                                }
                        )
                )
                .then(
                    Commands.literal("delete")
                        .requires { it.sender.hasPermission("hideandseek.admin") }
                        .then(
                            Commands.argument("name", StringArgumentType.word())
                                .executes { ctx ->
                                    val sender = ctx.source.sender
                                    val name = StringArgumentType.getString(ctx, "name")
                                    if (!worldMapManager.deleteMap(name)) {
                                        sender.sendMessage(Component.text("Map nicht gefunden.", NamedTextColor.RED))
                                        return@executes 0
                                    }
                                    sender.sendMessage(Component.text("Map '$name' gelöscht.", NamedTextColor.GREEN))
                                    1
                                }
                        )
                )
                .then(
                    Commands.literal("join")
                        .then(
                            Commands.argument("name", StringArgumentType.word())
                                .executes { ctx ->
                                    val player = ctx.source.sender as? Player ?: return@executes 0
                                    val name = StringArgumentType.getString(ctx, "name")
                                    val map = worldMapManager.setActiveMap(name)
                                    if (map == null) {
                                        player.sendMessage(Component.text("Map nicht gefunden.", NamedTextColor.RED))
                                        return@executes 0
                                    }
                                    val world = worldMapManager.resolveWorld(map)
                                    if (world == null) {
                                        player.sendMessage(Component.text("Welt konnte nicht geladen werden.", NamedTextColor.RED))
                                        return@executes 0
                                    }
                                    player.teleport(map.borderCenter.toLocation(world))
                                    player.sendMessage(Component.text("Map '$name' betreten und aktiv gesetzt.", NamedTextColor.GREEN))
                                    1
                                }
                        )
                )
                .then(
                    Commands.literal("edit")
                        .requires { it.sender.hasPermission("hideandseek.admin") }
                        .then(
                            Commands.argument("name", StringArgumentType.word())
                                .executes { ctx ->
                                    val player = ctx.source.sender as? Player ?: return@executes 0
                                    val name = StringArgumentType.getString(ctx, "name")
                                    val updated = worldMapManager.updateMap(
                                        name = name,
                                        worldName = player.world.name,
                                        borderCenter = StoredLocation.fromLocation(player.location)
                                    )
                                    if (updated == null) {
                                        player.sendMessage(Component.text("Map nicht gefunden.", NamedTextColor.RED))
                                        return@executes 0
                                    }
                                    player.sendMessage(
                                        Component.text(
                                            "Map '$name' aktualisiert. Border-Mittelpunkt wurde gesetzt.",
                                            NamedTextColor.GREEN
                                        )
                                    )
                                    1
                                }
                                .then(
                                    Commands.literal("center")
                                        .executes { ctx ->
                                            val player = ctx.source.sender as? Player ?: return@executes 0
                                            val name = StringArgumentType.getString(ctx, "name")
                                            val updated = worldMapManager.updateBorderCenter(
                                                name = name,
                                                borderCenter = StoredLocation.fromLocation(player.location)
                                            )
                                            if (updated == null) {
                                                player.sendMessage(Component.text("Map nicht gefunden.", NamedTextColor.RED))
                                                return@executes 0
                                            }
                                            player.sendMessage(
                                                Component.text(
                                                    "Border-Mittelpunkt der Map '$name' auf deine aktuelle Position gesetzt.",
                                                    NamedTextColor.GREEN
                                                )
                                            )
                                            1
                                        }
                                )
                        )
                )
                .build(),
            "World Map Verwaltung"
        )
    }
}
