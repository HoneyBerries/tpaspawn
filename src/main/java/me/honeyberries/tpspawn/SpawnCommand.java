package me.honeyberries.tpspawn;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SpawnCommand implements CommandExecutor, TabExecutor {

    private final Map<UUID, Long> cooldowns = new HashMap<>(); // Store cooldown as long (milliseconds)

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command! Sorry");
            return true;
        }

        float cooldownTime = TPSpawnSettings.getInstance().getCooldown() * 1000; // Convert cooldown to milliseconds
        long currentTime = System.currentTimeMillis(); // Current time in milliseconds
        UUID playerUUID = player.getUniqueId();

        // Check if the player is on cooldown and if they have the bypass permission
        if (cooldowns.containsKey(playerUUID) && !player.hasPermission("tpspawn.cooldown.bypass")) {
            long lastUsed = cooldowns.get(playerUUID);
            if ((currentTime - lastUsed) < cooldownTime) { // Still have cooldown left
                int timeLeft = (int) Math.ceil((cooldownTime - (currentTime - lastUsed)) / 1000.0); // Time left in seconds
                player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.GREEN + timeLeft + ChatColor.RED + " seconds before using this command again!");
                return true;
            }
        }

        // Get the world and teleport the player to spawn
        World world = Objects.requireNonNull(Bukkit.getWorld("world"), "Overworld does not exist!");

        // Prevent fall damage if the setting is enabled
        if (TPSpawnSettings.getInstance().isPreventFallDamage()) {
            player.setVelocity(new Vector()); // Prevent fall damage
        }

        player.teleport(world.getSpawnLocation());
        player.sendMessage(ChatColor.GOLD + "You went to the world's spawn!");
        TPSpawn.getInstance().getLogger().info(player.getName() + " went to the world spawn!");

        // Update the cooldown time for the player
        cooldowns.put(playerUUID, currentTime);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return new ArrayList<>();
    }
}
