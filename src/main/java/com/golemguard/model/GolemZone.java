package com.golemguard.model;

// immutable data class representing a single allowed iron golem spawn zone

public final class GolemZone {

    private final String worldName;
    private final int centerX;
    private final int centerZ;
    private final int radius;

    public GolemZone(String worldName, int centerX, int centerZ, int radius) {
        this.worldName = worldName;
        this.centerX   = centerX;
        this.centerZ   = centerZ;
        this.radius    = radius;
    }

    public boolean contains(String world, int x, int z) {
        return worldName.equals(world)
                && Math.abs(centerX - x) <= radius
                && Math.abs(centerZ - z) <= radius;
    }

    public boolean isTooClose(GolemZone other, int threshold) {
        return worldName.equals(other.worldName)
                && Math.abs(centerX - other.centerX) < threshold
                && Math.abs(centerZ - other.centerZ) < threshold;
    }
    public double distanceTo(int x, int z) {
        return Math.sqrt(Math.pow(centerX - x, 2) + Math.pow(centerZ - z, 2));
    }

    public String getWorldName() { return worldName; }
    public int    getCenterX()   { return centerX;   }
    public int    getCenterZ()   { return centerZ;   }
    public int    getRadius()    { return radius;     }

    @Override
    public String toString() {
        return "world=" + worldName + " center=(" + centerX + ", " + centerZ + ") radius=±" + radius;
    }
}