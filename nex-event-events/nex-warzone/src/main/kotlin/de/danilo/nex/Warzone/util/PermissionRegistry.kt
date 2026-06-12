package de.danilo.nex.Warzone.util

import dev.slne.surf.api.paper.permission.PermissionRegistry

object PermissionRegistry : PermissionRegistry() {

    private const val PREFIX = "nex.warzone."
    private const val COMMAND_PREFIX = "$PREFIX.command"

    val COMMAND_WARZONE = create("$COMMAND_PREFIX.warzone")
    val COMMAND_WARZONE_RELOAD = create("$COMMAND_PREFIX.warzone.reload")

}