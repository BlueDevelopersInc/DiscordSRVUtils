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

package tk.bluetree242.discordsrvutils.bukkit.cmd;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.bukkit.BukkitPlayer;
import tk.bluetree242.discordsrvutils.commands.game.DiscordSRVUtilsCommand;
import tk.bluetree242.discordsrvutils.platform.command.CommandUser;
import tk.bluetree242.discordsrvutils.utils.Utils;

import java.util.List;

public class BukkitCommandListener implements CommandExecutor, TabCompleter {
    public DiscordSRVUtilsCommand cmd = new DiscordSRVUtilsCommand();
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return cmd.onTabComplete(args, wrapUser(sender), alias);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        DiscordSRVUtils.get().executeAsync(() -> {
            try {
                cmd.onRunAsync(args, wrapUser(sender), label);
            } catch (Throwable throwable) {
                sender.sendMessage(Utils.colors("&cError Running Command"));
                throwable.printStackTrace();
            }
        });
        return true;
    }

    private CommandUser wrapUser(CommandSender sender) {
        if (sender instanceof Player) {
            return new BukkitPlayer(((Player) sender));
        }
        else if (sender instanceof ConsoleCommandSender) {
            return new BukkitConsoleCommandUser();
        }
        else {
            return new BukkitCommandUser(sender);
        }
    }
}
