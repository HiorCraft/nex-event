package dev.hiorcraft.nex.base.core.access

import dev.hiorcraft.nex.base.api.common.state.EventServerState
import dev.hiorcraft.nex.base.core.loader.redisLoader

val eventServerAccess = EventServerAccess()

class EventServerAccess {
    fun getEventServerState(): EventServerState = redisLoader.eventServerState.get()
    fun setEventServerState(state: EventServerState) = redisLoader.eventServerState.set(state)
}
