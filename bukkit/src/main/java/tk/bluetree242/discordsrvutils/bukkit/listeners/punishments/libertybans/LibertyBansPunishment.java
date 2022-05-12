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

package tk.bluetree242.discordsrvutils.bukkit.listeners.punishments.libertybans;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import space.arim.libertybans.api.Operator;
import space.arim.libertybans.api.PlayerOperator;
import space.arim.libertybans.api.PlayerVictim;
import space.arim.libertybans.api.Victim;
import tk.bluetree242.discordsrvutils.interfaces.Punishment;
import tk.bluetree242.discordsrvutils.utils.Utils;

@RequiredArgsConstructor
public class LibertyBansPunishment implements Punishment<space.arim.libertybans.api.punish.Punishment> {
    private final space.arim.libertybans.api.punish.Punishment punishment;
    private final Operator operator;
    private final boolean revoke;

    @Override
    public String getDuration() {
        if (punishment.isPermanent()) return "Permanent";
        return Utils.getDuration((punishment.getEndDate().toEpochMilli() - punishment.getStartDate().toEpochMilli()));
    }

    @Override
    public String getOperator() {
        if (operator.getType() == Operator.OperatorType.CONSOLE) {
            return "CONSOLE";
        } else {
            PlayerOperator operatorplayer = (PlayerOperator) operator;
            String name = Bukkit.getOfflinePlayer(operatorplayer.getUUID()).getName();
            return name == null ? "Unknown" : name;
        }
    }

    @Override
    public String getName() {
        if (punishment.getVictim().getType() == Victim.VictimType.ADDRESS) {
            return "Unknown";
        } else {
            PlayerVictim victim = (PlayerVictim) punishment.getVictim();
            String name = Bukkit.getOfflinePlayer(victim.getUUID()).getName();
            return name == null ? "Unknown" : name;
        }
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
    public space.arim.libertybans.api.punish.Punishment getOrigin() {
        return punishment;
    }

    @Override
    public PunishmentProvider getPunishmentProvider() {
        return PunishmentProvider.LIBERTYBANS;
    }

    @Override
    public PunishmentType getPunishmentType() {
        switch (punishment.getType()) {
            case BAN:
                return PunishmentType.BAN;
            case MUTE:
                return PunishmentType.MUTE;
            default:
                return null;
        }
    }



    @Override
    public boolean isRevoke() {
        return revoke;
    }

    @Override
    public boolean isIp() {
        return !(punishment.getVictim() instanceof PlayerVictim);
    }
}
