package dev.hiorcraft.nex.base.paper.command

import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.argument
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.hiorcraft.nex.base.api.common.state.EventServerState
import dev.hiorcraft.nex.base.core.access.eventServerAccess
import dev.hiorcraft.nex.base.paper.eventServerConfigHolder
import dev.hiorcraft.nex.base.paper.permission.PermissionRegistry

private val settableStates = EventServerState.entries.filter { it != EventServerState.UNKNOWN }

fun eventServerCommand() = commandTree("eventserver") {
    withPermission(PermissionRegistry.COMMAND_EVENT_SERVER)

    anyExecutor { executor, _ ->
        val state = eventServerAccess.getEventServerState()
        executor.sendText {
            appendInfoPrefix()
            info("Server-Status: ")
            variableValue(state.displayName)
        }
    }

    literalArgument("state") {
        anyExecutor { executor, _ ->
            val state = eventServerAccess.getEventServerState()
            executor.sendText {
                appendInfoPrefix()
                info("Server-Status: ")
                variableValue(state.displayName)
                info(" | Spieler können joinen: ")
                variableValue(if (state.playerJoin) "Ja" else "Nein")
            }
        }
    }

    literalArgument("set") {
        withPermission(PermissionRegistry.COMMAND_EVENT_SERVER_CHANGE_STATE)
        argument(eventServerStateArgument("state")) {
            anyExecutor { executor, args ->
                val state: EventServerState by args
                val current = eventServerAccess.getEventServerState()

                if (state == current) {
                    executor.sendText {
                        appendInfoPrefix()
                        info("Der Server-Status ist bereits ")
                        variableValue(state.displayName)
                        info(".")
                    }
                    return@anyExecutor
                }

                eventServerAccess.setEventServerState(state)
                executor.sendText {
                    appendSuccessPrefix()
                    success("Server-Status auf ")
                    variableValue(state.displayName)
                    success(" gesetzt.")
                }
            }
        }
    }

    literalArgument("reload") {
        withPermission(PermissionRegistry.COMMAND_EVENT_SERVER_RELOAD)
        anyExecutor { executor, _ ->
            eventServerConfigHolder.reload()
            executor.sendText {
                appendSuccessPrefix()
                success("Die Event Server Konfiguration wurde neu geladen.")
            }
        }
    }
}

private fun eventServerStateArgument(nodeName: String): Argument<EventServerState> =
    CustomArgument(StringArgument(nodeName)) { info ->
        settableStates.find { it.name.equals(info.input(), ignoreCase = true) }
            ?: throw CustomArgument.CustomArgumentException.fromString(
                "Ungültiger Status. Erlaubt: ${settableStates.joinToString(", ") { it.name }}"
            )
    }.replaceSuggestions(
        ArgumentSuggestions.strings(*settableStates.map { it.name }.toTypedArray())
    )