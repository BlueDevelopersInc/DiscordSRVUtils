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
