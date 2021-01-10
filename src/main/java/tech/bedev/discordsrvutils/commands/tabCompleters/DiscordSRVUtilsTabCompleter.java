package tech.bedev.discordsrvutils.commands.tabCompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class DiscordSRVUtilsTabCompleter implements TabCompleter {

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

            List<String> arg0 = new ArrayList<>();
            if (sender.hasPermission("discordsrvutils.reload")) {
                arg0.add("reload");
            }
            if (sender.hasPermission("discordsrvutils.updatecheck")) {
                arg0.add("updatecheck");
            }
            if (sender.hasPermission("discordsrvutils.clearmemory")) {
                arg0.add("clearmemory");
                arg0.add("confirm");
            }

            List<String> result = new ArrayList<>();
            for (String a : arg0) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(a);
                }
            }
            return result;

    }
}
