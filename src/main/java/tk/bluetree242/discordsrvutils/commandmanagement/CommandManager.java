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
