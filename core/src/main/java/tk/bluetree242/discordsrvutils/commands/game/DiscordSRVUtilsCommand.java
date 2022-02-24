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

package tk.bluetree242.discordsrvutils.commands.game;

import github.scarsz.discordsrv.util.DebugUtil;
import lombok.RequiredArgsConstructor;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.exceptions.ConfigurationLoadException;
import tk.bluetree242.discordsrvutils.platform.PlatformPlayer;
import tk.bluetree242.discordsrvutils.platform.command.CommandUser;
import tk.bluetree242.discordsrvutils.platform.command.ConsoleCommandUser;
import tk.bluetree242.discordsrvutils.platform.command.PlatformCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class DiscordSRVUtilsCommand implements PlatformCommand {
    private final DiscordSRVUtils core;

    @Override
    public void onRunAsync(String[] args, CommandUser sender, String label) throws Throwable {
        if (args.length == 0) {
            sender.sendMessage("&eRunning DiscordSRVUtils v" + core.getPlatform().getDescription().getVersion());
            String build = core.getVersionConfig().getString("buildNumber");
            if (!build.equals("NONE")) {
                sender.sendMessage("&eBuild #" + build);
            }
            sender.sendMessage("&bStatus: " + (core.isReady() ? "&aRunning and functioning" : "&cNot running"));
            return;
        }
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("discordsrvutils.reload")) {
                    sender.sendMessage("&eReloading Configuration");
                    try {
                        core.reloadConfigs();
                        sender.sendMessage("&aConfiguration successfully reloaded");
                    } catch (ConfigurationLoadException e) {
                        sender.sendMessage("&cCould not reload the " + e.getConfigName() + ". Please check server console");
                        e.printStackTrace();
                    }
                    return;
                }
            } else if (args[0].equalsIgnoreCase("debug")) {
                if (sender.hasPermission("discordsrvutils.debug")) {
                    sender.sendMessage("&aPreparing Debug Report... Please wait");
                    try {
                        sender.sendMessage("&aYour Debug report is available at: &e" + core.getPlatform().getServer().getDebugger().run());
                    } catch (Exception e) {
                        sender.sendMessage("&cERROR: " + e.getMessage());
                    }
                    return;
                }
            } else if (args[0].equalsIgnoreCase("updatecheck")) {
                if (sender.hasPermission("discordsrvutils.updatecheck")) {
                    if (sender instanceof ConsoleCommandUser) {
                        core.getUpdateChecker().updateCheck();
                    } else {
                        core.getUpdateChecker().updateCheck((PlatformPlayer) sender);
                    }
                    return;
                }
            }
        }
        sender.sendMessage("&cSubCommand not found");
    }

    @Override
    public List<String> onTabComplete(String[] args, CommandUser sender, String label) {
        List<String> values = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("discordsrvutils.reload"))
                values.add("reload");
            if (sender.hasPermission("discordsrvutils.debug"))
                values.add("debug");

            if (sender.hasPermission("discordsrvutils.updatecheck"))
                values.add("updatecheck");
        }

        List<String> result = new ArrayList<>();
        for (String a : values) {
            if (a.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                result.add(a);
        }
        return result.isEmpty() ? Collections.emptyList() : result;
    }
}
