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

package tk.bluetree242.discordsrvutils.listeners.afk;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.earth2me.essentials.Essentials;
import de.myzelyam.api.vanish.VanishAPI;
import de.myzelyam.supervanish.SuperVanish;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.messages.MessageManager;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;

public class EssentialsAFKListener implements Listener {

    private DiscordSRVUtils core = DiscordSRVUtils.get();

    @EventHandler
    public void onAfk(AfkStatusChangeEvent e) {
        core.executeAsync(() -> {
            boolean afk = e.getAffected().isAfk();
            Player player = e.getAffected().getBase();
            if (!shouldSend(player)) return;
            if (core.getMainConfig().afk_message_enabled()) {
                PlaceholdObjectList holders = new PlaceholdObjectList();
                holders.add(new PlaceholdObject(player, "player"));
                TextChannel channel = core.getChannel(core.getMainConfig().afk_channel());
                if (channel == null) {
                    core.severe("No Channel was found with ID " + core.getMainConfig().afk_channel() + ". Afk/NoLonger message was not sent for " + player.getName());
                    return;
                }
                Message msg;
                if (e.getValue()) {
                    msg = MessageManager.get().getMessage(core.getMainConfig().afk_message(), holders, player).build();
                } else {
                    msg = MessageManager.get().getMessage(core.getMainConfig().no_longer_afk_message(), holders, player).build();
                }
                core.queueMsg(msg, channel).queue();
            }
        });
    }

    public static boolean shouldSend(Player p) {
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("Essentials")) {
            Essentials plugin = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            if (plugin.getUser(p.getUniqueId()).isHidden()) return false;
        }
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("CMI")) {
            CMIUser user = CMI.getInstance().getPlayerManager().getUser(p);
            if (user.isVanished()) return false;
        }
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getServer().getPluginManager().isPluginEnabled("PremiumVanish")) {
            if (VanishAPI.isInvisible(p)) return false;
        }
        return true;
    }
}
