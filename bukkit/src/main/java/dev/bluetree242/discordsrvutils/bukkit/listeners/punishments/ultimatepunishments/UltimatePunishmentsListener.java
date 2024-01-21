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

package dev.bluetree242.discordsrvutils.bukkit.listeners.punishments.ultimatepunishments;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.bukkit.listeners.punishments.GenericPunishment;
import dev.bluetree242.discordsrvutils.interfaces.Punishment;
import lombok.RequiredArgsConstructor;
import me.TechsCode.UltraPunishments.event.BanEvent;
import me.TechsCode.UltraPunishments.event.KickEvent;
import me.TechsCode.UltraPunishments.event.UnbanEvent;
import me.leoko.advancedban.bukkit.event.PunishmentEvent;
import me.leoko.advancedban.bukkit.event.RevokePunishmentEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class UltimatePunishmentsListener implements Listener {
    private final DiscordSRVUtils core;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBan(BanEvent e) {
        handle(Punishment.PunishmentType.BAN, e, true, e.getPlayer(), e.getIssuer(), e.getReason());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUnBan(UnbanEvent e) {
        handle(Punishment.PunishmentType.BAN, e, false, e.getPlayer(), e.getIssuer(), "Original reason unknown.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKick(KickEvent e) {
        handle(Punishment.PunishmentType.KICK, e, true, e.getPlayer(), e.getIssuer(), e.getReason());
    }

    private void handle(Punishment.PunishmentType type, Object origin, boolean grant, OfflinePlayer p, CommandSender issuer, String reason) {
        GenericPunishment punishment = new GenericPunishment("Permanent",
                issuer.getName(),
                p.getName(),
                reason,
                true,
                origin,
                Punishment.PunishmentProvider.ULTIMATEPUNISHMENTS,
                type, grant,
                false,
                p.getUniqueId(),
                grant ? null : issuer.getName());
        Punishment.handlePunishment(punishment, core);
    }

    public void remove() {
        PunishmentEvent.getHandlerList().unregister((Plugin) core.getPlatform().getOriginal());
        RevokePunishmentEvent.getHandlerList().unregister((Plugin) core.getPlatform().getOriginal());
    }
}
