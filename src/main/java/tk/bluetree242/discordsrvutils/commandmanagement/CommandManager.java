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
    }

    public static CommandManager get() {
        return main;
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
