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

package dev.bluetree242.discordsrvutils.bukkit.listeners.punishments;

import dev.bluetree242.discordsrvutils.interfaces.Punishment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class GenericPunishment implements Punishment {
    private final String duration;
    private final String operator;
    private final String name;
    private final String reason;
    private final boolean permanent;
    private final Object origin;
    private final PunishmentProvider punishmentProvider;
    private final PunishmentType punishmentType;
    private final boolean grant;
    private final boolean ip;
    private final UUID targetUUID;
    private final String revoker;
}
