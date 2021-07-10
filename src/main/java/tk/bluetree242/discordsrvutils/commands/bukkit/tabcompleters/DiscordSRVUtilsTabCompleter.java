package tk.bluetree242.discordsrvutils.commands.bukkit.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DiscordSRVUtilsTabCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> values = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("discordsrvutils.reload"))
            values.add("reload");
        }
        return values.isEmpty() ? null : values;
    }
}
