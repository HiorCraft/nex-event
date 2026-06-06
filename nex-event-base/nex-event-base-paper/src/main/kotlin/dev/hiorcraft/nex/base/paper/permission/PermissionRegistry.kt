package dev.hiorcraft.nex.base.paper.permission

import dev.slne.surf.api.paper.permission.PermissionRegistry

object PermissionRegistry : PermissionRegistry() {
    const val PREFIX = "nex.event"

    val COMMAND_EVENT_SERVER = create("$PREFIX.command.eventserver")
    val COMMAND_EVENT_SERVER_CHANGE_STATE = create("$PREFIX.command.eventserver.changestate")
    val COMMAND_EVENT_SERVER_RELOAD = create("$PREFIX.command.eventserver.reload")
}
