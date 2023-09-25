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

package dev.bluetree242.discordsrvutils.systems.commandmanagement;


import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.commands.discord.invitetracking.InvitesCommand;
import dev.bluetree242.discordsrvutils.commands.discord.leveling.LeaderboardCommand;
import dev.bluetree242.discordsrvutils.commands.discord.leveling.LevelCommand;
import dev.bluetree242.discordsrvutils.commands.discord.suggestions.ApproveSuggestionCommand;
import dev.bluetree242.discordsrvutils.commands.discord.suggestions.DenySuggestionCommand;
import dev.bluetree242.discordsrvutils.commands.discord.suggestions.SuggestCommand;
import dev.bluetree242.discordsrvutils.commands.discord.suggestions.SuggestionNoteCommand;
import dev.bluetree242.discordsrvutils.commands.discord.HelpCommand;
import dev.bluetree242.discordsrvutils.commands.discord.admin.EchoCommand;
import dev.bluetree242.discordsrvutils.commands.discord.admin.TestMessageCommand;
import dev.bluetree242.discordsrvutils.commands.discord.other.LinkAccountCommand;
import dev.bluetree242.discordsrvutils.commands.discord.other.UnlinkAccountCommand;
import dev.bluetree242.discordsrvutils.commands.discord.status.StatusCommand;
import dev.bluetree242.discordsrvutils.commands.discord.tickets.*;
import dev.bluetree242.discordsrvutils.commands.discord.tickets.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {
    private final DiscordSRVUtils core;
    private final ConcurrentHashMap<String, Command> cmds = new ConcurrentHashMap<>();
    private final List<Command> commands = new ArrayList<>();
    private final List<Command> commandsWithoutAliases = new ArrayList<>();

    public CommandManager(DiscordSRVUtils core) {
        this.core = core;
        registerCommands();
    }

    public void registerCommands() {
        registerCommand(new EchoCommand(core));
        registerCommand(new TestMessageCommand(core));
        registerCommand(new HelpCommand(core));
        registerCommand(new CreatePanelCommand(core));
        registerCommand(new PanelListCommand(core));
        registerCommand(new DeletePanelCommand(core));
        registerCommand(new EditPanelCommand(core));
        registerCommand(new CloseCommand(core));
        registerCommand(new ReopenCommand(core));
        registerCommand(new LevelCommand(core));
        registerCommand(new LeaderboardCommand(core));
        registerCommand(new SuggestCommand(core));
        registerCommand(new SuggestionNoteCommand(core));
        registerCommand(new ApproveSuggestionCommand(core));
        registerCommand(new DenySuggestionCommand(core));
        registerCommand(new StatusCommand(core));
        registerCommand(new LinkAccountCommand(core));
        registerCommand(new UnlinkAccountCommand(core));
        registerCommand(new InvitesCommand(core));
    }


    public void registerCommand(Command cmd) {
        if (getCommandHashMap().get(cmd.getCmd()) != null) return;
        cmds.put(cmd.getCmd().toLowerCase(), cmd);
        commands.add(cmd);
        commandsWithoutAliases.add(cmd);
        for (String a : cmd.getAliases()) {
            cmds.put(a.toLowerCase(), cmd);
        }
    }

    public ConcurrentHashMap<String, Command> getCommandHashMap() {
        return cmds;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public List<Command> getDisabledCommands(boolean onlyConfig) {
        List<Command> result = new ArrayList<>();
        if (onlyConfig)
            for (String command : core.getMainConfig().disabled_commands()) {
                result.add(getCommandHashMap().get(command.toLowerCase(Locale.ROOT)));
            }
        else for (Command cmd : commandsWithoutAliases) {
            if (!cmd.isEnabled()) result.add(cmd);
        }
        return result;
    }

    public List<Command> getCommandsWithoutAliases() {
        return commandsWithoutAliases;
    }
}
