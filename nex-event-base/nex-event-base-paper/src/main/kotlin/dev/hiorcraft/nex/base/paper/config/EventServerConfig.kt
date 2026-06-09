package dev.hiorcraft.nex.base.paper.config

import dev.hiorcraft.nex.base.api.common.state.EventServerState
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class EventServerConfig(
    val defaultState: EventServerState = EventServerState.CLOSED
)
