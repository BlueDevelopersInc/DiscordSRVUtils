package com.bluetree.discordsrvutils.events;

import com.bluetree.discordsrvutils.DiscordSRVUtils;
import com.bluetree.discordsrvutils.utils.PlayerUtil;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberJoinEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.manager.UUIDManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.UUID;

public class JDAEvents extends ListenerAdapter {

    private DiscordSRVUtils core;
    public JDAEvents(DiscordSRVUtils core) {
        this.core = core;
    }


    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        Bukkit.getScheduler().runTask(core, () -> {
            UUID puuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(e.getUser().getId());
            String pname = Bukkit.getOfflinePlayer(puuid).getName();
            if (puuid != null) {
                if (PunishmentManager.get().isBanned(UUIDManager.get().getUUID(pname))) {
                    if (core.getConfig().getBoolean("advancedban_punishments_to_discord")) {
                        e.getGuild().ban(e.getMember().getUser(), 0, "DiscordSRVUtils banned by Advancedban").queue();
                        return;
                    }
                } else if (PunishmentManager.get().isMuted(UUIDManager.get().getUUID(pname))) {
                    if (core.getConfig().getBoolean("advancedban_punishments_to_discord")) {
                        if (e.getGuild().getRoleById(core.getConfig().getString("muted_role")) != null) {
                            e.getGuild().addRoleToMember(e.getMember(), e.getGuild().getRoleById(core.getConfig().getLong("muted_role"))).queue();
                        } else {
                            PlayerUtil.sendToAuthorizedPlayers("&cError: &eCould not give role to muted member because role is not found.");
                        }
                    }
                }

            }
            if (core.getConfig().getLong("welcomer_channel") == 000000000000000000) {
                core.getLogger().info(e.getMember().getUser().getName() + " Joined server" + " " + '"' + e.getGuild().getName() + '"' + ", Could not send message because the welcomer_channel wasn't set in the config");
                PlayerUtil.sendToAuthorizedPlayers("&cError: &e" + e.getMember().getUser().getName() + " Joined server" + '"' + e.getGuild().getName() + '"' + ", Could not send message because the welcomer_message wasn't set in the config");
            } else {
                if (e.getGuild().getTextChannelById(core.getConfig().getLong("welcomer_channel")) == null) {
                    core.getLogger().warning("welcomer_channel channel was not found on the guild. Please make sure you entered the right channel id.");
                    PlayerUtil.sendToAuthorizedPlayers("&cError: &ewelcomer_channel channel was not found on the guild. Please make sure that you entered the right channel id.");
                } else {
                    EmbedBuilder embed = new EmbedBuilder().setDescription(String.join("\n",
                            core.getConfig().getStringList("welcomer_message"))
                            .replace("[User_Name]", e.getMember().getUser().getName())
                            .replace("[User_Mention]", e.getMember().getAsMention())
                            .replace("[User_tag]", e.getMember().getUser().getAsTag())
                    );
                    if (core.getConfig().getStringList("welcomer_message") == null) {
                        core.getLogger().info("Could not send message to welcomer channel because welcomer_message is not set in the config.");
                        PlayerUtil.sendToAuthorizedPlayers("&cError: &eCould not send message to welcomer channel because welcomer_message is not set.");

                    }

                    String config = core.getConfig().getString("welcomer_message_embed_color");

                    if (config != null) {
                        switch (config.toUpperCase()) {
                            case "AQUA":
                                embed.setColor(1752220);
                                break;
                            case "GREEN":
                                embed.setColor(3066993);
                                break;
                            case "BLUE":
                                embed.setColor(3447003);
                                break;
                            case "PURPLE":
                                embed.setColor(10181046);
                                break;
                            case "GOLD":
                                embed.setColor(15844367);
                                break;
                            case "ORANGE":
                                embed.setColor(15105570);
                                break;
                            case "RED":
                                embed.setColor(15158332);
                                break;
                            case "GREY":
                                embed.setColor(9807270);
                                break;
                            case "DARKER_GREY":
                                embed.setColor(8359053);
                                break;
                            case "NAVY":
                                embed.setColor(3426654);
                                break;
                            case "DARK_AQUA":
                                embed.setColor(1146986);
                                break;
                            case "DARK_GREEN":
                                embed.setColor(2067276);
                                break;
                            case "DARK_BLUE":
                                embed.setColor(2123412);
                                break;
                            case "DARk_PURPLE":
                                embed.setColor(7419530);
                                break;
                            case "DARK_GOLD":
                                embed.setColor(12745742);
                                break;
                            case "DARK_ORANGE":
                                embed.setColor(11027200);
                                break;
                            case "DARK_RED":
                                embed.setColor(10038562);
                                break;
                            case "DARK_GREY":
                                embed.setColor(9936031);
                                break;
                            case "LIGHT_GREY":
                                embed.setColor(12370112);
                                break;
                            case "DARK_NAVY":
                                embed.setColor(2899536);
                                break;
                            case "LUMINOUS_VIVID_PINK":
                                embed.setColor(16580705);
                                break;
                            case "DARK_VIVID_PINK":
                                embed.setColor(12320855);
                                break;
                            default:
                                PlayerUtil.sendToAuthorizedPlayers("&cError: &eInvalid color in welcomer_message_embed_color");
                        }
                    }

                    try {
                        e.getGuild().getTextChannelById(core.getConfig().getLong("welcomer_channel")).sendMessage(embed.build()).queue();
                    } catch (NullPointerException ignored) {
                        core.getLogger().warning("Channel ID in config option \"welcomer_channel\" led to an unknown channel.");
                    }
                }

            }
            if (core.getConfig().getBoolean("join_message_to_online_players")) {
                String message = core.getConfig().getString("mc_welcomer_message");
                if (message != null) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message)
                            .replace("[User_tag]", e.getMember().getUser().getAsTag())
                            .replace("[User_Name]", e.getMember().getUser().getName())
                            .replace("[Guild_Name]", e.getGuild().getName()));
                } else {
                    core.getLogger().warning("Could not send welcomer message to online players, mc_welcomer_message is not set in the config.");
                    PlayerUtil.sendToAuthorizedPlayers("&cError: &eCould not send welcomer message to online players, mc_welcomer_message is not set in the config.");
                }
            }
        });

    }



}
