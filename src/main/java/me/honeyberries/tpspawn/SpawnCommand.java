package me.honeyberries.tpspawn;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;

/**
 * Handles the /spawn command, which teleports players to the world's spawn location.
 * This command includes a cooldown system and permission checks.
 */
public class SpawnCommand implements CommandExecutor, TabExecutor {

    /**
     * Stores cooldown timestamps for each player to prevent repeated teleportation within a short time.
     */
    private final Map<UUID, Double> cooldowns = new HashMap<>();

    /**
     * Handles execution of the /spawn command.
     *
     * @param sender  The command sender (must be a player).
     * @param command The command that was executed.
     * @param label   The alias of the command used.
     * @param args    The arguments provided by the sender.
     * @return true if the command was processed successfully.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Ensure the sender is a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command! Sorry", NamedTextColor.RED));
            return true;
        }

        // Check if the player has permission
        if (!player.hasPermission("tpspawn.command.spawn")) {
            player.sendMessage(Component.text("You don't have permission to teleport to the world spawn!", NamedTextColor.RED));
            return true;
        }

        // Handle the /spawn help command
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(player);
            return true;
        }

        // Handle teleportation to world spawn
        double cooldownTime = TPSpawnSettings.getInstance().getCooldown() * 1000; // Convert to milliseconds
        double currentTime = System.currentTimeMillis();
        UUID playerUUID = player.getUniqueId();

        // Check cooldowns
        if (cooldowns.containsKey(playerUUID) && !player.hasPermission("tpspawn.cooldown.bypass")) {
            double lastUsed = cooldowns.get(playerUUID);
            if ((currentTime - lastUsed) < cooldownTime) {
                int timeLeft = (int) Math.ceil((cooldownTime - (currentTime - lastUsed)) / 1000.0);
                player.sendMessage(
                        Component.text("You must wait ", NamedTextColor.RED)
                                .append(Component.text(timeLeft, NamedTextColor.GREEN))
                                .append(Component.text(" seconds before using this command again!", NamedTextColor.RED))
                );
                return true;
            }
        }

        // Get world spawn and teleport the player
        World world = Objects.requireNonNull(Bukkit.getWorld("world"), "Overworld does not exist!");
        player.teleport(world.getSpawnLocation());
        player.sendMessage(Component.text("You went to the world's spawn!", NamedTextColor.GOLD)
                .clickEvent(ClickEvent.suggestCommand("spawn")));

        TPSpawn.getInstance().getLogger().info(player.getName() + " went to the world spawn!");

        // Play teleport sound if enabled
        if (TPSpawnSettings.getInstance().isTeleportSound()) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        }

        // Update cooldown
        cooldowns.put(playerUUID, currentTime);
        return true;
    }

    /**
     * Provides tab completion suggestions for the /spawn command.
     *
     * @param sender  The command sender.
     * @param command The command being tab-completed.
     * @param label   The alias of the command used.
     * @param args    The arguments provided so far.
     * @return A list of possible tab completions.
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("help");
        }
        return List.of();
    }

    /**
     * Sends a help message to the player explaining the /spawn command usage.
     *
     * @param player The player receiving the help message.
     */
    private void sendHelpMessage(Player player) {
        player.sendMessage(Component.text("------ Spawn Command Help ------", NamedTextColor.GREEN));
        player.sendMessage(Component.text("/spawn", NamedTextColor.AQUA)
                .append(Component.text(" - Teleports you to the world's spawn.", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/spawn help", NamedTextColor.AQUA)
                .append(Component.text(" - Displays this help message.", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("--------------------------------", NamedTextColor.GREEN));
    }
}
