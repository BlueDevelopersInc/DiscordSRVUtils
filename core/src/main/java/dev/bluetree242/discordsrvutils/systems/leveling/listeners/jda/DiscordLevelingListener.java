/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2023 BlueTree242
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

package dev.bluetree242.discordsrvutils.systems.leveling.listeners.jda;


import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.events.DiscordLevelupEvent;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import dev.bluetree242.discordsrvutils.systems.leveling.MessageType;
import dev.bluetree242.discordsrvutils.systems.leveling.PlayerStats;
import dev.bluetree242.discordsrvutils.utils.Utils;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberJoinEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class DiscordLevelingListener extends ListenerAdapter {
    private final DiscordSRVUtils core;

    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent e) {
        if (core.getMainConfig().bungee_mode()) return;
        core.getAsyncManager().executeAsync(() -> {
            if (e.getMessage().isWebhookMessage()) return;
            if (e.getAuthor().isBot()) return;
            if (core.getPlatform().getDiscordSRV().getMainGuild().getIdLong() == core.getPlatform().getDiscordSRV().getMainGuild().getIdLong()) {
                if (core.getLevelingConfig().enabled()) {
                    PlayerStats stats = core.getLevelingManager().getPlayerStats(e.getMember().getIdLong());
                    if (stats == null) {
                        return;
                    }
                    if (core.getLevelingConfig().antispam_messages()) {
                        Long val = core.getLevelingManager().antispamMap.get(stats.getUuid());
                        if (val == null) {
                            core.getLevelingManager().antispamMap.put(stats.getUuid(), System.nanoTime());
                        } else {
                            if (!(System.nanoTime() - val >= core.getLevelingManager().MAP_EXPIRATION_NANOS))
                                return;
                            core.getLevelingManager().antispamMap.remove(stats.getUuid());
                            core.getLevelingManager().antispamMap.put(stats.getUuid(), System.nanoTime());
                        }
                    }
                    int toAdd = Utils.nextInt(15, 25);
                    boolean leveledUp = stats.setXP(stats.getXp() + toAdd, new DiscordLevelupEvent(stats, e.getChannel(), e.getAuthor()));
                    stats.addMessage(MessageType.DISCORD);
                    if (leveledUp) {
                        core.queueMsg(core.getMessageManager().getMessage(core.getLevelingConfig().discord_message(), PlaceholdObjectList.ofArray(core,
                                new PlaceholdObject(core, stats, "stats"),
                                new PlaceholdObject(core, e.getAuthor(), "user"),
                                new PlaceholdObject(core, e.getMember(), "member"),
                                new PlaceholdObject(core, core.getPlatform().getDiscordSRV().getMainGuild(), "guild")
                        ), null).build(), core.getJdaManager().getChannel(core.getLevelingConfig().discord_channel(), e.getChannel())).queue();
                        core.getLevelingManager().getLevelingRewardsManager().rewardIfOnline(stats);
                    }
                }
            }
        });
    }


    //give leveling roles when they rejoin the discord server
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent e) {
        core.getAsyncManager().executeAsync(() -> {
            if (core.getDiscordSRV().getUuid(e.getUser().getId()) != null) {
                PlayerStats stats = core.getLevelingManager().getPlayerStats(e.getUser().getIdLong());
                if (stats == null) return;
                List<Role> toAdd = core.getLevelingManager().getLevelingRewardsManager().getRolesForLevel(stats.getLevel());
                Collection actions = new ArrayList<>();
                for (Role role : toAdd) {
                    actions.add(core.getPlatform().getDiscordSRV().getMainGuild().addRoleToMember(e.getMember(), role).reason("Account Linked"));
                }
                if (!actions.isEmpty()) RestAction.allOf(actions).queue();
            }
        });
    }
}