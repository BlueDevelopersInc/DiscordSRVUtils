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

package tk.bluetree242.discordsrvutils.commandmanagement;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.utils.Utils;

public  abstract class BukkitCommand implements CommandExecutor {
    protected  DiscordSRVUtils core = DiscordSRVUtils.get();
    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        DiscordSRVUtils.get().executeAsync(() -> {
            try {
                onRunAsync(sender, command, label, args);
            } catch (Throwable ex) {
                ex.printStackTrace();
                sender.sendMessage(Utils.colors("&cAn internal error occurred while executing this command"));
            }
        });
        return true;
    }

    public abstract void onRunAsync(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws Throwable;


    public String colors(String s) {
        return Utils.colors(s);
    }

}
