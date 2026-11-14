package com.maeve.vcengine.command;

import com.maeve.vcengine.MythicVoiceEngine;
import com.maeve.vcengine.voice.VoiceSoundEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class MvhCommand implements CommandExecutor {
    private final MythicVoiceEngine plugin;

    public MvhCommand(MythicVoiceEngine plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 2 && args[0].equalsIgnoreCase("simulate")) {
            simulate(sender, args[1]);
            return true;
        }

        if (args.length != 2 || !args[0].equalsIgnoreCase("debug")) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /mvh debug <on|off|status> | /mvh simulate <voice|whisper>");
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "on" -> {
                plugin.setDebugEnabled(true);
                sender.sendMessage(ChatColor.GREEN + "[MVH] Debug enabled :D");
            }
            case "off" -> {
                plugin.setDebugEnabled(false);
                sender.sendMessage(ChatColor.GREEN + "[MVH] Debug disabled :c");
            }
            case "status" -> sender.sendMessage(ChatColor.YELLOW + "Voice debug: " + (plugin.isDebugEnabled() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
            default -> sender.sendMessage(ChatColor.YELLOW + "Usage: /mvh debug <on|off|status>");
        }

        return true;
    }

    private void simulate(CommandSender sender, String mode) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "[MVH] Only players can simulate voice");
            return;
        }

        if (!mode.equalsIgnoreCase("voice") && !mode.equalsIgnoreCase("whisper")) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /mvh simulate <voice|whisper>");
            return;
        }

        boolean whispering = mode.equalsIgnoreCase("whisper");
        VoiceSoundEvent event = new VoiceSoundEvent(
                player.getUniqueId(),
                player.getName(),
                player.getLocation(),
                whispering,
                plugin.getVoiceRadius(whispering),
                System.currentTimeMillis()
        );

        int alerted = plugin.getMobNotifier().alertNearbyMobs(event, player);
        sender.sendMessage(ChatColor.GREEN + "[MVH] Simulated " + mode.toLowerCase() + ", alerted " + alerted + " mobs");
    }
}