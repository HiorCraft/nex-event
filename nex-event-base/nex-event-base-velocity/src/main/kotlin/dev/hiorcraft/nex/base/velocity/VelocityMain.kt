package dev.hiorcraft.nex.base.velocity

import jakarta.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.proxy.ProxyServer

class VelocityMain @Inject constructor(
    private val server: ProxyServer
) {

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
    }
}
