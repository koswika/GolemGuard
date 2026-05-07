package com.golemguard.command;

import com.golemguard.manager.ZoneManager;
import com.golemguard.model.GolemZone;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class GolemZoneCommand implements CommandExecutor, TabCompleter {

    private static final String PERMISSION = "golemguard.zone";
    private static final int DEFAULT_RADIUS = 70;

    private final ZoneManager zoneManager;

    public GolemZoneCommand(ZoneManager zoneManager) {
        this.zoneManager = zoneManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be run in-game.");
            return true;
        }

        if (!player.hasPermission(PERMISSION)) {
            player.sendMessage("§cYou don't have permission to manage golem zones.");
            return true;
        }

        if (args.length == 0) {
            handleSet(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "remove" -> handleRemove(player);
            case "list"   -> handleList(player);
            default       -> sendUsage(player);
        }

        return true;
    }

    private void handleSet(Player player) {
        Location loc     = player.getLocation();
        String worldName = loc.getWorld().getName();
        int    centerX   = loc.getBlockX();
        int    centerZ   = loc.getBlockZ();

        GolemZone zone = new GolemZone(worldName, centerX, centerZ, DEFAULT_RADIUS);

        if (!zoneManager.addZone(zone)) {
            player.sendMessage("§eA zone already exists very close to here. Use §6/golemzone list §eto see all zones.");
            return;
        }

        player.sendMessage("§aZone created! Iron Golems can spawn within §b±" + DEFAULT_RADIUS
                + " blocks §aof §b(" + centerX + ", " + centerZ + ")§a in §b" + worldName + "§a.");
        player.sendMessage("§7Zone saved to disk");
    }

    private void handleRemove(Player player) {
        Location loc = player.getLocation();
        String worldName = loc.getWorld().getName();

        Optional<GolemZone> removed = zoneManager.removeNearest(worldName, loc.getBlockX(), loc.getBlockZ());

        if (removed.isEmpty()) {
            player.sendMessage("§cNo zones found in world §e" + worldName + "§c.");
            return;
        }

        GolemZone zone = removed.get();
        player.sendMessage("§aRemoved zone at §b(" + zone.getCenterX() + ", " + zone.getCenterZ()
                + ")§a in §b" + zone.getWorldName() + "§a. §7(Saved to disk)");
    }

    private void handleList(Player player) {
        List<GolemZone> all = zoneManager.getAllZones();

        if (all.isEmpty()) {
            player.sendMessage("§eNo iron golem zones are currently set.");
            return;
        }

        player.sendMessage("§6--- GolemGuard Zones (" + all.size() + ") ---");
        for (int i = 0; i < all.size(); i++) {
            GolemZone z = all.get(i);
            player.sendMessage("§7#" + (i + 1) + " §fWorld: §b" + z.getWorldName()
                    + " §fCenter: §b(" + z.getCenterX() + ", " + z.getCenterZ() + ")"
                    + " §fRadius: §b±" + z.getRadius());
        }
    }

    private void sendUsage(Player player) {
        player.sendMessage("§eUsage:");
        player.sendMessage("§7  /golemzone §f— Set zone at your location");
        player.sendMessage("§7  /golemzone remove §f— Remove nearest zone");
        player.sendMessage("§7  /golemzone list §f— List all zones");
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return List.of("remove", "list");
        }
        return List.of();
    }
}
