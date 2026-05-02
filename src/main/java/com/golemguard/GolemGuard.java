package com.golemguard;

import com.golemguard.command.GolemZoneCommand;
import com.golemguard.listener.GolemSpawnListener;
import com.golemguard.listener.PlayerZoneListener;
import com.golemguard.manager.ZoneManager;
import com.golemguard.manager.ZoneRepository;
import org.bukkit.plugin.java.JavaPlugin;

public final class GolemGuard extends JavaPlugin {

    private ZoneManager zoneManager;

    @Override
    public void onEnable() {
        ZoneRepository repository = new ZoneRepository(this);
        zoneManager = new ZoneManager(repository);

        GolemZoneCommand   command         = new GolemZoneCommand(zoneManager);
        GolemSpawnListener spawnListener   = new GolemSpawnListener(zoneManager);
        PlayerZoneListener playerListener  = new PlayerZoneListener(zoneManager);

        var cmd = getCommand("golemzone");
        if (cmd != null) {
            cmd.setExecutor(command);
            cmd.setTabCompleter(command);
        } else {
            getLogger().severe("Could not find 'golemzone' command in plugin.yml!");
        }

        getServer().getPluginManager().registerEvents(spawnListener,  this);
        getServer().getPluginManager().registerEvents(playerListener, this);

        getLogger().info("GolemGuard enabled! " + zoneManager.getZoneCount() + " zone(s) loaded.");
    }

    @Override
    public void onDisable() {
        getLogger().info("GolemGuard disabled.");
    }
}