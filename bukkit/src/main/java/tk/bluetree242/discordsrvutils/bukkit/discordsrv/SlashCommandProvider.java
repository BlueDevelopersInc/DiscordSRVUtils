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

package tk.bluetree242.discordsrvutils.bukkit.discordsrv;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import lombok.RequiredArgsConstructor;
import tk.bluetree242.discordsrvutils.bukkit.DiscordSRVUtilsBukkit;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandManager;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class SlashCommandProvider implements github.scarsz.discordsrv.api.commands.SlashCommandProvider {
    private final DiscordSRVUtilsBukkit core;
    @Override
    public Set<PluginSlashCommand> getSlashCommands() {
        Set<PluginSlashCommand> commands = new HashSet<>();
        if (core.getCore() == null || !core.getCore().isEnabled() || !core.getCore().getMainConfig().register_slash()) return commands;
        CommandManager manager = core.getCore().getCommandManager();
        for (Command command : manager.getCommands()) {
            if (!command.isEnabled()) continue;
            commands.add(getCmd(command.getCmd(), command));
            for (String alias : command.getAliases()) {
                commands.add(getCmd(alias, command));
            }
        }
        return commands;
    }


    private PluginSlashCommand getCmd(String alias, Command cmd) {
        return new PluginSlashCommand(core, new CommandData(alias, cmd.getDescription()).addOptions(cmd.getOptions()), DiscordSRV.getPlugin().getMainGuild() + "");
    }
}
