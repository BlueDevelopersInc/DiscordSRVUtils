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

package dev.bluetree242.discordsrvutils.bukkit.listeners.afk.essentials;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.earth2me.essentials.Essentials;
import de.myzelyam.api.vanish.VanishAPI;
import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import lombok.RequiredArgsConstructor;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import dev.bluetree242.discordsrvutils.bukkit.BukkitPlayer;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;

@RequiredArgsConstructor
public class EssentialsAFKListener implements Listener {

    private final DiscordSRVUtils core;

    public static boolean  shouldSend(Player p) {
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("Essentials")) {
            Essentials plugin = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            if (plugin.getUser(p.getUniqueId()).isHidden()) return false;
        }
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("CMI")) {
            CMIUser user = CMI.getInstance().getPlayerManager().getUser(p);
            if (user.isVanished()) return false;
        }
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getServer().getPluginManager().isPluginEnabled("PremiumVanish")) {
            return !VanishAPI.isInvisible(p);
        }
        return true;
    }

    public void remove() {
        AfkStatusChangeEvent.getHandlerList().unregister((Plugin) core.getPlatform().getOriginal());
    }

    @EventHandler
    public void onAfk(AfkStatusChangeEvent e) {
        core.getAsyncManager().executeAsync(() -> {
            boolean afk = e.getAffected().isAfk();
            Player player = e.getAffected().getBase();
            if (!shouldSend(player)) return;
            if (core.getMainConfig().afk_message_enabled()) {
                PlaceholdObjectList holders = new PlaceholdObjectList(core);
                holders.add(new PlaceholdObject(core, player, "player"));
                TextChannel channel = core.getJdaManager().getChannel(core.getMainConfig().afk_channel());
                if (channel == null) {
                    core.severe("No Channel was found with ID " + core.getMainConfig().afk_channel() + ". Afk/NoLonger message was not sent for " + player.getName());
                    return;
                }
                Message msg;
                if (e.getValue()) {
                    msg = core.getMessageManager().getMessage(core.getMainConfig().afk_message(), holders, new BukkitPlayer(core, player)).build();
                } else {
                    msg = core.getMessageManager().getMessage(core.getMainConfig().no_longer_afk_message(), holders, new BukkitPlayer(core, player)).build();
                }
                core.queueMsg(msg, channel).queue();
            }
        });
    }
}
