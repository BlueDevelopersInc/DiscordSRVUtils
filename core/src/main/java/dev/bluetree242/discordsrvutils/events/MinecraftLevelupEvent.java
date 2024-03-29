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

package dev.bluetree242.discordsrvutils.events;

import dev.bluetree242.discordsrvutils.platform.PlatformPlayer;
import dev.bluetree242.discordsrvutils.systems.leveling.PlayerStats;
import lombok.Getter;

@Getter
public class MinecraftLevelupEvent extends LevelupEvent {
    private final PlayerStats stats;
    private final PlatformPlayer player;

    public MinecraftLevelupEvent(PlayerStats stats, PlatformPlayer player) {
        super(stats, player.getUniqueId());
        this.stats = stats;
        this.player = player;
    }

}
