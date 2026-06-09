package dev.hiorcraft.nex.hideandseek.world

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.plugin.java.JavaPlugin

class WorldMapManager(private val plugin: JavaPlugin) {

    private val maps = linkedMapOf<String, WorldMap>()
    var activeMapName: String? = null
        private set

    val allMaps: Collection<WorldMap>
        get() = maps.values

    val activeMap: WorldMap?
        get() = activeMapName?.let { maps[it] }

    fun loadFromConfig() {
        maps.clear()
        val root = plugin.config.getConfigurationSection("maps")
        root?.getKeys(false)?.forEach { rawName ->
            val section = root.getConfigurationSection(rawName) ?: return@forEach
            val worldName = section.getString("world") ?: return@forEach
            val center = readLocation(section.getConfigurationSection("border-center")) ?: return@forEach
            maps[rawName.lowercase()] = WorldMap(rawName.lowercase(), worldName, center)
            ensureWorldLoaded(worldName)
        }

        val configuredActive = plugin.config.getString("active-map")?.lowercase()
        activeMapName = configuredActive?.takeIf { maps.containsKey(it) }
        activeMap?.let { ensureWorldLoaded(it.worldName) }
    }

    fun createMap(name: String, worldName: String, borderCenter: StoredLocation): Boolean {
        val key = name.lowercase()
        if (maps.containsKey(key)) return false
        maps[key] = WorldMap(key, worldName, borderCenter)
        if (activeMapName == null) {
            activeMapName = key
        }
        saveToConfig()
        return true
    }

    fun deleteMap(name: String): Boolean {
        val key = name.lowercase()
        val removed = maps.remove(key) ?: return false
        if (activeMapName == removed.name) {
            activeMapName = maps.keys.firstOrNull()
        }
        saveToConfig()
        return true
    }

    fun setActiveMap(name: String): WorldMap? {
        val key = name.lowercase()
        val map = maps[key] ?: return null
        activeMapName = key
        ensureWorldLoaded(map.worldName)
        saveToConfig()
        return map
    }

    fun updateMap(name: String, worldName: String, borderCenter: StoredLocation): WorldMap? {
        val key = name.lowercase()
        val map = maps[key] ?: return null
        maps[key] = map.copy(worldName = worldName, borderCenter = borderCenter)
        saveToConfig()
        return maps[key]
    }

    fun getMap(name: String): WorldMap? = maps[name.lowercase()]

    fun resolveWorld(map: WorldMap): World? = ensureWorldLoaded(map.worldName)

    private fun ensureWorldLoaded(worldName: String): World? {
        return Bukkit.getWorld(worldName) ?: WorldCreator.name(worldName).createWorld()
    }

    private fun saveToConfig() {
        plugin.config.set("active-map", activeMapName)
        plugin.config.set("maps", null)

        val mapsSection = plugin.config.createSection("maps")
        maps.values.forEach { map ->
            val section = mapsSection.createSection(map.name)
            section.set("world", map.worldName)
            val center = section.createSection("border-center")
            center.set("x", map.borderCenter.x)
            center.set("y", map.borderCenter.y)
            center.set("z", map.borderCenter.z)
            center.set("yaw", map.borderCenter.yaw.toDouble())
            center.set("pitch", map.borderCenter.pitch.toDouble())
        }

        plugin.saveConfig()
    }

    private fun readLocation(section: ConfigurationSection?): StoredLocation? {
        section ?: return null
        return StoredLocation(
            x = section.getDouble("x"),
            y = section.getDouble("y"),
            z = section.getDouble("z"),
            yaw = section.getDouble("yaw").toFloat(),
            pitch = section.getDouble("pitch").toFloat()
        )
    }
}
