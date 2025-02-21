package me.honeyberries.tpspawn;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
 * Handles the /tpspawn command and its subcommands such as reload, cooldown, and sound settings.
 */
public class TPSpawnCommand implements CommandExecutor, TabExecutor {

    /**
     * Executes the /tpspawn command, handling different actions such as reload, cooldown, and sound configuration.
     * @param sender The sender of the command.
     * @param command The command being executed.
     * @param label The label of the command.
     * @param args The arguments passed with the command.
     * @return true if the command was handled, false otherwise.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Permission check
        if (!sender.hasPermission("tpspawn.command.edit")) {
            sender.sendMessage(Component.text("Sorry, you don't have the permissions to run this command!", NamedTextColor.RED));
            return true;
        }

        // Handle arguments
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "help":
                    showHelp(sender);
                    break;
                case "reload":
                    TPSpawnSettings.getInstance().loadConfig();
                    sender.sendMessage(Component.text("Configuration successfully reloaded!", NamedTextColor.AQUA));
                    break;
                case "cooldown":
                    double cooldown = TPSpawnSettings.getInstance().getCooldown();
                    sender.sendMessage(Component.text("Cooldown time is set to ", NamedTextColor.GOLD)
                            .append(Component.text(cooldown + " seconds!", NamedTextColor.AQUA)));
                    break;
                case "sound":
                    boolean playSound = TPSpawnSettings.getInstance().isTeleportSound();
                    sender.sendMessage(Component.text("Teleport sound is currently set to ", NamedTextColor.GOLD)
                            .append(Component.text(String.valueOf(playSound), NamedTextColor.AQUA)));
                    break;
                default:
                    sender.sendMessage(Component.text("Invalid action! Usage: /tpspawn <action> <value>", NamedTextColor.RED));
                    break;
            }
            return true;
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "cooldown":
                    setCooldown(sender, args[1]);
                    break;
                case "sound":
                    setSound(sender, args[1]);
                    break;
                default:
                    sender.sendMessage(Component.text("Invalid action! Usage: /tpspawn <action> <value>", NamedTextColor.RED));
                    break;
            }
            return true;
        }

        sender.sendMessage(Component.text("Invalid Syntax! Usage: /tpspawn <action> <value>", NamedTextColor.RED));
        return true;
    }

    /**
     * Shows the help menu for the TPSpawn command.
     * @param sender The sender who invoked the command.
     */
    private void showHelp(CommandSender sender) {
        sender.sendMessage(Component.text("------ TPSpawn Command Help ------", NamedTextColor.GREEN));
        sender.sendMessage(Component.text("/tpspawn reload", NamedTextColor.AQUA)
                .append(Component.text(" - Reloads the plugin configuration.", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/tpspawn cooldown", NamedTextColor.AQUA)
                .append(Component.text(" - Displays the current cooldown time.", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/tpspawn cooldown <value>", NamedTextColor.AQUA)
                .append(Component.text(" - Sets a new cooldown time (in seconds).", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/tpspawn sound", NamedTextColor.AQUA)
                .append(Component.text(" - Shows if teleport sound is enabled.", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/tpspawn sound <true/false>", NamedTextColor.AQUA)
                .append(Component.text(" - Enables or disables teleport sound.", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("----------------------------------", NamedTextColor.GREEN));
    }

    /**
     * Sets the cooldown time for teleportation.
     * @param sender The sender who invoked the command.
     * @param cooldownStr The string value representing the cooldown time.
     */
    private void setCooldown(CommandSender sender, String cooldownStr) {
        try {
            double cooldown = Double.parseDouble(cooldownStr);
            if (cooldown < 0.0) {
                sender.sendMessage(Component.text("Cooldown must be non-negative", NamedTextColor.RED));
                return;
            }
            TPSpawnSettings.getInstance().setCooldown(cooldown);
            TPSpawn.getInstance().getLogger().info("Cooldown: " + cooldown);
            sender.sendMessage(Component.text("Cooldown is now set to ", NamedTextColor.GOLD)
                    .append(Component.text(cooldown + " seconds!", NamedTextColor.AQUA)));
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Invalid number format for cooldown.", NamedTextColor.RED));
        }
    }

    /**
     * Sets whether the teleport sound is enabled.
     * @param sender The sender who invoked the command.
     * @param soundStr The string value representing whether the sound is enabled.
     */
    private void setSound(CommandSender sender, String soundStr) {
        boolean playSound = Boolean.parseBoolean(soundStr);
        TPSpawnSettings.getInstance().setTeleportSound(playSound);
        TPSpawn.getInstance().getLogger().info("Teleport sound is set to " + playSound);
        sender.sendMessage(Component.text("Teleport sound is now set to ", NamedTextColor.GOLD)
                .append(Component.text(String.valueOf(playSound), NamedTextColor.AQUA)));
    }

    /**
     * Tab-completion for the /tpspawn command.
     * @param sender The sender who invoked the command.
     * @param command The command being executed.
     * @param label The label of the command.
     * @param args The arguments passed with the command.
     * @return A list of suggestions for the tab-completion.
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Stream.of("reload", "cooldown", "sound", "help")
                    .filter(option -> option.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && "sound".equalsIgnoreCase(args[0])) {
            return Stream.of("true", "false")
                    .filter(option -> option.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
