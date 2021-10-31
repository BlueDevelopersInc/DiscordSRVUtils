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

package tk.bluetree242.discordsrvutils.leveling.listeners.jda;


import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.events.DiscordLevelupEvent;
import tk.bluetree242.discordsrvutils.leveling.LevelingManager;
import tk.bluetree242.discordsrvutils.leveling.MessageType;
import tk.bluetree242.discordsrvutils.messages.MessageManager;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;

import java.security.SecureRandom;

public class DiscordLevelingListener extends ListenerAdapter {
    private DiscordSRVUtils core = DiscordSRVUtils.get();

    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (core.getMainConfig().bungee_mode()) return;
        core.executeAsync(() -> {
            if (e.getMessage().isWebhookMessage()) return;
            if (e.getAuthor().isBot()) return;
            if (e.getGuild().getIdLong() == core.getGuild().getIdLong()) {
                if (core.getLevelingConfig().enabled()) {
                    core.handleCF(LevelingManager.get().getPlayerStats(e.getMember().getIdLong()), stats -> {
                        if (stats == null) {
                            return;
                        }
                        if (core.getLevelingConfig().antispam_messages()) {
                            Long val = LevelingManager.get().antispamMap.get(stats.getUuid());
                            if (val == null) {
                                LevelingManager.get().antispamMap.put(stats.getUuid(), System.nanoTime());
                            } else {
                                if (!(System.nanoTime() - val >= LevelingManager.get().MAP_EXPIRATION_NANOS)) return;
                                LevelingManager.get().antispamMap.remove(stats.getUuid());
                                LevelingManager.get().antispamMap.put(stats.getUuid(), System.nanoTime());
                            }
                        }
                        int toAdd = new SecureRandom().nextInt(50);
                        boolean leveledUp = core.handleCFOnAnother(stats.setXP(stats.getXp() + toAdd));
                        core.handleCFOnAnother(stats.addMessage(MessageType.DISCORD));
                        if (leveledUp) {
                            try {
                                DiscordSRV.api.callEvent(new DiscordLevelupEvent(stats, e.getChannel(), e.getAuthor()));
                            } catch (Exception x) {
                            }
                            e.getChannel().sendMessage(MessageManager.get().getMessage(core.getLevelingConfig().discord_message(), PlaceholdObjectList.ofArray(
                                    new PlaceholdObject(stats, "stats"),
                                    new PlaceholdObject(e.getAuthor(), "user"),
                                    new PlaceholdObject(e.getMember(), "member"),
                                    new PlaceholdObject(e.getGuild(), "guild")
                            ), null).build()).queue();
                        }
                    }, null);
                }
            }
        });
    }
}