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

import java.util.*;

public class CommandCategory {

    public static final CommandCategory TICKETS = new CommandCategory("Tickets", "\uD83C\uDFAB");
    public static final CommandCategory ADMIN = new CommandCategory("Admin", "⚒️");
    public static final CommandCategory TICKETS_ADMIN = new CommandCategory("Tickets Admin", "\uD83C\uDF9F️");
    public static final CommandCategory LEVELING = new CommandCategory("Leveling", "\uD83C\uDFC5");
    public static final CommandCategory SUGGESTIONS = new CommandCategory("Suggestions", "\uD83D\uDCA1");
    public static final CommandCategory SUGGESTIONS_ADMIN = new CommandCategory("Suggestions Admin", "\uD83D\uDCA1");
    private static Set<CommandCategory> externals = new HashSet<>();
    private List<Command> commands = new ArrayList<>();
    private String name;
    private String prefix;


    private CommandCategory(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }

    public static CommandCategory[] values() {
        ArrayList<CommandCategory> list = new ArrayList(Arrays.asList(new CommandCategory[]{TICKETS, TICKETS_ADMIN, ADMIN, LEVELING, SUGGESTIONS, SUGGESTIONS_ADMIN}));
        for (CommandCategory external : externals) {
            list.add(external);
        }
        return list.toArray(new CommandCategory[0]);
    }

    public static void registerCategory(String name, String prefix) {
        for (CommandCategory external : values()) {
            if (external.getName().equalsIgnoreCase(name)) return;
        }
        externals.add(new CommandCategory(name, prefix));
    }

    public static CommandCategory ofName(String name) {
        for (CommandCategory value : values()) {
            if (value.name.equalsIgnoreCase(name)) return value;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    protected void addCommand(Command cmd) {
        commands.add(cmd);
    }

    public List<Command> getCommands() {
        return commands;
    }

    @Override
    public String toString() {
        return prefix + " " + name;
    }
}
