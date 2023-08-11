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

package tk.bluetree242.discordsrvutils.bukkit.listeners.punishments.advancedban;

import lombok.RequiredArgsConstructor;
import me.leoko.advancedban.manager.UUIDManager;
import tk.bluetree242.discordsrvutils.interfaces.Punishment;
import tk.bluetree242.discordsrvutils.utils.Utils;

import java.util.UUID;

@RequiredArgsConstructor
public class AdvancedBanPunishment implements Punishment<me.leoko.advancedban.utils.Punishment> {
    private final me.leoko.advancedban.utils.Punishment punishment;
    private final boolean revoke;

    @Override
    public String getDuration() {
        if (isPermanent())
            return "Permanent";
        return Utils.getDuration((punishment.getEnd() - punishment.getStart()) + 1);
    }

    @Override
    public String getOperator() {
        return punishment.getOperator();
    }

    @Override
    public String getName() {
        return punishment.getName();
    }

    @Override
    public String getReason() {
        return punishment.getReason();
    }

    @Override
    public boolean isPermanent() {
        return punishment.getEnd() == -1;
    }

    @Override
    public me.leoko.advancedban.utils.Punishment getOrigin() {
        return punishment;
    }

    @Override
    public PunishmentProvider getPunishmentProvider() {
        return PunishmentProvider.ADVANCEDBAN;
    }

    @Override
    public PunishmentType getPunishmentType() {
        if (punishment.getType().getBasic() == me.leoko.advancedban.utils.PunishmentType.WARNING)
            return PunishmentType.WARN;
        return PunishmentType.get(punishment.getType().getBasic().name());
    }

    @Override
    public boolean isGrant() {
        return !revoke;
    }

    @Override
    public boolean isIp() {
        return punishment.getType().isIpOrientated();
    }

    @Override
    public UUID getTargetUUID() {
        return UUID.fromString(punishment.getUuid());
    }
}
