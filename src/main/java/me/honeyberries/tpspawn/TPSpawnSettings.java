package me.honeyberries.tpspawn;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;

public class TPSpawnSettings {

    private static final TPSpawnSettings INSTANCE = new TPSpawnSettings();

    private File configFile;
    private YamlConfiguration yamlConfig;
    private double cooldown;
    private boolean teleportSound;

    public TPSpawnSettings() {
    }

    public static TPSpawnSettings getInstance() {
        return INSTANCE;
    }

    public void loadConfig() {

        configFile = new File(TPSpawn.getInstance().getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            TPSpawn.getInstance().saveResource("config.yml", false);
        }

        yamlConfig = YamlConfiguration.loadConfiguration(configFile);
        yamlConfig.options().parseComments(true);

        // Load values from config
        try {
            cooldown = Math.max(yamlConfig.getDouble("cooldown"), 0.0);
        } catch (Exception e) {
            TPSpawn.getInstance().getLogger().warning("Failed to load cooldown time. Defaulting to 0!");
            cooldown = 0.0;
        }

        try {
            teleportSound = yamlConfig.getBoolean("teleport-sound");
        } catch (Exception e) {
            TPSpawn.getInstance().getLogger().warning("Failed to load teleport sound! Defaulting to false");
            teleportSound = false;
        }

        TPSpawn.getInstance().getLogger().info("Configuration successfully loaded");
        TPSpawn.getInstance().getLogger().info("Cooldown: " + cooldown + " seconds");
        TPSpawn.getInstance().getLogger().info("Play teleport sound: " + teleportSound);

    }

    /**
     * Saves the current configuration to the config file.
     */
    public void saveConfig() {
        try {
            yamlConfig.save(configFile);
        } catch (IOException e) {
            TPSpawn.getInstance().getLogger().warning("Failed to save configuration file.");
        }
    }

    /**
     * Sets a value in the configuration and saves it.
     *
     * @param path  the configuration path
     * @param value the value to set
     */
    public void set(@NotNull String path, @NotNull Object value) {
        yamlConfig.set(path, value);
        saveConfig();
    }


    public double getCooldown() {
        return cooldown;
    }

    public void setCooldown(double cooldown) {
        this.cooldown = cooldown;
        set("cooldown", cooldown);
    }

    public boolean isTeleportSound() {
        return teleportSound;
    }

    public void setTeleportSound(boolean teleportSound) {
        this.teleportSound = teleportSound;
        set("teleport-sound", teleportSound);
    }
}
