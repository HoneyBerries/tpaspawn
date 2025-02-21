package me.honeyberries.tpspawn;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;

public final class TPSpawn extends JavaPlugin {

    @Override
    public void onEnable() {

        // Plugin startup logic
        getLogger().info("TP Spawn has been enabled!");

        // Register /spawn command with the server
        if (getServer().getPluginCommand("spawn") != null) {
            Objects.requireNonNull(getServer().getPluginCommand("spawn")).setExecutor(new SpawnCommand());
            getLogger().info("Successfully registered /spawn command.");
        } else {
            getLogger().warning("Failed to register /spawn command!");
        }

        // Register /tpspawn command with the server
        if (getServer().getPluginCommand("tpspawn") != null) {
            Objects.requireNonNull(getServer().getPluginCommand("tpspawn")).setExecutor(new TPSpawnCommand());
            getLogger().info("Successfully registered /tpspawn command.");
        } else {
            getLogger().warning("Failed to register /tpspawn command!");
        }

        // Load the configuration file
        try {
            TPSpawnSettings.getInstance().loadConfig();
            getLogger().info("Configuration successfully loaded!");
        } catch (Exception e) {
            getLogger().warning("Failed to load the configuration file!");
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("TP Spawn has been disabled!");
    }

    /**
     * Provides access to the plugin instance.
     * @return The plugin instance.
     */
    public static TPSpawn getInstance() {
        return getPlugin(TPSpawn.class);
    }

}
