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

package dev.bluetree242.discordsrvutils.bukkit.listeners.punishments.litebans;

import dev.bluetree242.discordsrvutils.interfaces.Punishment;
import dev.bluetree242.discordsrvutils.utils.Utils;
import litebans.api.Entry;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@RequiredArgsConstructor
public class LitebansPunishment implements Punishment<Entry> {
    private final Entry punishment;
    private final boolean revoke;


    protected static OfflinePlayer toOfflinePlayer(String uuid) {
        return Bukkit.getOfflinePlayer(UUID.fromString(uuid));
    }

    @Override
    public String getDuration() {
        if (punishment.isPermanent())
            return "Permanent";
        return Utils.getDuration((punishment.getDateEnd() - punishment.getDateStart()));
    }

    @Override
    public String getOperator() {
        return punishment.getExecutorName();
    }

    @Override
    public String getName() {
        OfflinePlayer p = toOfflinePlayer(punishment.getUuid());
        if (p == null) return "Unknown";
        return p.getName() == null ? "Unknown" : p.getName();
    }

    @Override
    public String getReason() {
        return punishment.getReason();
    }

    @Override
    public boolean isPermanent() {
        return punishment.isPermanent();
    }

    @Override
    public Entry getOrigin() {
        return punishment;
    }

    @Override
    public PunishmentProvider getPunishmentProvider() {
        return PunishmentProvider.LITEBANS;
    }

    @Override
    public PunishmentType getPunishmentType() {
        return PunishmentType.get(punishment.getType());
    }

    @Override
    public boolean isGrant() {
        return !revoke;
    }

    @Override
    public boolean isIp() {
        return punishment.isIpban();
    }

    @Override
    public UUID getTargetUUID() {
        return punishment.getUuid() == null ? null : UUID.fromString(punishment.getUuid());
    }
}
