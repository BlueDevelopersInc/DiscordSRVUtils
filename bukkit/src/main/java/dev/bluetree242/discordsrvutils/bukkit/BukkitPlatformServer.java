/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2024 BlueTree242
 * -------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * END
 */

package dev.bluetree242.discordsrvutils.bukkit;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.bukkit.cmd.BukkitConsoleCommandUser;
import dev.bluetree242.discordsrvutils.platform.Debugger;
import dev.bluetree242.discordsrvutils.platform.PlatformPlayer;
import dev.bluetree242.discordsrvutils.platform.PlatformPluginDescription;
import dev.bluetree242.discordsrvutils.platform.PlatformServer;
import dev.bluetree242.discordsrvutils.platform.command.CommandUser;
import github.scarsz.discordsrv.util.SchedulerUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BukkitPlatformServer extends PlatformServer {
    private final DiscordSRVUtils core;
    @Getter
    private final Debugger debugger;
    private final DiscordSRVUtilsBukkit main;

    public BukkitPlatformServer(DiscordSRVUtils core, DiscordSRVUtilsBukkit main) {
        this.core = core;
        debugger = new BukkitDebugger(core);
        this.main = main;
    }

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
            result.add(new BukkitPlayer(core, onlinePlayer));
        }
        return result;
    }

    @Override
    public int getMaxPlayers() {
        return Bukkit.getServer().getMaxPlayers();
    }

    @Override
    public int getOnlineCount() {
        return Bukkit.getServer().getOnlinePlayers().size();
    }

    @Override
    public CommandUser getConsoleSender() {
        return new BukkitConsoleCommandUser(main);
    }


    @Override
    public String colors(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public Object getOriginal() {
        return Bukkit.getServer();
    }

    @Override
    public PlatformPlayer getOfflinePlayer(UUID uuid) {
        return new BukkitOfflinePlayer(Bukkit.getOfflinePlayer(uuid), core);
    }

    @Override
    public PlatformPlayer getPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return null;
        return new BukkitPlayer(core, player);
    }

    @Override
    public PlatformPlayer getPlayer(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) return null;
        return new BukkitPlayer(core, player);
    }

    @Override
    public void executeConsoleCommands(String... cmds) {
        SchedulerUtil.runTask(main, () -> {
            for (String cmd : cmds) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
        });
    }

    @Override
    public void runAsync(Runnable runnable) {
        SchedulerUtil.runTaskAsynchronously(main, runnable);
    }


}
