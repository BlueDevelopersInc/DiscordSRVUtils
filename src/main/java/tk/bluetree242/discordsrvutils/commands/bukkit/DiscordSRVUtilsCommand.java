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

package tk.bluetree242.discordsrvutils.commands.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.bukkit.BukkitPlayer;
import tk.bluetree242.discordsrvutils.commandmanagement.BukkitCommand;
import tk.bluetree242.discordsrvutils.exceptions.ConfigurationLoadException;
import tk.bluetree242.discordsrvutils.utils.DebugUtil;

public class DiscordSRVUtilsCommand extends BukkitCommand {
    public void onRunAsync(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws Throwable {
        if (args.length == 0) {
            sender.sendMessage(colors("&eRunning DiscordSRVUtils v" + core.getDescription().getVersion()));
            String build = core.getVersionConfig().getString("buildNumber");
            if (!build.equals("NONE")) {
                sender.sendMessage(colors("&eBuild #" + build));
            }
            sender.sendMessage(colors("&bStatus: " + (core.isReady() ? "&aRunning and functioning" : "&cNot running")));
            return;
        }
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("discordsrvutils.reload") || sender instanceof ConsoleCommandSender) {
                    sender.sendMessage(colors("&eReloading Configuration"));
                    try {
                        core.reloadConfigs();
                        sender.sendMessage(colors("&aConfiguration sucessfully reloaded"));
                    } catch (ConfigurationLoadException e) {
                        sender.sendMessage(colors("&cCould not reload the " + e.getConfigName() + ". Please check server console"));
                        e.printStackTrace();
                    }
                    return;
                }
            } else if (args[0].equalsIgnoreCase("debug")) {
                if (sender.hasPermission("discordsrvutils.debug") || sender instanceof ConsoleCommandSender) {
                    sender.sendMessage(ChatColor.GREEN + "Preparing Debug Report... Please wait");
                    try {
                        sender.sendMessage(colors("&aYour Debug report is available at: &e" + DebugUtil.run()));
                    } catch (Exception e) {
                        sender.sendMessage(colors("&cERROR: " + e.getMessage()));
                    }
                    return;
                }
            } else if (args[0].equalsIgnoreCase("updatecheck")) {
                if (sender.hasPermission("discordsrvutils.updatecheck")) {
                    if (sender instanceof ConsoleCommandSender) {
                        DiscordSRVUtils.get().updateCheck();
                    } else {
                        DiscordSRVUtils.get().updateCheck(new BukkitPlayer(((Player) sender)));
                    }
                    return;
                }
            }
        }
        sender.sendMessage(ChatColor.RED + "SubCommand not found");
    }
}
