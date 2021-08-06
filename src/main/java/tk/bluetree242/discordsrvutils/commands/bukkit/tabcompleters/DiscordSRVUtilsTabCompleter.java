package tk.bluetree242.discordsrvutils.commands.bukkit.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiscordSRVUtilsTabCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> values = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("discordsrvutils.reload"))
            values.add("reload");
            if (sender.hasPermission("discordsrvutils.debug"))
                values.add("debug");
        }

        List<String> result = new ArrayList<>();
        for (String a : values) {
            if (a.toLowerCase().startsWith(args[args.length -1].toLowerCase()))
                result.add(a);
        }
        return result.isEmpty() ? Collections.emptyList() : result;
    }
}
