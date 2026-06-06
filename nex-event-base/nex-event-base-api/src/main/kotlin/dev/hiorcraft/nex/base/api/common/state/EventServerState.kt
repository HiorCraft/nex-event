package dev.hiorcraft.nex.base.api.common.state

import kotlinx.serialization.Serializable

@Serializable
enum class EventServerState(val playerJoin: Boolean, val displayName: String) {
    OPEN(true, "Offen"),
    CLOSED(false, "Geschlossen"),
    UNKNOWN(false, "Unbekannt");

    fun next(): EventServerState = when (this) {
        OPEN -> CLOSED
        CLOSED -> OPEN
        UNKNOWN -> UNKNOWN
    }
}
