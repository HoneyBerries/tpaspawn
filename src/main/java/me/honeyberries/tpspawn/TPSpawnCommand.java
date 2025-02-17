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
import java.util.stream.Stream;

/**
 * Command executor for the /tpspawn command.
 * Handles various actions like reloading configuration, and setting cooldown
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
                case "help":
                    sender.sendMessage(ChatColor.GREEN + "------ TPSpawn Command Help ------");
                    sender.sendMessage(ChatColor.AQUA + "/tpspawn reload" + ChatColor.GRAY + " - Reloads the plugin configuration.");
                    sender.sendMessage(ChatColor.AQUA + "/tpspawn cooldown" + ChatColor.GRAY + " - Displays the current cooldown time.");
                    sender.sendMessage(ChatColor.AQUA + "/tpspawn cooldown <value>" + ChatColor.GRAY + " - Sets a new cooldown time (in seconds).");
                    sender.sendMessage(ChatColor.AQUA + "/tpspawn sound" + ChatColor.GRAY + " - Shows if teleport sound is enabled.");
                    sender.sendMessage(ChatColor.AQUA + "/tpspawn sound <true/false>" + ChatColor.GRAY + " - Enables or disables teleport sound.");
                    sender.sendMessage(ChatColor.GREEN + "----------------------------------");
                    break;

                case "reload":
                    TPSpawnSettings.getInstance().loadConfig();
                    sender.sendMessage(ChatColor.AQUA + "Configuration successfully reloaded!");
                    break;  // Prevents falling through to the next case

                case "cooldown":
                    double cooldown = TPSpawnSettings.getInstance().getCooldown();
                    sender.sendMessage(ChatColor.GOLD + "Cooldown time is set to " + ChatColor.AQUA + cooldown + ChatColor.GOLD + " seconds!");
                    break;


                case "sound":
                    boolean playSound = TPSpawnSettings.getInstance().isTeleportSound();
                    sender.sendMessage(ChatColor.GOLD + "Teleport sound is currently set to " + ChatColor.AQUA + playSound);
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
                        double cooldown = Double.parseDouble(args[1]);
                        if (cooldown < 0.0) {
                            sender.sendMessage(ChatColor.RED + "Cooldown must be non-negative");
                            break;
                        }

                        TPSpawnSettings.getInstance().setCooldown(cooldown);
                        TPSpawn.getInstance().getLogger().info("Cooldown: " + cooldown);
                        sender.sendMessage(ChatColor.GOLD + "Cooldown is now set to " + ChatColor.AQUA + cooldown + ChatColor.GOLD + " seconds!");

                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Invalid number format for cooldown.");
                    }
                    break;


                case "sound":
                    boolean playSound = Boolean.parseBoolean(args[1]);
                    TPSpawnSettings.getInstance().setTeleportSound(playSound);
                    TPSpawn.getInstance().getLogger().info("Teleport sound is set to " + playSound);
                    sender.sendMessage(ChatColor.GOLD + "Teleport sound is now set to " + ChatColor.AQUA + playSound);
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
            return Stream.of("reload", "cooldown", "sound", "help")
                    .filter(option -> option.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Suggest true/false for the second argument when 'sound' is already an arg
        if (args.length == 2) {
            if (List.of("sound").contains(args[0].toLowerCase())) {
                return Stream.of("true", "false")
                        .filter(option -> option.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList(); // No suggestions
    }
}
