package com.golemguard.manager;

import com.golemguard.model.GolemZone;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ZoneRepository {

    private static final String FILE_NAME = "golem_zones.yml";
    private static final String ZONES_KEY = "zones";

    private final JavaPlugin plugin;
    private final File       file;
    private FileConfiguration config;

    public ZoneRepository(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file   = new File(plugin.getDataFolder(), FILE_NAME);
    }

    public List<GolemZone> loadAll() {
        ensureFileExists();
        config = YamlConfiguration.loadConfiguration(file);

        List<?> raw = config.getList(ZONES_KEY);
        if (raw == null || raw.isEmpty()) {
            plugin.getLogger().info("[GolemGuard] No saved zones found");
            return new ArrayList<>();
        }

        List<GolemZone> zones = new ArrayList<>();
        for (Object entry : raw) {
            if (entry instanceof Map<?, ?> map) {
                try {
                    String world = (String)  map.get("world");
                    int    x     = ((Number) map.get("x")).intValue();
                    int    z     = ((Number) map.get("z")).intValue();
                    int    r     = ((Number) map.get("radius")).intValue();
                    zones.add(new GolemZone(world, x, z, r));
                } catch (Exception e) {
                    plugin.getLogger().warning("[GolemGuard] Skipping malformed zone entry: " + entry);
                }
            }
        }

        plugin.getLogger().info("[GolemGuard] Loaded " + zones.size() + " zone(s) from disk.");
        return zones;
    }

    public void saveAll(List<GolemZone> zones) {
        ensureFileExists();
        if (config == null) config = YamlConfiguration.loadConfiguration(file);

        List<Map<String, Object>> list = new ArrayList<>();
        for (GolemZone zone : zones) {
            list.add(Map.of(
                    "world",  zone.getWorldName(),
                    "x",      zone.getCenterX(),
                    "z",      zone.getCenterZ(),
                    "radius", zone.getRadius()
            ));
        }

        config.set(ZONES_KEY, list);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("[GolemGuard] Could not save golem_zones.yml: " + e.getMessage());
        }
    }

    private void ensureFileExists() {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("[GolemGuard] Could not create golem_zones.yml: " + e.getMessage());
            }
        }
    }
}