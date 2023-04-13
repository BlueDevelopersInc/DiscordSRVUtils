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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.platform.PlatformPlayer;

import java.util.UUID;

@RequiredArgsConstructor
public class BukkitOfflinePlayer extends PlatformPlayer {
    @Getter
    private final OfflinePlayer player;
    private final DiscordSRVUtils core;

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public void sendMessage(String msg) {
        //offline
    }

    @Override
    public boolean hasPermission(String node) {
        return false; //false for security
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public String placeholders(String s) {
        return core.getPlatform().placehold(this, s);
    }
}
