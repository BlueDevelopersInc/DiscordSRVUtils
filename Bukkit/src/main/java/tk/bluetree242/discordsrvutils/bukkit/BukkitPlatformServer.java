/*
 *  LICENSE
 *  DiscordSRVUtils
 *  -------------
 *  Copyright (C) 2020 - 2021 BlueTree242
 *  -------------
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program.  If not, see
 *  <http://www.gnu.org/licenses/gpl-3.0.html>.
 *  END
 */

package tk.bluetree242.discordsrvutils.bukkit;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import tk.bluetree242.discordsrvutils.bukkit.cmd.ConsoleCommandUser;
import tk.bluetree242.discordsrvutils.platform.PlatformDiscordSRV;
import tk.bluetree242.discordsrvutils.platform.PlatformPlayer;
import tk.bluetree242.discordsrvutils.platform.PlatformPluginDescription;
import tk.bluetree242.discordsrvutils.platform.PlatformServer;
import tk.bluetree242.discordsrvutils.platform.command.CommandUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BukkitPlatformServer extends PlatformServer {
    @Override
    public boolean isPluginEnabled(String name) {
        return Bukkit.getPluginManager().isPluginEnabled(name);
    }

    @Override
    public boolean isPluginInstalled(String name) {
        return Bukkit.getPluginManager().getPlugin(name) != null;
    }

    @Override
    public PlatformPluginDescription getPluginDescription(String name) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        if (plugin == null) return null;
        return new BukkitPluginDescription(plugin);
    }

    @Override
    public List<PlatformPlayer> getOnlinePlayers() {
        List<PlatformPlayer> result = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            result.add(new BukkitPlayer(onlinePlayer));
        }
        return result;
    }

    @Override
    public CommandUser getConsoleSender() {
        return new ConsoleCommandUser();
    }


    @Override
    public String colors(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }



}
