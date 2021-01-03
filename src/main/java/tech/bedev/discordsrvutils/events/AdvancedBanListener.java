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
import tech.bedev.discordsrvutils.Configs.BansIntegrationConfig;
import tech.bedev.discordsrvutils.DiscordSRVUtils;
import tech.bedev.discordsrvutils.utils.PlayerUtil;

import java.util.UUID;

public class AdvancedBanListener implements Listener {
    private final DiscordSRVUtils core;

    public static JDA getJda() {
        return DiscordSRV.getPlugin().getJda();
    }

    public AdvancedBanListener(DiscordSRVUtils core) {
        this.core = core;
    }

    @EventHandler
    public void onPlayerPunished(PunishmentEvent event) {
        String userId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(Bukkit.getOfflinePlayer(event.getPunishment().getName()).getUniqueId());
        String channel = DiscordSRVUtils.BotSettingsconfig.chat_channel();
        BansIntegrationConfig conf = DiscordSRVUtils.BansIntegrationconfig;
        Bukkit.getScheduler().runTask(core, () -> {
            PunishmentType type = event.getPunishment().getType();
           switch (type) {
               case BAN:
                   if (conf.isSyncPunishmentsWithDiscord()) {
                       if (userId != null) {
                           DiscordSRV.getPlugin().getMainGuild().ban(userId, 0, "DiscordSRVUtils Ban plugins Sync").queue();
                       }
                   } if (conf.isSendPunishmentmsgesToDiscord()) {
                       DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.bannedMessage())
                               .replace("[Player]", event.getPunishment().getName())
                               .replace("[Operator]", event.getPunishment().getOperator())
                               .replace("[Reason]", event.getPunishment().getReason())
                       ).queue();
               }
                   break;
               case TEMP_BAN:
                   if (conf.isSyncPunishmentsWithDiscord()) {
                       if (userId != null) {
                           DiscordSRV.getPlugin().getMainGuild().ban(userId, 0, "DiscordSRVUtils Ban plugins Sync").queue();
                       }
                   } if (conf.isSendPunishmentmsgesToDiscord()) {
                   DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.tempBannedMessage())
                           .replace("[Player]", event.getPunishment().getName())
                           .replace("[Operator]", event.getPunishment().getOperator())
                           .replace("[Reason]", event.getPunishment().getReason())
                           .replace("[Duration]", event.getPunishment().getDuration(true))
                   ).queue();
               }
                   break;
               case IP_BAN:
                   if (conf.isSyncPunishmentsWithDiscord()) {
                       if (userId != null) {
                           DiscordSRV.getPlugin().getMainGuild().ban(userId, 0, "DiscordSRVUtils Ban plugins Sync").queue();
                       }
                   } if (conf.isSendPunishmentmsgesToDiscord()) {
                   DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.IPBannedMessage())
                           .replace("[Player]", event.getPunishment().getName())
                           .replace("[Operator]", event.getPunishment().getOperator())
                           .replace("[Reason]", event.getPunishment().getReason())
                   ).queue();
               }
                   break;
               case TEMP_IP_BAN:
                   if (conf.isSyncPunishmentsWithDiscord()) {
                       if (userId != null) {
                           DiscordSRV.getPlugin().getMainGuild().ban(userId, 0, "DiscordSRVUtils Ban plugins Sync").queue();
                       }
                   } if (conf.isSendPunishmentmsgesToDiscord()) {
                   DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.TempIPBannedMessage())
                           .replace("[Player]", event.getPunishment().getName())
                           .replace("[Operator]", event.getPunishment().getOperator())
                           .replace("[Reason]", event.getPunishment().getReason())
                           .replace("[Duration]", event.getPunishment().getDuration(true))
                   ).queue();
               }
                   break;
               case MUTE:
                   if (conf.isSyncPunishmentsWithDiscord()) {
                       if (userId != null) {
                           if (DiscordSRV.getPlugin().getMainGuild().getRoleById(DiscordSRVUtils.Moderationconfig.MutedRole()) != null) {
                               DiscordSRV.getPlugin().getMainGuild().addRoleToMember(userId, DiscordSRV.getPlugin().getMainGuild().getRoleById(DiscordSRVUtils.Moderationconfig.MutedRole())).queue();
                           }
                       }
                   } if (conf.isSendPunishmentmsgesToDiscord()) {
                   DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.MutedMessage())
                           .replace("[Player]", event.getPunishment().getName())
                           .replace("[Operator]", event.getPunishment().getOperator())
                           .replace("[Reason]", event.getPunishment().getReason())
                   ).queue();
               }
                   break;
               case TEMP_MUTE:
                   if (conf.isSyncPunishmentsWithDiscord()) {
                       if (userId != null) {
                           if (DiscordSRV.getPlugin().getMainGuild().getRoleById(DiscordSRVUtils.Moderationconfig.MutedRole()) != null) {
                               DiscordSRV.getPlugin().getMainGuild().addRoleToMember(userId, DiscordSRV.getPlugin().getMainGuild().getRoleById(DiscordSRVUtils.Moderationconfig.MutedRole())).queue();
                           }
                       }
                   } if (conf.isSendPunishmentmsgesToDiscord()) {
                   DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.TempMutedMessage())
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
    public void onPlayerUnpunish(RevokePunishmentEvent event) {
        String channel = DiscordSRVUtils.BotSettingsconfig.chat_channel();
        String userId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(Bukkit.getOfflinePlayer(event.getPunishment().getName()).getUniqueId());
        PunishmentType type = event.getPunishment().getType();
        Role muted = DiscordSRV.getPlugin().getMainGuild().getRoleById(core.getConfig().getLong("muted_role"));
        BansIntegrationConfig conf = DiscordSRVUtils.BansIntegrationconfig;
        Bukkit.getScheduler().runTask(core, () -> {
            switch (type) {
                case BAN:
                    if (conf.isSyncUnpunishmentsWithDiscord()) {
                        if (userId != null) {
                            DiscordSRV.getPlugin().getMainGuild().unban(userId).queue();
                        }
                    } if (conf.isSyncUnpunishmentsmsgWithDiscord()) {
                    DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.unbannedMessage())
                            .replace("[Player]", event.getPunishment().getName())
                            .replace("[Operator]", event.getPunishment().getOperator())
                            .replace("[Reason]", event.getPunishment().getReason())
                    ).queue();
                }
                    break;
                case TEMP_BAN:
                    if (TimeManager.getTime() >= event.getPunishment().getEnd()) {
                        if (conf.isSyncUnpunishmentsWithDiscord()) {
                            if (userId != null) {
                                DiscordSRV.getPlugin().getMainGuild().unban(userId).queue();
                            }
                        }
                    } else {
                        if (conf.isSyncUnpunishmentsWithDiscord()) {
                            if (userId != null) {
                                DiscordSRV.getPlugin().getMainGuild().unban(userId).queue();
                            }
                        }
                        if (conf.isSyncUnpunishmentsmsgWithDiscord()) {
                            DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.unbannedMessage())
                                    .replace("[Player]", event.getPunishment().getName())
                                    .replace("[Operator]", event.getPunishment().getOperator())
                                    .replace("[Reason]", event.getPunishment().getReason())
                            ).queue();
                        }

                    }
                case IP_BAN:
                    if (conf.isSyncUnpunishmentsWithDiscord()) {
                        if (userId != null) {
                            DiscordSRV.getPlugin().getMainGuild().unban(userId).queue();
                        }
                    } if (conf.isSyncUnpunishmentsmsgWithDiscord()) {
                    DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.unipbannedMessage())
                            .replace("[Player]", event.getPunishment().getName())
                            .replace("[Operator]", event.getPunishment().getOperator())
                            .replace("[Reason]", event.getPunishment().getReason())
                    ).queue();
                }
                    break;
                case TEMP_IP_BAN:
                    if (TimeManager.getTime() >= event.getPunishment().getEnd()) {
                        if (conf.isSyncUnpunishmentsWithDiscord()) {
                            if (userId != null) {
                                DiscordSRV.getPlugin().getMainGuild().unban(userId).queue();
                            }
                        }
                    } else {
                        if (conf.isSyncUnpunishmentsWithDiscord()) {
                            if (userId != null) {
                                if (muted != null) {
                                    DiscordSRV.getPlugin().getMainGuild().removeRoleFromMember(userId, muted);

                                }
                            }
                        }
                        if (conf.isSyncUnpunishmentsmsgWithDiscord()) {
                            DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.unipbannedMessage())
                                    .replace("[Player]", event.getPunishment().getName())
                                    .replace("[Operator]", event.getPunishment().getOperator())
                                    .replace("[Reason]", event.getPunishment().getReason())
                            ).queue();
                        }

                    }
                case MUTE:
                    if (conf.isSyncUnpunishmentsWithDiscord()) {
                        if (userId != null) {
                            if (muted != null) {
                                DiscordSRV.getPlugin().getMainGuild().removeRoleFromMember(userId, muted);

                            }
                        }
                    } if (conf.isSyncUnpunishmentsmsgWithDiscord()) {
                    DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.unmuteMessage())
                            .replace("[Player]", event.getPunishment().getName())
                            .replace("[Operator]", event.getPunishment().getOperator())
                            .replace("[Reason]", event.getPunishment().getReason())
                    ).queue();
                }
                    break;
                case TEMP_MUTE:
                    if (TimeManager.getTime() >= event.getPunishment().getEnd()) {
                        if (conf.isSyncUnpunishmentsWithDiscord()) {
                            if (userId != null) {
                                if (muted != null) {
                                    DiscordSRV.getPlugin().getMainGuild().removeRoleFromMember(userId, muted);

                                }
                            }
                        }
                    } else {
                        if (conf.isSyncUnpunishmentsWithDiscord()) {
                            if (userId != null) {
                                if (muted != null) {
                                    DiscordSRV.getPlugin().getMainGuild().removeRoleFromMember(userId, muted);

                                }
                            }
                        }
                        if (conf.isSyncUnpunishmentsmsgWithDiscord()) {
                            DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", conf.unmuteMessage())
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


