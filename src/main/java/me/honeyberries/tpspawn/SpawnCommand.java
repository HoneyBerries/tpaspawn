package me.honeyberries.tpspawn;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
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

    private final Map<UUID, Double> cooldowns = new HashMap<>(); // Store cooldown as double (milliseconds)

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command! Sorry");
            return true;
        }

        if (!sender.hasPermission("tpspawn.command.spawn")) {
            sender.sendMessage(ChatColor.RED + "You don't have the permissions to teleport to the world spawn!");
        }

        if (args.length == 0) {

            double cooldownTime = TPSpawnSettings.getInstance().getCooldown() * 1000; // Convert cooldown to milliseconds
            double currentTime = System.currentTimeMillis(); // Current time in milliseconds
            UUID playerUUID = player.getUniqueId();

            // Check if the player still has cooldown and if they don't have the bypass permission
            if (cooldowns.containsKey(playerUUID) && !player.hasPermission("tpspawn.cooldown.bypass")) {
                double lastUsed = cooldowns.get(playerUUID);

                if ((currentTime - lastUsed) < cooldownTime) { // Still have cooldown left
                    int timeLeft = (int) Math.ceil((cooldownTime - (currentTime - lastUsed)) / 1000.0); // Time left in seconds

                    player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.GREEN + timeLeft + ChatColor.RED + " seconds before using this command again!");
                    return true;
                }
            }

            // Get the world and teleport the player to spawn
            World world = Objects.requireNonNull(Bukkit.getWorld("world"), "Overworld does not exist!");


            player.teleport(world.getSpawnLocation());
            player.sendMessage(ChatColor.GOLD + "You went to the world's spawn!");
            TPSpawn.getInstance().getLogger().info(player.getName() + " went to the world spawn!");

            if (TPSpawnSettings.getInstance().isTeleportSound()) {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            }

            // Update the cooldown time for the player
            cooldowns.put(playerUUID, currentTime);

            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(ChatColor.GREEN + "------ Spawn Command Help ------");
            sender.sendMessage(ChatColor.AQUA + "/spawn" + ChatColor.GRAY + " - Teleports you to the world's spawn.");
            sender.sendMessage(ChatColor.AQUA + "/spawn help" + ChatColor.GRAY + " - Displays this help message.");
            sender.sendMessage(ChatColor.GREEN + "--------------------------------");
            return true;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (args.length == 1) {
            return List.of("help");
        }

        return List.of();
    }
}
