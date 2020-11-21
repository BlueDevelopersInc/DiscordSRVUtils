package com.bluetree.discordsrvutils.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerUtil {
    public static void sendToAuthorizedPlayers(String msg) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("discordsrvutils.log")) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
        }
    }
}
