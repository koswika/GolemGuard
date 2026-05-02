package com.golemguard.manager;

import com.golemguard.model.GolemZone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ZoneManager {

    private static final int DUPLICATE_THRESHOLD = 10; // blocks

    private final ZoneRepository repository;
    private final List<GolemZone> zones = new ArrayList<>();

    public ZoneManager(ZoneRepository repository) {
        this.repository = repository;
        zones.addAll(repository.loadAll());
    }

    public boolean addZone(GolemZone candidate) {
        for (GolemZone existing : zones) {
            if (existing.isTooClose(candidate, DUPLICATE_THRESHOLD)) return false;
        }
        zones.add(candidate);
        repository.saveAll(zones);
        return true;
    }

    public Optional<GolemZone> removeNearest(String worldName, int x, int z) {
        GolemZone nearest = null;
        double bestDist   = Double.MAX_VALUE;

        for (GolemZone zone : zones) {
            if (!zone.getWorldName().equals(worldName)) continue;
            double dist = zone.distanceTo(x, z);
            if (dist < bestDist) {
                bestDist = dist;
                nearest  = zone;
            }
        }

        if (nearest == null) return Optional.empty();

        zones.remove(nearest);
        repository.saveAll(zones);
        return Optional.of(nearest);
    }

    public boolean isInsideAnyZone(String worldName, int x, int z) {
        for (GolemZone zone : zones) {
            if (zone.contains(worldName, x, z)) return true;
        }
        return false;
    }

    public List<GolemZone> getAllZones() {
        return Collections.unmodifiableList(zones);
    }

    public int getZoneCount() {
        return zones.size();
    }
}