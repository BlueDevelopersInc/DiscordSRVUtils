package tech.bedev.discordsrvutils.commands.tabcompletion;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class DiscordSRVUtilsTabCompleter implements TabCompleter
{

	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
	{
		List<String> result = new ArrayList<>();
		if(sender.hasPermission("discordsrvutils.reload"))
		{
			result.add("reload");
		}
		if(sender.hasPermission("discordsrvutils.updatecheck"))
		{
			result.add("updatecheck");
		}
		return result;
	}
}
