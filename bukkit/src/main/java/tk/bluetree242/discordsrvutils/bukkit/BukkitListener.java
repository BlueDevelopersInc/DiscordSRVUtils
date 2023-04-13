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

package tk.bluetree242.discordsrvutils.bukkit;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.platform.PlatformPlayer;
import tk.bluetree242.discordsrvutils.platform.events.PlatformChatEvent;
import tk.bluetree242.discordsrvutils.platform.events.PlatformJoinEvent;
import tk.bluetree242.discordsrvutils.platform.listener.PlatformListener;

@RequiredArgsConstructor
public class BukkitListener implements Listener {
    private final DiscordSRVUtils core;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        PlatformChatEvent event = new PlatformChatEvent() {
            @Override
            public PlatformPlayer getPlayer() {
                return new BukkitPlayer(core, e.getPlayer());
            }

            @Override
            public boolean isCancelled() {
                return e.isCancelled();
            }
        };

        for (Object o : core.getPlatform().getListeners()) {
            PlatformListener listener = (PlatformListener) o;
            listener.onChat(event);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(PlayerJoinEvent e) {
        PlatformJoinEvent event = new PlatformJoinEvent() {
            @Override
            public PlatformPlayer getPlayer() {
                return new BukkitPlayer(core, e.getPlayer());
            }
        };

        for (Object o : core.getPlatform().getListeners()) {
            PlatformListener listener = (PlatformListener) o;
            listener.onJoin(event);
        }
    }


}

