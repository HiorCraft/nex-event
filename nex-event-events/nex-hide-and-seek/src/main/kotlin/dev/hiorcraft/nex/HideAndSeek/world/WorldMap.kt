package dev.hiorcraft.nex.hideandseek.world

import org.bukkit.Location
import org.bukkit.World

data class WorldMap(
    val name: String,
    val worldName: String,
    val borderCenter: StoredLocation
)

data class StoredLocation(
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float
) {
    fun toLocation(world: World): Location = Location(world, x, y, z, yaw, pitch)

    companion object {
        fun fromLocation(location: Location): StoredLocation = StoredLocation(
            x = location.x,
            y = location.y,
            z = location.z,
            yaw = location.yaw,
            pitch = location.pitch
        )
    }
}
