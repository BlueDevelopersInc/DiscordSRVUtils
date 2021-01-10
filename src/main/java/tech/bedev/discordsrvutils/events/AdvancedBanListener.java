package tech.bedev.discordsrvutils.events;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import me.leoko.advancedban.bukkit.event.PunishmentEvent;
import me.leoko.advancedban.bukkit.event.RevokePunishmentEvent;
import me.leoko.advancedban.manager.TimeManager;
import me.leoko.advancedban.utils.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tech.bedev.discordsrvutils.DiscordSRVUtils;
import tech.bedev.discordsrvutils.configs.BansIntegrationConfig;

public class AdvancedBanListener implements Listener
{
	private final DiscordSRVUtils core;

	public AdvancedBanListener(DiscordSRVUtils core)
	{
		this.core = core;
	}

	public static JDA getJda()
	{
		return DiscordSRV.getPlugin().getJda();
	}

	@EventHandler
	public void onPlayerPunished(PunishmentEvent event)
	{
		String userId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(Bukkit.getOfflinePlayer(event.getPunishment().getName()).getUniqueId());
		String channel = DiscordSRVUtils.BotSettingsconfig.getChatChannel();
		BansIntegrationConfig conf = DiscordSRVUtils.BansIntegrationconfig;
		Bukkit.getScheduler().runTask(core, () ->
		{
			PunishmentType type = event.getPunishment().getType();
			switch(type)
			{
				case BAN:
					if(conf.isSyncPunishmentsWithDiscord())
					{
						if(userId != null)
						{
							DiscordSRV.getPlugin().getMainGuild().ban(userId, 0, "DiscordSRVUtils Ban plugins Sync").queue();
						}
					}
					if(conf.isSendPunishmentmsgesToDiscord())
					{
						DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.getBannedMessage())
								.replace("[Player]", event.getPunishment().getName())
								.replace("[Operator]", event.getPunishment().getOperator())
								.replace("[Reason]", event.getPunishment().getReason())
						).queue();
					}
					break;
				case TEMP_BAN:
					if(conf.isSyncPunishmentsWithDiscord())
					{
						if(userId != null)
						{
							DiscordSRV.getPlugin().getMainGuild().ban(userId, 0, "DiscordSRVUtils Ban plugins Sync").queue();
						}
					}
					if(conf.isSendPunishmentmsgesToDiscord())
					{
						DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.getTempBannedMessage())
								.replace("[Player]", event.getPunishment().getName())
								.replace("[Operator]", event.getPunishment().getOperator())
								.replace("[Reason]", event.getPunishment().getReason())
								.replace("[Duration]", event.getPunishment().getDuration(true))
						).queue();
					}
					break;
				case IP_BAN:
					if(conf.isSyncPunishmentsWithDiscord())
					{
						if(userId != null)
						{
							DiscordSRV.getPlugin().getMainGuild().ban(userId, 0, "DiscordSRVUtils Ban plugins Sync").queue();
						}
					}
					if(conf.isSendPunishmentmsgesToDiscord())
					{
						DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.getIPBannedMessage())
								.replace("[Player]", event.getPunishment().getName())
								.replace("[Operator]", event.getPunishment().getOperator())
								.replace("[Reason]", event.getPunishment().getReason())
						).queue();
					}
					break;
				case TEMP_IP_BAN:
					if(conf.isSyncPunishmentsWithDiscord())
					{
						if(userId != null)
						{
							DiscordSRV.getPlugin().getMainGuild().ban(userId, 0, "DiscordSRVUtils Ban plugins Sync").queue();
						}
					}
					if(conf.isSendPunishmentmsgesToDiscord())
					{
						DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.getTempIPBannedMessage())
								.replace("[Player]", event.getPunishment().getName())
								.replace("[Operator]", event.getPunishment().getOperator())
								.replace("[Reason]", event.getPunishment().getReason())
								.replace("[Duration]", event.getPunishment().getDuration(true))
						).queue();
					}
					break;
				case MUTE:
					if(conf.isSyncPunishmentsWithDiscord())
					{
						if(userId != null)
						{
							if(DiscordSRV.getPlugin().getMainGuild().getRoleById(DiscordSRVUtils.Moderationconfig.getMutedRole()) != null)
							{
								DiscordSRV.getPlugin().getMainGuild().addRoleToMember(userId, DiscordSRV.getPlugin().getMainGuild().getRoleById(DiscordSRVUtils.Moderationconfig.getMutedRole())).queue();
							}
						}
					}
					if(conf.isSendPunishmentmsgesToDiscord())
					{
						DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.getMutedMessage())
								.replace("[Player]", event.getPunishment().getName())
								.replace("[Operator]", event.getPunishment().getOperator())
								.replace("[Reason]", event.getPunishment().getReason())
						).queue();
					}
					break;
				case TEMP_MUTE:
					if(conf.isSyncPunishmentsWithDiscord())
					{
						if(userId != null)
						{
							if(DiscordSRV.getPlugin().getMainGuild().getRoleById(DiscordSRVUtils.Moderationconfig.getMutedRole()) != null)
							{
								DiscordSRV.getPlugin().getMainGuild().addRoleToMember(userId, DiscordSRV.getPlugin().getMainGuild().getRoleById(DiscordSRVUtils.Moderationconfig.getMutedRole())).queue();
							}
						}
					}
					if(conf.isSendPunishmentmsgesToDiscord())
					{
						DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.getTempMutedMessage())
								.replace("[Player]", event.getPunishment().getName())
								.replace("[Operator]", event.getPunishment().getOperator())
								.replace("[Reason]", event.getPunishment().getReason())
								.replace("[Duration]", event.getPunishment().getDuration(true))
						).queue();
					}
					break;


			}
		});

	}

	@EventHandler
	public void onPlayerUnpunish(RevokePunishmentEvent event)
	{
		String channel = DiscordSRVUtils.BotSettingsconfig.getChatChannel();
		String userId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(Bukkit.getOfflinePlayer(event.getPunishment().getName()).getUniqueId());
		PunishmentType type = event.getPunishment().getType();
		Role muted = DiscordSRV.getPlugin().getMainGuild().getRoleById(core.getConfig().getLong("muted_role"));
		BansIntegrationConfig conf = DiscordSRVUtils.BansIntegrationconfig;
		Bukkit.getScheduler().runTask(core, () ->
		{
			switch(type)
			{
				case BAN:
					if(conf.isSyncUnpunishmentsWithDiscord())
					{
						if(userId != null)
						{
							DiscordSRV.getPlugin().getMainGuild().unban(userId).queue();
						}
					}
					if(conf.isSyncUnpunishmentsmsgWithDiscord())
					{
						DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.getUnbannedMessage())
								.replace("[Player]", event.getPunishment().getName())
								.replace("[Operator]", event.getPunishment().getOperator())
								.replace("[Reason]", event.getPunishment().getReason())
						).queue();
					}
					break;
				case TEMP_BAN:
					if(TimeManager.getTime() >= event.getPunishment().getEnd())
					{
						if(conf.isSyncUnpunishmentsWithDiscord())
						{
							if(userId != null)
							{
								DiscordSRV.getPlugin().getMainGuild().unban(userId).queue();
							}
						}
					}
					else
					{
						if(conf.isSyncUnpunishmentsWithDiscord())
						{
							if(userId != null)
							{
								DiscordSRV.getPlugin().getMainGuild().unban(userId).queue();
							}
						}
						if(conf.isSyncUnpunishmentsmsgWithDiscord())
						{
							DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.getUnbannedMessage())
									.replace("[Player]", event.getPunishment().getName())
									.replace("[Operator]", event.getPunishment().getOperator())
									.replace("[Reason]", event.getPunishment().getReason())
							).queue();
						}

					}
				case IP_BAN:
					if(conf.isSyncUnpunishmentsWithDiscord())
					{
						if(userId != null)
						{
							DiscordSRV.getPlugin().getMainGuild().unban(userId).queue();
						}
					}
					if(conf.isSyncUnpunishmentsmsgWithDiscord())
					{
						DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.getIPUnbanMessage())
								.replace("[Player]", event.getPunishment().getName())
								.replace("[Operator]", event.getPunishment().getOperator())
								.replace("[Reason]", event.getPunishment().getReason())
						).queue();
					}
					break;
				case TEMP_IP_BAN:
					if(TimeManager.getTime() >= event.getPunishment().getEnd())
					{
						if(conf.isSyncUnpunishmentsWithDiscord())
						{
							if(userId != null)
							{
								DiscordSRV.getPlugin().getMainGuild().unban(userId).queue();
							}
						}
					}
					else
					{
						if(conf.isSyncUnpunishmentsWithDiscord())
						{
							if(userId != null)
							{
								if(muted != null)
								{
									DiscordSRV.getPlugin().getMainGuild().removeRoleFromMember(userId, muted);

								}
							}
						}
						if(conf.isSyncUnpunishmentsmsgWithDiscord())
						{
							DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.getIPUnbanMessage())
									.replace("[Player]", event.getPunishment().getName())
									.replace("[Operator]", event.getPunishment().getOperator())
									.replace("[Reason]", event.getPunishment().getReason())
							).queue();
						}

					}
				case MUTE:
					if(conf.isSyncUnpunishmentsWithDiscord())
					{
						if(userId != null)
						{
							if(muted != null)
							{
								DiscordSRV.getPlugin().getMainGuild().removeRoleFromMember(userId, muted);

							}
						}
					}
					if(conf.isSyncUnpunishmentsmsgWithDiscord())
					{
						DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.getUnmutedMessage())
								.replace("[Player]", event.getPunishment().getName())
								.replace("[Operator]", event.getPunishment().getOperator())
								.replace("[Reason]", event.getPunishment().getReason())
						).queue();
					}
					break;
				case TEMP_MUTE:
					if(TimeManager.getTime() >= event.getPunishment().getEnd())
					{
						if(conf.isSyncUnpunishmentsWithDiscord())
						{
							if(userId != null)
							{
								if(muted != null)
								{
									DiscordSRV.getPlugin().getMainGuild().removeRoleFromMember(userId, muted);

								}
							}
						}
					}
					else
					{
						if(conf.isSyncUnpunishmentsWithDiscord())
						{
							if(userId != null)
							{
								if(muted != null)
								{
									DiscordSRV.getPlugin().getMainGuild().removeRoleFromMember(userId, muted);

								}
							}
						}
						if(conf.isSyncUnpunishmentsmsgWithDiscord())
						{
							DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.getUnmutedMessage())
									.replace("[Player]", event.getPunishment().getName())
									.replace("[Operator]", event.getPunishment().getOperator())
									.replace("[Reason]", event.getPunishment().getReason())
							).queue();
						}

					}
			}


		});

	}
}


