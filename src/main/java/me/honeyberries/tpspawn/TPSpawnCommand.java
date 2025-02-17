package me.honeyberries.tpspawn;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Command executor for the /tpspawn command.
 * Handles various actions like reloading configuration, setting cooldown, and preventing fall damage.
 */
public class TPSpawnCommand implements CommandExecutor, TabExecutor {

    /**
     * Executes the command based on the provided arguments.
     *
     * @param sender The sender of the command (e.g., player or console).
     * @param command The command that was executed.
     * @param label The label of the command (e.g., "tpspawn").
     * @param args The arguments passed with the command.
     * @return True if the command was handled successfully, false otherwise.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // Check if the sender has permission to use the command
        if (!sender.hasPermission("tpspawn.command.edit")) {
            sender.sendMessage(ChatColor.RED + "Sorry, you don't have the permissions to run this command!");
            return true;
        }

        // Handle different actions based on the provided arguments
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "reload":
                    TPSpawnSettings.getInstance().loadConfig();
                    sender.sendMessage(ChatColor.GREEN + "Configuration successfully reloaded!");
                    break;  // Prevents falling through to the next case

                case "cooldown":
                    float cooldown = TPSpawnSettings.getInstance().getCooldown();
                    sender.sendMessage(ChatColor.GOLD + "Cooldown time is set to " + ChatColor.GREEN + cooldown + ChatColor.GOLD + " seconds!");
                    break;

                case "preventfalldamage":
                    boolean preventFallDamage = TPSpawnSettings.getInstance().isPreventFallDamage();
                    sender.sendMessage(ChatColor.GOLD + "Preventing fall damage is currently set to " + ChatColor.GREEN + preventFallDamage);
                    break;

                default:
                    sender.sendMessage(ChatColor.RED + "Invalid action! Usage: /tpspawn <action> <value>");
                    break;
            }
            return true;
        }

        else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "cooldown":
                    try {
                        long cooldown = Long.parseLong(args[1]);
                        TPSpawnSettings.getInstance().setCooldown(cooldown);
                        TPSpawn.getInstance().getLogger().info("Cooldown: " + cooldown);
                        sender.sendMessage(ChatColor.GOLD + "Cooldown is now set to " + ChatColor.GREEN + cooldown + ChatColor.GOLD + " seconds!");
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Invalid number format for cooldown.");
                    }
                    break;

                case "preventfalldamage":
                    boolean preventFallDamage = Boolean.parseBoolean(args[1]);
                    TPSpawnSettings.getInstance().setPreventFallDamage(preventFallDamage);
                    TPSpawn.getInstance().getLogger().info("Preventing fall damage: " + preventFallDamage);
                    sender.sendMessage(ChatColor.GOLD + "Preventing fall damage is now set to " + ChatColor.GREEN + preventFallDamage);
                    break;

                default:
                    sender.sendMessage(ChatColor.RED + "Invalid action! Usage: /tpspawn <action> <value>");
                    break;
            }
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Invalid Syntax! Usage: /tpspawn <action> <value>");
        return true;
    }

    /**
     * Provides tab completion suggestions for the /tpspawn command.
     *
     * @param sender The sender of the command (e.g., player or console).
     * @param command The command being executed.
     * @param label The label of the command (e.g., "tpspawn").
     * @param args The arguments passed with the command.
     * @return A list of suggestions based on the current argument position.
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Suggest actions for the first argument
        if (args.length == 1) {
            List<String> validSuggestions = List.of("reload", "cooldown", "preventfalldamage");
            return validSuggestions.stream()
                    .filter(option -> option.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Suggest true/false for the second argument when 'preventfalldamage' is used
        if (args.length == 2 && args[0].equalsIgnoreCase("preventfalldamage")) {
            return List.of("true", "false");
        }

        return Collections.emptyList(); // No suggestions
    }
}
