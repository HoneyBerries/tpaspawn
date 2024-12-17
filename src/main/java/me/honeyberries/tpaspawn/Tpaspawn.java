package me.honeyberries.tpaspawn;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public final class Tpaspawn extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("TPA Spawn has been enabled!");

        getServer().getPluginCommand("spawn").setExecutor(new SpawnCommand());

    }


    @Override
    public void onDisable() {
        getLogger().info("TPA Spawn has been disabled!");
    }



}
