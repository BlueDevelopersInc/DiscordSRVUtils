package tech.bedev.discordsrvutils.managers;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import tech.bedev.discordsrvutils.DiscordSRVUtils;

import java.util.UUID;

public class ConfOptionsManager
{
	private final DiscordSRVUtils core;

	public ConfOptionsManager(DiscordSRVUtils core)
	{
		this.core = core;
	}

	public static JDA getJda()
	{
		return DiscordSRV.getPlugin().getJda();
	}

	public String getConfigWithPapi(UUID uuid, String msg)
	{
		if(DiscordSRVUtils.placeholderAPI)
		{
			if(uuid != null)
			{
				return PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(uuid), msg);
			}
			else
			{
				return PlaceholderAPI.setPlaceholders(null, msg);
			}
		}
		else
		{
			return msg;
		}
	}

	public String convertToColourCode(String msg)
	{
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
}
