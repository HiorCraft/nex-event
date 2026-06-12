package de.danilo.nex.Warzone.commands

import de.danilo.nex.Warzone.util.PermissionRegistry
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.anyExecutor

fun NexWarzonCommand() = commandTree("Nexwarzone") {
    withPermission(PermissionRegistry.COMMAND_WARZONE)

    literalArgument("reload")
    withPermission(PermissionRegistry.COMMAND_WARZONE_RELOAD)

    anyExecutor { executor, _ ->

    }
}
