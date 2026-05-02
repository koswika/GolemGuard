package com.golemguard.listener;

import com.golemguard.manager.ZoneManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.util.Set;

public class GolemSpawnListener implements Listener {

    private static final Set<SpawnReason> NATURAL_REASONS = Set.of(
            SpawnReason.VILLAGE_DEFENSE,
            SpawnReason.NATURAL
    );

    private final ZoneManager zoneManager;

    public GolemSpawnListener(ZoneManager zoneManager) {
        this.zoneManager = zoneManager;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onGolemSpawn(CreatureSpawnEvent event) {
        if (event.getEntity().getType() != EntityType.IRON_GOLEM) return;

        if (NATURAL_REASONS.contains(event.getSpawnReason())) return;

        Location loc = event.getLocation();
        String worldName = loc.getWorld().getName();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        if (!zoneManager.isInsideAnyZone(worldName, x, z)) {
            event.setCancelled(true);
        }
    }
}