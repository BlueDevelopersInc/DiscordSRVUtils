/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2022 BlueTree242
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

package dev.bluetree242.discordsrvutils.bukkit.cmd;

import dev.bluetree242.discordsrvutils.platform.command.ConsoleCommandUser;
import dev.bluetree242.discordsrvutils.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class BukkitConsoleCommandUser extends ConsoleCommandUser {
    @Getter
    private final CommandSender sender = Bukkit.getConsoleSender();

    @Override
    public String getName() {
        return sender.getName();
    }

    @Override
    public boolean hasPermission(String node) {
        return true;
    }

    @Override
    public void sendMessage(String msg) {
        sender.sendMessage(Utils.colors(msg));
    }
}
