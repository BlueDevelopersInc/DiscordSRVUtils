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

package dev.bluetree242.discordsrvutils.commands.game;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.VersionInfo;
import dev.bluetree242.discordsrvutils.events.LevelupEvent;
import dev.bluetree242.discordsrvutils.exceptions.ConfigurationLoadException;
import dev.bluetree242.discordsrvutils.exceptions.InvalidResponseException;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import dev.bluetree242.discordsrvutils.platform.PlatformPlayer;
import dev.bluetree242.discordsrvutils.platform.command.CommandUser;
import dev.bluetree242.discordsrvutils.platform.command.ConsoleCommandUser;
import dev.bluetree242.discordsrvutils.platform.command.PlatformCommand;
import dev.bluetree242.discordsrvutils.systems.leveling.PlayerStats;
import dev.bluetree242.discordsrvutils.updatechecker.UpdateChecker;
import dev.bluetree242.discordsrvutils.utils.Utils;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.event.ClickEvent;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.format.NamedTextColor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DiscordSRVUtilsCommand implements PlatformCommand {
    private final DiscordSRVUtils core;
    private boolean migrated = false;

    @Override
    public void onRunAsync(String[] args, CommandUser sender, String label) throws Throwable {
        if (args.length == 0) {
            sender.sendMessage("&eRunning DiscordSRVUtils v&a" + core.getPlatform().getDescription().getVersion());
            sender.sendMessage("&eBuild " + (VersionInfo.BUILD_NUMBER.equalsIgnoreCase("NONE") ? "&aNone/Unknown" : "&a#" + VersionInfo.BUILD_NUMBER));
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
                    try {
                        UpdateChecker.UpdateCheckResult result = core.getUpdateChecker().updateCheck(false);
                        if (result == null) sender.sendMessage("&cLooks like update checking is disabled.");
                        else {
                            if (result.getMessage() == null) {
                                if (result.getVersionsBehind() <= 0) {
                                    sender.sendMessage("&aYou are up to date.");
                                } else {
                                    sender.sendMessage(Component.text("You are ").color(NamedTextColor.YELLOW)
                                            .append(Component.text(result.getVersionsBehind()).color(NamedTextColor.RED))
                                            .appendSpace().append(Component.text("versions behind. Update is available at "))
                                            .append(Component.text(result.getDownloadUrl()).color(NamedTextColor.AQUA).clickEvent(ClickEvent.openUrl(result.getDownloadUrl())))
                                    );
                                }
                            } else sender.sendMessage(result.getMessage());
                        }
                    } catch (InvalidResponseException e) {
                        sender.sendMessage("&4Failed to check for updates: &c" + e.getFriendlyMessage());
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
                    if (name.equalsIgnoreCase("all")) {
                        core.getLevelingManager().resetLeveling();
                        core.getLevelingManager().cachedUUIDS.invalidateAll();
                        core.getLevelingManager().getLevelingRewardsManager().setRewardCache(Utils.OBJECT_MAPPER.createObjectNode());
                        core.getLevelingManager().getLevelingRewardsManager().saveRewardCache();
                        sender.sendMessage("&eEveryone's level has been reset");
                    } else {
                        PlayerStats stats = core.getLevelingManager().getPlayerStats(name);
                        if (stats == null) {
                            sender.sendMessage("&cPlayer not found");
                        } else {
                            stats.setLevel(0);
                            stats.setXP(0);
                            core.getLevelingManager().getLevelingRewardsManager().getRewardCache().remove(stats.getUuid().toString());
                            core.getLevelingManager().getLevelingRewardsManager().saveRewardCache();
                            sender.sendMessage("&ePlayer's level has been reset.");
                        }
                    }
                    return;
                }
            } else if (args[0].equalsIgnoreCase("migrateLeveling")) {
                if (sender instanceof ConsoleCommandUser) { // Only console
                    if (migrated) {
                        sender.sendMessage("&cAlready migrated.");
                        return;
                    }
                    migrated = true;
                    sender.sendMessage("&cMigrating leveling to new mee6 leveling, please wait....");
                    core.getLevelingManager().convertToMee6();
                    sender.sendMessage("&eSuccessfully migrated, If you used leveling roles before, please reconfigure according to the new leveling system, keep in mind that leveling roles was upgraded to leveling-&lrewards&e.json (https://wiki.discordsrvutils.xyz/leveling-conversion)");
                    return;
                }
            } else if (args[0].equalsIgnoreCase("addxp") && core.getLevelingConfig().enabled()) {
                if (sender.hasPermission("discordsrvutils.addxp")) {
                    String name = args.length >= 2 ? args[1] : null;
                    if (name == null) {
                        sender.sendMessage("&cPlease provide player name.");
                        return;
                    }
                    String amountString = args.length >= 3 ? args[2] : null;
                    if (amountString == null || !Utils.isInt(amountString)) {
                        sender.sendMessage("&cPlease provide a valid xp amount.");
                        return;
                    }
                    PlayerStats stats = core.getLevelingManager().getPlayerStats(name);
                    if (stats == null) {
                        sender.sendMessage("&cPlayer not found");
                    } else {
                        int xp = Integer.parseInt(amountString);
                        stats.setXP(xp + stats.getXp(), new LevelupEvent(stats, stats.getUuid()));
                        sender.sendMessage("&aAdded &6" + xp + " &axp to &6" + name + "&a. Now they are level &6" + stats.getLevel() + " &aand have &6" + stats.getXp() + "&a xp.");
                        core.getLevelingManager().getLevelingRewardsManager().rewardIfOnline(stats);
                        return;
                    }
                }
            } else if ((args[0].equalsIgnoreCase("sendAfk") || args[0].equalsIgnoreCase("sendUnAfk")) && sender instanceof ConsoleCommandUser) {
                String name = args.length >= 2 ? args[1] : null;
                if (name == null) {
                    sender.sendMessage("&cPlease provide player name.");
                    return;
                }
                PlatformPlayer<?> player = core.getServer().getPlayer(name);
                if (player == null) {
                    sender.sendMessage("&cPlayer not found.");
                    return;
                }
                if (!player.shouldSendAfk()) return;
                if (core.getMainConfig().afk_message_enabled()) {
                    PlaceholdObjectList holders = new PlaceholdObjectList(core);
                    holders.add(new PlaceholdObject(core, player, "player"));
                    TextChannel channel = core.getJdaManager().getChannel(core.getMainConfig().afk_channel());
                    if (channel == null) {
                        core.severe("No Channel was found with ID " + core.getMainConfig().afk_channel() + ". Afk/NoLonger message was not sent for " + player.getName());
                        return;
                    }
                    Message msg = core.getMessageManager().getMessage(args[0].equalsIgnoreCase("sendAfk") ? core.getMainConfig().afk_message() : core.getMainConfig().no_longer_afk_message(), holders, player).build();
                    core.queueMsg(msg, channel).queue();
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
            if (sender.hasPermission("discordsrvutils.addxp") && core.getLevelingConfig().enabled())
                values.add("addxp");
        } else if ((args.length == 2 && args[0].equalsIgnoreCase("resetlevel") && sender.hasPermission("discordsrvutils.resetlevel"))
                || (args.length == 2 && args[0].equalsIgnoreCase("addxp") && sender.hasPermission("discordsrvutils.addxp") && core.getLevelingConfig().enabled())) {
            List<String> result = core.getPlatform().getServer().getOnlinePlayers().stream().map(PlatformPlayer::getName).collect(Collectors.toList());
            if (args[0].equalsIgnoreCase("resetlevel")) result.add("all");
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
