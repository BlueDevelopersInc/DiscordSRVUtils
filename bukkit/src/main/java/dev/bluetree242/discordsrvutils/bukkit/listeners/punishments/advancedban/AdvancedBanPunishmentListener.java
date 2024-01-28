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

package dev.bluetree242.discordsrvutils.bukkit.listeners.punishments.advancedban;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.interfaces.Punishment;
import lombok.RequiredArgsConstructor;
import me.leoko.advancedban.bukkit.event.PunishmentEvent;
import me.leoko.advancedban.bukkit.event.RevokePunishmentEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class AdvancedBanPunishmentListener implements Listener {
    private final DiscordSRVUtils core;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPunish(PunishmentEvent e) {
        core.getAsyncManager().executeAsync(() -> {
            AdvancedBanPunishment punishment = new AdvancedBanPunishment(e.getPunishment(), false);
            Punishment.handlePunishment(punishment, core);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRevoke(RevokePunishmentEvent e) {
        core.getAsyncManager().executeAsync(() -> {
            AdvancedBanPunishment punishment = new AdvancedBanPunishment(e.getPunishment(), true);
            Punishment.handlePunishment(punishment, core);
        });
    }


    public void remove() {
        PunishmentEvent.getHandlerList().unregister((Plugin) core.getPlatform().getOriginal());
        RevokePunishmentEvent.getHandlerList().unregister((Plugin) core.getPlatform().getOriginal());
    }
}
