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
import tech.bedev.discordsrvutils.utils.PlayerUtil;

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
        String channel = DiscordSRVUtils.BotSettingsconfig.chat_channel();
        Bukkit.getScheduler().runTask(core, () -> {
            String userId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(Bukkit.getOfflinePlayer(event.getPunishment().getName()).getUniqueId());


            PunishmentType type = event.getPunishment().getType();
            switch (type) {
                case BAN: {
                    if (core.getConfig().getBoolean("advancedban_punishments_to_discord")) {
                        if (!(userId == null)) {
                            if (!(DiscordSRV.getPlugin().getMainGuild().getMemberById(userId) == null)) {
                                DiscordSRV.getPlugin().getMainGuild().ban(userId, 0, "DiscordSRVUtils banned by advancedban").queue();
                            }
                        }
                    }
                    if (core.getConfig().getBoolean("advancedban_ban_message_to_discord")) {
                        DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", core.getConfig().getStringList("advancedban_ban_message"))
                                .replace("[Player]", event.getPunishment().getName())
                                .replace("[Operator]", event.getPunishment().getOperator())
                                .replace("[Reason]", event.getPunishment().getReason())
                        ).queue();


                    }
                    break;
                }
                case MUTE: {
                    if (core.getConfig().getBoolean("advancedban_punishments_to_discord")) {
                        if (userId != null) {
                            if (!(DiscordSRV.getPlugin().getMainGuild().getRoleById(core.getConfig().getLong("muted_role")) == null)) {
                                DiscordSRV.getPlugin().getMainGuild().addRoleToMember(userId, DiscordSRV.getPlugin().getMainGuild().getRoleById(core.getConfig().getLong("muted_role"))).queue();
                            } else {
                                if (core.getConfig().getLong("muted_role") == 000000000000000000) {
                                    PlayerUtil.sendToAuthorizedPlayers("&CError: &eCould not give muted role to muted player because role is in it's default stats (000000000000000000)");
                                } else {
                                    PlayerUtil.sendToAuthorizedPlayers("&cError: &eMuted role id not found on the main guild.");
                                }
                            }
                        }
                    }
                    if (core.getConfig().getBoolean("advancedban_mute_message_to_discord")) {
                        DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", core.getConfig().getStringList("advancedban_mute_message"))
                                .replace("[Player]", event.getPunishment().getName())
                                .replace("[Operator]", event.getPunishment().getOperator())
                                .replace("[Reason]", event.getPunishment().getReason())
                        ).queue();
                    }
                    break;
                }
                case KICK: {
                    break;
                }
                case TEMP_MUTE: {
                    if (core.getConfig().getBoolean("advancedban_punishments_to_discord")) {
                        if (!(userId == null)) {
                            Role mutedRole = DiscordSRV.getPlugin().getMainGuild().getRoleById(core.getConfig().getLong("muted_role"));
                            if (mutedRole != null) {
                                DiscordSRV.getPlugin().getMainGuild().addRoleToMember(userId, mutedRole).queue();
                            } else {
                                if (core.getConfig().getLong("muted_role") == 000000000000000000) {
                                    PlayerUtil.sendToAuthorizedPlayers("&cError: &eCould not give muted role to muted player because role is in its default state (000000000000000000)");
                                } else {
                                    PlayerUtil.sendToAuthorizedPlayers("&cError: &eMuted role id not found on the main guild.");
                                }
                            }
                        }
                    }
                    if (core.getConfig().getBoolean("advancedban_temp_mute_message_to_discord")) {
                        DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", core.getConfig().getStringList("advancedban_temp_mute_message"))
                                .replace("[Player]", event.getPunishment().getName())
                                .replace("[Operator]", event.getPunishment().getOperator())
                                .replace("[Reason]", event.getPunishment().getReason())
                                .replace("[Duration]", event.getPunishment().getDuration(true))
                        ).queue();
                    }
                    break;
                }
                case TEMP_BAN: {
                    if (core.getConfig().getBoolean("advancedban_punishments_to_discord")) {
                        if (userId != null) {
                            if (!(DiscordSRV.getPlugin().getMainGuild().getMemberById(userId) == null)) {
                                DiscordSRV.getPlugin().getMainGuild().ban(userId, 0, "DiscordSRVUtils banned by advancedban").queue();
                            }
                        }
                    }
                    if (core.getConfig().getBoolean("advancedban_temp_ban_message_to_discord")) {
                        DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", core.getConfig().getStringList("advancedban_temp_ban_message"))
                                .replace("[Player]", event.getPunishment().getName())
                                .replace("[Operator]", event.getPunishment().getOperator())
                                .replace("[Reason]", event.getPunishment().getReason())
                                .replace("[Duration]", event.getPunishment().getDuration(true))
                        ).queue();

                    }
                    break;
                }
                case IP_BAN: {
                    if (core.getConfig().getBoolean("advancedban_punishments_to_discord")) {
                        if (userId != null) {
                            if (!(DiscordSRV.getPlugin().getMainGuild().getMemberById(userId) == null)) {
                                DiscordSRV.getPlugin().getMainGuild().ban(userId, 0, "DiscordSRVUtils banned by advancedban").queue();
                            }
                        }
                    }
                    if (core.getConfig().getBoolean("advancedban_ip_ban_message_to_discord")) {
                        DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", core.getConfig().getStringList("advancedban_ip_ban_message"))
                                .replace("[Player]", event.getPunishment().getName())
                                .replace("[Operator]", event.getPunishment().getOperator())
                                .replace("[Reason]", event.getPunishment().getReason())
                        ).queue();

                    }
                    break;
                }
                case TEMP_IP_BAN: {
                    if (core.getConfig().getBoolean("advancedban_punishments_to_discord")) {
                        if (!(userId == null)) {
                            if (!(DiscordSRV.getPlugin().getMainGuild().getMemberById(userId) == null)) {
                                DiscordSRV.getPlugin().getMainGuild().ban(userId, 0, "DiscordSRVUtils banned by advancedban").queue();
                            }
                        }
                    }
                    if (core.getConfig().getBoolean("advancedban_temp_ip_ban_message_to_discord")) {
                        DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", core.getConfig().getStringList("advancedban_temp_ip_ban_message"))
                                .replace("[Player]", event.getPunishment().getName())
                                .replace("[Operator]", event.getPunishment().getOperator())
                                .replace("[Reason]", event.getPunishment().getReason())
                                .replace("[Duration]", event.getPunishment().getDuration(true))
                        ).queue();

                    }
                    break;
                }


            }
        });

    }

    @EventHandler
    public void onPlayerUnpunish(RevokePunishmentEvent event) {
        String channel = DiscordSRVUtils.BotSettingsconfig.chat_channel();
        Bukkit.getScheduler().runTask(core, () -> {
            String userId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(Bukkit.getOfflinePlayer(event.getPunishment().getName()).getUniqueId());
            PunishmentType type = event.getPunishment().getType();
            Role muted = DiscordSRV.getPlugin().getMainGuild().getRoleById(core.getConfig().getLong("muted_role"));

            switch (type) {
                case BAN: {
                    if (core.getConfig().getBoolean("advancedban_unpunishments_to_discord")) {
                        if (userId != null) {
                            DiscordSRV.getPlugin().getMainGuild().unban(userId).queue();
                        }
                    }
                    if (core.getConfig().getBoolean("advancedban_unban_to_discord")) {
                        DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", core.getConfig().getStringList("advancedban_unban_message"))
                                .replace("[Player]", event.getPunishment().getName())
                                .replace("[Operator]", event.getPunishment().getOperator())
                        ).queue();

                    }
                    break;
                }
                case TEMP_BAN: {
                    if (TimeManager.getTime() >= event.getPunishment().getEnd()) {
                        if (core.getConfig().getBoolean("advancedban_unpunishments_to_discord")) {
                            if (userId != null) {
                                DiscordSRV.getPlugin().getMainGuild().unban(userId).queue();
                            }
                        }

                    } else {
                        if (core.getConfig().getBoolean("advancedban_unpunishments_to_discord")) {
                            if (userId != null) {
                                DiscordSRV.getPlugin().getMainGuild().unban(userId).queue();
                            }
                        }
                        if (core.getConfig().getBoolean("advancedban_untempban_to_discord")) {
                            DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", core.getConfig().getStringList("advancedban_untempban_message"))
                                    .replace("[Player]", event.getPunishment().getName())
                                    .replace("[Operator]", event.getPunishment().getOperator())
                                    .replace("[Reason]", event.getPunishment().getReason())
                            ).queue();

                        }

                    }
                    break;
                }

                case IP_BAN: {
                    if (core.getConfig().getBoolean("advancedban_unpunishments_to_discord")) {
                        if (userId != null) {
                            DiscordSRV.getPlugin().getMainGuild().unban(userId).queue();
                        }
                    }
                    if (core.getConfig().getBoolean("advancedban_unipban_to_discord")) {
                        DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", core.getConfig().getStringList("advancedban_unipban_message"))
                                .replace("[Player]", event.getPunishment().getName())
                                .replace("[Operator]", event.getPunishment().getOperator())
                        ).queue();

                    }
                    break;
                }

                case TEMP_IP_BAN: {
                    if (TimeManager.getTime() >= event.getPunishment().getEnd()) {
                        if (core.getConfig().getBoolean("advancedban_unpunishments_to_discord")) {
                            if (userId != null) {
                                DiscordSRV.getPlugin().getMainGuild().unban(userId).queue();
                            }
                        }


                    } else {
                        if (core.getConfig().getBoolean("advancedban_unpunishments_to_discord")) {
                            if (userId != null) {
                                DiscordSRV.getPlugin().getMainGuild().unban(userId).queue();
                            }
                        }

                        if (core.getConfig().getBoolean("advancedban_untempipban_to_discord")) {
                            DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", core.getConfig().getStringList("advancedban_untempipban_message"))
                                    .replace("[Player]", event.getPunishment().getName())

                                    .replace("[Operator]", event.getPunishment().getOperator())
                            ).queue();

                        }
                    }
                    break;
                }
                case MUTE:
                    if (core.getConfig().getBoolean("advancedban_unpunishments_to_discord")) {
                        if (DiscordSRV.getPlugin().getMainGuild().getRoleById(core.getConfig().getLong("muted_role")) == null) {

                            PlayerUtil.sendToAuthorizedPlayers("&cError: &eCould not remove role from unmuted player because muted_role is not found on the guild.");
                        } else {
                            if (!(userId == null)) {
                                DiscordSRV.getPlugin().getMainGuild().removeRoleFromMember(userId, muted).queue();
                            }
                        }
                    }
                    if (core.getConfig().getBoolean("advancedban_unmute_message_to_discord")) {
                        DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", core.getConfig().getStringList("advancedban_unmute_message"))
                                .replace("[Player]", event.getPunishment().getName())

                                .replace("[Operator]", event.getPunishment().getOperator())
                        ).queue();
                    }
                    break;

                case TEMP_MUTE:
                    if (TimeManager.getTime() >= event.getPunishment().getEnd()) {
                        if (core.getConfig().getBoolean("advancedban_unpunishments_to_discord")) {
                            if (DiscordSRV.getPlugin().getMainGuild().getRoleById(core.getConfig().getLong("muted_role")) == null) {

                                PlayerUtil.sendToAuthorizedPlayers("&cError: &eCould not remove role from unmuted player because muted_role is not found on the guild.");
                            } else {
                                if (!(userId == null)) {
                                    DiscordSRV.getPlugin().getMainGuild().removeRoleFromMember(userId, DiscordSRV.getPlugin().getMainGuild().getRoleById(core.getConfig().getLong("muted_role"))).queue();
                                }
                            }
                        }

                    } else {
                        if (core.getConfig().getBoolean("advancedban_unpunishments_to_discord")) {
                            if (DiscordSRV.getPlugin().getMainGuild().getRoleById(core.getConfig().getLong("muted_role")) == null) {

                                PlayerUtil.sendToAuthorizedPlayers("&cError: &eCould not remove role from unmuted player because muted_role is not found on the guild.");
                            } else {
                                if (!(userId == null)) {
                                    DiscordSRV.getPlugin().getMainGuild().removeRoleFromMember(userId, DiscordSRV.getPlugin().getMainGuild().getRoleById(core.getConfig().getLong("muted_role"))).queue();
                                }
                            }
                        }
                        if (core.getConfig().getBoolean("advancedban_untempmute_message_to_discord")) {
                            DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel).sendMessage(String.join("\n", core.getConfig().getStringList("advancedban_untempmute_message"))
                                    .replace("[Player]", event.getPunishment().getName())

                                    .replace("[Operator]", event.getPunishment().getOperator())
                            ).queue();
                        }

                    }
                    break;
            }
        });

    }
}


