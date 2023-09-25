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

package dev.bluetree242.discordsrvutils.bukkit.listeners.punishments.libertybans;

import dev.bluetree242.discordsrvutils.utils.Utils;
import lombok.RequiredArgsConstructor;
import space.arim.libertybans.api.*;
import space.arim.libertybans.api.punish.Punishment;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
public class LibertyBansPunishment implements dev.bluetree242.discordsrvutils.interfaces.Punishment<Punishment> {
    private final space.arim.libertybans.api.punish.Punishment punishment;
    private final Operator operator;
    private final boolean revoke;
    private final LibertyBans plugin;

    private String operatorName = null;
    private String targetName = null;

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
            String name = retrieveName(true);
            return name == null ? "Unknown" : name;
        }
    }

    @Override
    public String getName() {
        if (punishment.getVictim().getType() == Victim.VictimType.ADDRESS) {
            return "Unknown";
        } else {
            String name = retrieveName(false);
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
        return PunishmentType.get(punishment.getType().name());
    }


    @Override
    public boolean isGrant() {
        return !revoke;
    }

    @Override
    public boolean isIp() {
        return !(punishment.getVictim() instanceof PlayerVictim);
    }

    @Override
    public UUID getTargetUUID() {
        if (punishment.getVictim().getType() == Victim.VictimType.ADDRESS) {
            return null;
        } else {
            PlayerVictim victim = (PlayerVictim) punishment.getVictim();
            return victim.getUUID();
        }
    }

    private String retrieveName(boolean operator) {
        String saved = operator ? operatorName : targetName;
        if (saved != null) return saved.equals("NONE@*") ? null : saved;
        String result = null;
        try {
            result = plugin.getUserResolver().lookupName(operator ? getOperatorUUID() : getTargetUUID()).get().orElse(null);
        } catch (InterruptedException | ExecutionException e) {
            //nothing
        }
        if (result != null) {
            if (operator) operatorName = result;
            else targetName = result;
        } else {
            if (operator) operatorName = "NONE@*";
            else targetName = "NONE@*";
        }
        return result;
    }

    private UUID getOperatorUUID() {
        if (operator.getType() != Operator.OperatorType.PLAYER) return null;
        return ((PlayerOperator) operator).getUUID();
    }
}
