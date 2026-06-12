package de.danilo.nex.Warzone.Config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class LobbyConfig(
    val timer: Long,
) {

}