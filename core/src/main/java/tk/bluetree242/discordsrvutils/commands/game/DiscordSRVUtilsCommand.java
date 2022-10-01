/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2022 BlueTree242
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

package tk.bluetree242.discordsrvutils.commands.game;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.exceptions.ConfigurationLoadException;
import tk.bluetree242.discordsrvutils.platform.PlatformPlayer;
import tk.bluetree242.discordsrvutils.platform.command.CommandUser;
import tk.bluetree242.discordsrvutils.platform.command.ConsoleCommandUser;
import tk.bluetree242.discordsrvutils.platform.command.PlatformCommand;
import tk.bluetree242.discordsrvutils.systems.leveling.PlayerStats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DiscordSRVUtilsCommand implements PlatformCommand {
    private final DiscordSRVUtils core;

    @Override
    public void onRunAsync(String[] args, CommandUser sender, String label) throws Throwable {
        if (args.length == 0) {
            sender.sendMessage("&eRunning DiscordSRVUtils v" + core.getPlatform().getDescription().getVersion());
            String build = core.getVersionConfig().getString("buildNumber");
            sender.sendMessage("&eBuild " + (build.equalsIgnoreCase("NONE") ? "None/Unknown" : "#" + build));
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
                        sender.sendMessage("&aYour Debug report is available at: &e" + core.getServer().getDebugger().run());
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
            } else if (args[0].equalsIgnoreCase("resetlevel")) {
                if (sender.hasPermission("discordsrvutils.resetlevel")) {
                    String name = args.length >= 2 ? args[1] : null;
                    if (name == null) {
                        sender.sendMessage("&cPlease provide player name or all for all players.");
                        return;
                    }
                        DSLContext jooq = core.getDatabaseManager().jooq();
                        if (name.equalsIgnoreCase("all")) {
                            core.getLevelingManager().resetLeveling(jooq);
                            core.getLevelingManager().cachedUUIDS.invalidateAll();
                            sender.sendMessage("&eEveryone's level has been reset");
                        } else {
                            PlayerStats stats = core.getLevelingManager().getPlayerStats(name, jooq);
                            if (stats == null) {
                                sender.sendMessage("&cPlayer not found");
                            } else {
                                stats.setLevel(0, jooq);
                                stats.setXP(0, jooq);
                                sender.sendMessage("&ePlayer's level has been reset.");
                            }
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
            if (sender.hasPermission("discordsrvutils.removeslash"))
                values.add("removeslash");
            if (sender.hasPermission("discordsrvutils.resetlevel"))
                values.add("resetlevel");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("resetlevel") && sender.hasPermission("discordsrvutils.resetlevel")) {
            List<String> result = core.getPlatform().getServer().getOnlinePlayers().stream().map(PlatformPlayer::getName).collect(Collectors.toList());
            result.add("all");
            return result;
        }

        List<String> result = new ArrayList<>();
        for (String a : values) {
            if (a.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                result.add(a);
        }
        return result.isEmpty() ? Collections.emptyList() : result;
    }
}
