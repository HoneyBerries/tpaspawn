package me.honeyberries.tpspawn;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class TPSpawn extends JavaPlugin {

    @Override
    public void onEnable() {

        // Plugin startup logic
        getLogger().info("TP Spawn has been enabled!");

        //Register /spawn command with server
        Objects.requireNonNull(getServer().getPluginCommand("spawn")).setExecutor(new SpawnCommand());

        //Register /tpspawn command with server
        Objects.requireNonNull(getServer().getPluginCommand("tpspawn")).setExecutor(new TPSpawnCommand());

        //Load configuration file
        TPSpawnSettings.getInstance().loadConfig();

    }


    @Override
    public void onDisable() {
        getLogger().info("TP Spawn has been disabled!");
    }

    public static TPSpawn getInstance() {
        return getPlugin(TPSpawn.class);
    }

}
