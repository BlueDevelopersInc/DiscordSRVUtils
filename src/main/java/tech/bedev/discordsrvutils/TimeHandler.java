package tech.bedev.discordsrvutils;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import tech.bedev.discordsrvutils.managers.TimerManager;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

public class TimeHandler extends TimerTask
{
	private final DiscordSRVUtils core;

	public TimeHandler(DiscordSRVUtils core)
	{
		this.core = core;
	}

	public TimerManager getTimerManager()
	{
		return new TimerManager();
	}

	@Override
	public void run()
	{
		if(DiscordSRVUtils.BotSettingsconfig.isBungee()) return;
		if(!DiscordSRVUtils.isReady) return;
		for(Map.Entry<Long, Long> longLongEntry : core.tempmute.entrySet())
		{
			Long userID = longLongEntry.getKey();
			Long expiration = longLongEntry.getValue();
			if(expiration <= getTimerManager().getCurrentTime())
			{
				Member toUnmute = DiscordSRV.getPlugin().getMainGuild().getMemberById(userID);
				if(toUnmute != null)
				{
					if(toUnmute.getRoles().contains(DiscordSRV.getPlugin().getMainGuild().getRoleById(DiscordSRVUtils.Moderationconfig.getMutedRole())))
					{
						DiscordSRV.getPlugin().getMainGuild().removeRoleFromMember(toUnmute, DiscordSRV.getPlugin().getMainGuild().getRoleById(DiscordSRVUtils.Moderationconfig.getMutedRole())).queue();
					}
				}
				core.tempmute.remove(userID);
			}

		}

	}
}
