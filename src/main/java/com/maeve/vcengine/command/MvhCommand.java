package com.maeve.vcengine.command;

import com.maeve.vcengine.MythicVoiceEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class MvhCommand implements CommandExecutor {
    private final MythicVoiceEngine plugin;

    public MvhCommand(MythicVoiceEngine plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 2 || !args[0].equalsIgnoreCase("debug")) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /mvh debug <on|off|status>");
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
}