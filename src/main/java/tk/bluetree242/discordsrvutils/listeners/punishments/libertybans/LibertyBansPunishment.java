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

package tk.bluetree242.discordsrvutils.listeners.punishments.libertybans;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import space.arim.libertybans.api.*;
import space.arim.libertybans.api.punish.DraftPunishment;
import tk.bluetree242.discordsrvutils.interfaces.Punishment;
import tk.bluetree242.discordsrvutils.utils.Utils;

public class LibertyBansPunishment implements Punishment {
    private space.arim.libertybans.api.punish.Punishment punishment;

    public LibertyBansPunishment(space.arim.libertybans.api.punish.Punishment punishment) {
        this.punishment = punishment;
    }
    @Override
    public String getDuration() {
        if (punishment.isPermanent()) return "Permanent";
        return Utils.getDuration((punishment.getEndDate().toEpochMilli() - punishment.getStartDate().getEpochSecond()));
    }

    @Override
    public String getOperator() {
        if (punishment.getOperator().getType() == Operator.OperatorType.CONSOLE) {
            return "CONSOLE";
        } else {
            PlayerOperator operator = (PlayerOperator) punishment.getOperator();
            String name = Bukkit.getOfflinePlayer(operator.getUUID()).getName();
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
}
