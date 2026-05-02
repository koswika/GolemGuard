package com.golemguard.listener;

import com.golemguard.manager.ZoneManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// tracks players entering and leaving golem zones
public class PlayerZoneListener implements Listener {

    private final Set<UUID> playersInZone = new HashSet<>();

    private final ZoneManager zoneManager;

    public PlayerZoneListener(ZoneManager zoneManager) {
        this.zoneManager = zoneManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {

        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String worldName = event.getTo().getWorld().getName();
        int x = event.getTo().getBlockX();
        int z = event.getTo().getBlockZ();

        boolean nowInside = zoneManager.isInsideAnyZone(worldName, x, z);
        boolean wasInside = playersInZone.contains(uuid);

        if (nowInside && !wasInside) {

            playersInZone.add(uuid);
            sendActionBar(player, "§a⚙ You entered a Golem Farm Zone");
            player.playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 0.8f, 1.2f);

        } else if (!nowInside && wasInside) {

            playersInZone.remove(uuid);
            sendActionBar(player, "§c⚙ You left the Golem Farm Zone");
            player.playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 0.8f, 0.8f);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playersInZone.remove(event.getPlayer().getUniqueId());
    }

    private void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
}