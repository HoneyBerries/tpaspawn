package me.honeyberries.tpspawn;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;

/**
 * Manages the configuration settings for the TPSpawn plugin.
 * Loads, saves, and modifies settings such as teleport cooldown and sound effects.
 */
public class TPSpawnSettings {

    /**
     * Singleton instance of TPSpawnSettings.
     */
    private static final TPSpawnSettings INSTANCE = new TPSpawnSettings();

    private File configFile;
    private YamlConfiguration yamlConfig;
    private double cooldown;
    private boolean teleportSound;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private TPSpawnSettings() {
    }

    /**
     * Gets the singleton instance of TPSpawnSettings.
     *
     * @return The instance of TPSpawnSettings.
     */
    public static TPSpawnSettings getInstance() {
        return INSTANCE;
    }

    /**
     * Loads the configuration from the config.yml file.
     * If the file does not exist, it is created with default values.
     */
    public void loadConfig() {
        configFile = new File(TPSpawn.getInstance().getDataFolder(), "config.yml");

        // Create the config file if it does not exist
        if (!configFile.exists()) {
            TPSpawn.getInstance().saveResource("config.yml", false);
        }

        yamlConfig = YamlConfiguration.loadConfiguration(configFile);
        yamlConfig.options().parseComments(true); // Preserve comments in config file

        // Load cooldown setting from config, defaulting to 0 if an error occurs
        try {
            cooldown = Math.abs(yamlConfig.getDouble("cooldown"));
        } catch (Exception e) {
            TPSpawn.getInstance().getLogger().warning("Failed to load cooldown time. Defaulting to 0!");
            cooldown = 0.0;
        }

        // Load teleport sound setting from config, defaulting to false if an error occurs
        try {
            teleportSound = yamlConfig.getBoolean("teleport-sound");
        } catch (Exception e) {
            TPSpawn.getInstance().getLogger().warning("Failed to load teleport sound! Defaulting to false");
            teleportSound = false;
        }

        // Log the loaded configuration values
        TPSpawn.getInstance().getLogger().info("Configuration successfully loaded");
        TPSpawn.getInstance().getLogger().info("Cooldown: " + cooldown + " seconds");
        TPSpawn.getInstance().getLogger().info("Play teleport sound: " + teleportSound);
    }

    /**
     * Saves the current configuration settings to the config file.
     */
    public void saveConfig() {
        try {
            yamlConfig.save(configFile);
        } catch (IOException e) {
            TPSpawn.getInstance().getLogger().warning("Failed to save configuration file.");
        }
    }

    /**
     * Sets a value in the configuration file and saves it.
     *
     * @param path  The configuration path (e.g., "cooldown").
     * @param value The value to set.
     */
    public void set(@NotNull String path, @NotNull Object value) {
        yamlConfig.set(path, value);
        saveConfig();
    }

    /**
     * Gets the cooldown time for the /spawn command.
     *
     * @return The cooldown time in seconds.
     */
    public double getCooldown() {
        return cooldown;
    }

    /**
     * Sets a new cooldown time for the /spawn command and updates the config file.
     *
     * @param cooldown The new cooldown time in seconds.
     */
    public void setCooldown(double cooldown) {
        this.cooldown = cooldown;
        set("cooldown", cooldown);
    }

    /**
     * Checks if teleport sound is enabled.
     *
     * @return True if teleport sound is enabled, false otherwise.
     */
    public boolean isTeleportSound() {
        return teleportSound;
    }

    /**
     * Sets whether the teleport sound should be played and updates the config file.
     *
     * @param teleportSound True to enable teleport sound, false to disable it.
     */
    public void setTeleportSound(boolean teleportSound) {
        this.teleportSound = teleportSound;
        set("teleport-sound", teleportSound);
    }
}
