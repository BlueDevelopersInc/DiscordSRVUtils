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


import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.commands.discord.HelpCommand;
import tk.bluetree242.discordsrvutils.commands.discord.admin.TestMessageCommand;
import tk.bluetree242.discordsrvutils.commands.discord.leveling.LeaderboardCommand;
import tk.bluetree242.discordsrvutils.commands.discord.leveling.LevelCommand;
import tk.bluetree242.discordsrvutils.commands.discord.suggestions.ApproveSuggestionCommand;
import tk.bluetree242.discordsrvutils.commands.discord.suggestions.DenySuggestionCommand;
import tk.bluetree242.discordsrvutils.commands.discord.suggestions.SuggestCommand;
import tk.bluetree242.discordsrvutils.commands.discord.suggestions.SuggestionNoteCommand;
import tk.bluetree242.discordsrvutils.commands.discord.tickets.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {
    private static CommandManager main;
    private final DiscordSRVUtils core = DiscordSRVUtils.get();
    private final ConcurrentHashMap<String, Command> cmds = new ConcurrentHashMap<>();
    private final List<Command> commands = new ArrayList<>();
    private final List<Command> commandswithoutaliases = new ArrayList<>();

    public CommandManager() {
        main = this;
        registerCommands();
    }

    public static CommandManager get() {
        return main;
    }

    public void registerCommands() {
        CommandManager.get().registerCommand(new TestMessageCommand());
        CommandManager.get().registerCommand(new HelpCommand());
        CommandManager.get().registerCommand(new CreatePanelCommand());
        CommandManager.get().registerCommand(new PanelListCommand());
        CommandManager.get().registerCommand(new DeletePanelCommand());
        CommandManager.get().registerCommand(new EditPanelCommand());
        CommandManager.get().registerCommand(new CloseCommand());
        CommandManager.get().registerCommand(new ReopenCommand());
        CommandManager.get().registerCommand(new LevelCommand());
        CommandManager.get().registerCommand(new LeaderboardCommand());
        CommandManager.get().registerCommand(new SuggestCommand());
        CommandManager.get().registerCommand(new SuggestionNoteCommand());
        CommandManager.get().registerCommand(new ApproveSuggestionCommand());
        CommandManager.get().registerCommand(new DenySuggestionCommand());
    }

    public String getCommandPrefix() {
        return core.getCommandPrefix();
    }

    public void registerCommand(Command cmd) {
        if (getCommandHashMap().get(cmd.getCmd()) != null) return;
        cmds.put(cmd.getCmd().toLowerCase(), cmd);
        commands.add(cmd);
        commandswithoutaliases.add(cmd);
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

    public List<Command> getCommandsWithoutAliases() {
        return commandswithoutaliases;
    }


}
