package me.honeyberries.tpspawn;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class TPSpawnSettings {

    private static final TPSpawnSettings INSTANCE = new TPSpawnSettings();

    private File configFile;
    private YamlConfiguration yamlConfig;
    private boolean preventFallDamage;
    private long cooldown;

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
            preventFallDamage = yamlConfig.getBoolean("prevent-fall-damage-on-command", false);
        } catch (Exception e) {
            TPSpawn.getInstance().getLogger().warning("Failed to load prevent-fall-damage-on-command! Defaulting to false!");
        }
        try {
            cooldown = yamlConfig.getLong("cooldown", 0L);
        } catch (Exception e) {
            TPSpawn.getInstance().getLogger().warning("Failed to load cooldown time. Defaulting to 0!");
        }
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

    public boolean isPreventFallDamage() {
        return preventFallDamage;
    }

    public void setPreventFallDamage(boolean preventFallDamage) {
        this.preventFallDamage = preventFallDamage;
        set("prevent-fall-damage-on-command", preventFallDamage);
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
        set("cooldown", cooldown);
    }
}
