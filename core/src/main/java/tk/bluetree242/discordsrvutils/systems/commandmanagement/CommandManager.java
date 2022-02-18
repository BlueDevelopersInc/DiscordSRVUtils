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

package tk.bluetree242.discordsrvutils.systems.commandmanagement;


import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.CommandListUpdateAction;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.commands.discord.HelpCommand;
import tk.bluetree242.discordsrvutils.commands.discord.admin.TestMessageCommand;
import tk.bluetree242.discordsrvutils.commands.discord.leveling.LeaderboardCommand;
import tk.bluetree242.discordsrvutils.commands.discord.leveling.LevelCommand;
import tk.bluetree242.discordsrvutils.commands.discord.other.LinkAccountCommand;
import tk.bluetree242.discordsrvutils.commands.discord.status.StatusCommand;
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
        registerCommand(new TestMessageCommand());
        registerCommand(new HelpCommand());
        registerCommand(new CreatePanelCommand());
        registerCommand(new PanelListCommand());
        registerCommand(new DeletePanelCommand());
        registerCommand(new EditPanelCommand());
        registerCommand(new CloseCommand());
        registerCommand(new ReopenCommand());
        registerCommand(new LevelCommand());
        registerCommand(new LeaderboardCommand());
        registerCommand(new SuggestCommand());
        registerCommand(new SuggestionNoteCommand());
        registerCommand(new ApproveSuggestionCommand());
        registerCommand(new DenySuggestionCommand());
        registerCommand(new StatusCommand());
        registerCommand(new LinkAccountCommand());
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

    public List<Command> getDisabledCommands(boolean onlyConfig) {
        List<Command> result = new ArrayList<>();
        if (onlyConfig)
            for (String command : core.getMainConfig().disabled_commands()) {
                result.add(getCommandByName(command));
            }
        else for (Command cmd : commandswithoutaliases) {
            if (!cmd.isEnabled()) result.add(cmd);
        }
        return result;
    }

    public Command getCommandByName(String name) {
        for (Command command : commands) {
            if (command.getCmd().equalsIgnoreCase(name)) return command;
        }
        return null;
    }

    public List<Command> getCommandsWithoutAliases() {
        return commandswithoutaliases;
    }

    public void addSlashCommands() {
        CommandListUpdateAction commands = core.getGuild().updateCommands();
        for (Command command : this.commands) {
            if (!command.isEnabled()) continue;
            addCmd(command.getCmd(), command, commands);
            for (String alias : command.getAliases()) {
                addCmd(alias, command, commands);
            }
        }
        commands.queue();
    }

    private void addCmd(String alias, Command cmd, CommandListUpdateAction action) {
        action.addCommands(new CommandData(alias, cmd.getDescription()).addOptions(cmd.getOptions()));
    }


}
