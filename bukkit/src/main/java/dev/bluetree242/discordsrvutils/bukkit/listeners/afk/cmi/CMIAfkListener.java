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

package dev.bluetree242.discordsrvutils.bukkit.listeners.afk.cmi;

import com.Zrips.CMI.events.CMIAfkEnterEvent;
import com.Zrips.CMI.events.CMIAfkLeaveEvent;
import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import dev.bluetree242.discordsrvutils.bukkit.BukkitPlayer;
import dev.bluetree242.discordsrvutils.bukkit.listeners.afk.essentials.EssentialsAFKListener;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;

@RequiredArgsConstructor
public class CMIAfkListener implements Listener {
    private final DiscordSRVUtils core;

    @EventHandler
    public void onAfk(CMIAfkEnterEvent e) {
        core.getAsyncManager().executeAsync(() -> {
            Player player = e.getPlayer();
            if (!EssentialsAFKListener.shouldSend(player)) return;
            if (core.getMainConfig().afk_message_enabled()) {
                PlaceholdObjectList holders = new PlaceholdObjectList(core);
                holders.add(new PlaceholdObject(core, player, "player"));
                TextChannel channel = core.getJdaManager().getChannel(core.getMainConfig().afk_channel());
                if (channel == null) {
                    core.severe("No Channel was found with ID " + core.getMainConfig().afk_channel() + ". Afk/NoLonger message was not sent for " + player.getName());
                    return;
                }
                Message msg = core.getMessageManager().getMessage(core.getMainConfig().afk_message(), holders, new BukkitPlayer(core, player)).build();
                core.queueMsg(msg, channel).queue();
            }
        });
    }

    @EventHandler
    public void onNoLongerAfk(CMIAfkLeaveEvent e) {
        core.getAsyncManager().executeAsync(() -> {
            Player player = e.getPlayer();
            if (!EssentialsAFKListener.shouldSend(player)) return;
            if (core.getMainConfig().afk_message_enabled()) {
                PlaceholdObjectList holders = new PlaceholdObjectList(core);
                holders.add(new PlaceholdObject(core, player, "player"));
                TextChannel channel = core.getJdaManager().getChannel(core.getMainConfig().afk_channel());
                if (channel == null) {
                    core.severe("No Channel was found with ID " + core.getMainConfig().afk_channel() + ". Afk/NoLonger message was not sent for " + player.getName());
                    return;
                }
                Message msg = core.getMessageManager().getMessage(core.getMainConfig().no_longer_afk_message(), holders, new BukkitPlayer(core, player)).build();
                core.queueMsg(msg, channel).queue();
            }
        });
    }
}
