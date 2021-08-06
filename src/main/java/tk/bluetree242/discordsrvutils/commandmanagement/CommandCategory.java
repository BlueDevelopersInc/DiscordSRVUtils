package tk.bluetree242.discordsrvutils.commandmanagement;

import java.util.ArrayList;
import java.util.List;

public class CommandCategory {

    public static final CommandCategory TICKETS = new CommandCategory("Tickets", "\uD83C\uDFAB");
    public static final CommandCategory ADMIN = new CommandCategory("Admin", "⚒️");
    public static final CommandCategory TICKETS_ADMIN = new CommandCategory("Tickets Admin", "\uD83C\uDF9F️");

    public static CommandCategory[] values() {
        return new CommandCategory[]{TICKETS, TICKETS, ADMIN};
    }

    private List<Command> commands = new ArrayList<>();

    private String name;
    private String prefix;
    private CommandCategory(String name, String prefix) {
         this.name = name;
         this.prefix = prefix;
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
