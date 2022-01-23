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

package tk.bluetree242.discordsrvutils.bukkit.listeners.afk.cmi;

import com.Zrips.CMI.events.CMIAfkEnterEvent;
import com.Zrips.CMI.events.CMIAfkLeaveEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.bukkit.BukkitPlayer;
import tk.bluetree242.discordsrvutils.bukkit.listeners.afk.essentials.EssentialsAFKListener;
import tk.bluetree242.discordsrvutils.systems.messages.MessageManager;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;

public class CMIAfkListener implements Listener {
    private final DiscordSRVUtils core = DiscordSRVUtils.get();

    @EventHandler
    public void onAfk(CMIAfkEnterEvent e) {
        core.executeAsync(() -> {
            Player player = e.getPlayer();
            if (!EssentialsAFKListener.shouldSend(player)) return;
            if (core.getMainConfig().afk_message_enabled()) {
                PlaceholdObjectList holders = new PlaceholdObjectList();
                holders.add(new PlaceholdObject(player, "player"));
                TextChannel channel = core.getChannel(core.getMainConfig().afk_channel());
                if (channel == null) {
                    core.severe("No Channel was found with ID " + core.getMainConfig().afk_channel() + ". Afk/NoLonger message was not sent for " + player.getName());
                    return;
                }
                Message msg = MessageManager.get().getMessage(core.getMainConfig().afk_message(), holders, new BukkitPlayer(player)).build();
                core.queueMsg(msg, channel).queue();
            }
        });
    }

    @EventHandler
    public void onNoLongerAfk(CMIAfkLeaveEvent e) {
        core.executeAsync(() -> {
            Player player = e.getPlayer();
            if (!EssentialsAFKListener.shouldSend(player)) return;
            if (core.getMainConfig().afk_message_enabled()) {
                PlaceholdObjectList holders = new PlaceholdObjectList();
                holders.add(new PlaceholdObject(player, "player"));
                TextChannel channel = core.getChannel(core.getMainConfig().afk_channel());
                if (channel == null) {
                    core.severe("No Channel was found with ID " + core.getMainConfig().afk_channel() + ". Afk/NoLonger message was not sent for " + player.getName());
                    return;
                }
                Message msg = MessageManager.get().getMessage(core.getMainConfig().no_longer_afk_message(), holders, new BukkitPlayer(player)).build();
                core.queueMsg(msg, channel).queue();
            }
        });
    }
}