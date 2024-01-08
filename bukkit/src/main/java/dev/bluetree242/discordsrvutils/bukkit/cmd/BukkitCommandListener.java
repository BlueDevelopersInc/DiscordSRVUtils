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

package dev.bluetree242.discordsrvutils.bukkit.cmd;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.bukkit.BukkitPlayer;
import dev.bluetree242.discordsrvutils.bukkit.DiscordSRVUtilsBukkit;
import dev.bluetree242.discordsrvutils.commands.game.DiscordSRVUtilsCommand;
import dev.bluetree242.discordsrvutils.platform.command.CommandUser;
import dev.bluetree242.discordsrvutils.utils.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BukkitCommandListener implements CommandExecutor, TabCompleter {
    private final DiscordSRVUtils core;
    private final DiscordSRVUtilsCommand cmd;
    private final DiscordSRVUtilsBukkit main;

    public BukkitCommandListener(DiscordSRVUtils core, DiscordSRVUtilsBukkit main) {
        this.core = core;
        cmd = new DiscordSRVUtilsCommand(core);
        this.main = main;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return cmd.onTabComplete(args, wrapUser(sender), alias);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        core.getAsyncManager().executeAsync(() -> {
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
            return new BukkitPlayer(core, ((Player) sender));
        } else if (sender instanceof ConsoleCommandSender) {
            return new BukkitConsoleCommandUser(main);
        } else {
            return new BukkitCommandUser(sender);
        }
    }
}
