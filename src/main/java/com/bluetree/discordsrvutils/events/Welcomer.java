package com.bluetree.discordsrvutils.events;

import com.bluetree.discordsrvutils.DiscordSRVUtils;
import com.bluetree.discordsrvutils.utils.PlayerUtil;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberJoinEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Welcomer extends ListenerAdapter {

    private DiscordSRVUtils core;


    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        if (core.getConfig().getLong("welcomer_channel") == 000000000000000000) {
            core.getLogger().info(e.getMember().getUser().getName() + " Joined server" + " " + '"' + e.getGuild().getName() + '"' + ", Could not send message because the welcomer_channel wasn't set in the config");
            PlayerUtil.sendToAuthorizedPlayers("&cError: &e" + e.getMember().getUser().getName() + " Joined server" + '"' + e.getGuild().getName() + '"' + ", Could not send message because the welcomer_message wasn't set in the config");
        }
        else {
            if (e.getGuild().getTextChannelById(core.getConfig().getLong("welcomer_channel")) == null) {
                core.getLogger().warning("welcomer_channel channel was not found on the guild. Please make sure you entered the right channel id.");
                PlayerUtil.sendToAuthorizedPlayers("&cError: &ewelcomer_channel channel was not found on the guild. Please make sure that you entered the right channel id.");
            }
            else {
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
                    if (config.equalsIgnoreCase("AQUA")) {
                        embed.setColor(1752220);
                    }
                    else if (config.equalsIgnoreCase("GREEN")) {
                        embed.setColor(3066993);
                    }
                    else if (config.equalsIgnoreCase("BLUE")) {
                        embed.setColor(3447003);
                    }
                    else if (config.equalsIgnoreCase("PURPLE")) {
                        embed.setColor(10181046);
                    }
                    else if (config.equalsIgnoreCase("GOLD")) {
                        embed.setColor(15844367);
                    }
                    else if (config.equalsIgnoreCase("ORANGE")) {
                        embed.setColor(15105570);
                    }
                    else if (config.equalsIgnoreCase("RED")) {
                        embed.setColor(15158332);
                    }
                    else if (config.equalsIgnoreCase("GREY")) {
                        embed.setColor(9807270);
                    }
                    else if (config.equalsIgnoreCase("DARKER_GREY")) {
                        embed.setColor(8359053);
                    }
                    else if (config.equalsIgnoreCase("NAVY")) {
                        embed.setColor(3426654);
                    }
                    else if (config.equalsIgnoreCase("DARK_AQUA")) {
                        embed.setColor(1146986);
                    }
                    else if (config.equalsIgnoreCase("DARK_GREEN")) {
                        embed.setColor(2067276);
                    }
                    else if (config.equalsIgnoreCase("DARK_BLUE")) {
                        embed.setColor(2123412);
                    }
                    else if (config.equalsIgnoreCase("DARk_PURPLE")) {
                        embed.setColor(7419530);
                    }
                    else if (config.equalsIgnoreCase("DARK_GOLD")) {
                        embed.setColor(12745742);
                    }
                    else if (config.equalsIgnoreCase("DARK_ORANGE")) {
                        embed.setColor(11027200);
                    }
                    else if (config.equalsIgnoreCase("DARK_RED")) {
                        embed.setColor(10038562);
                    }
                    else if (config.equalsIgnoreCase("DARK_GREY")) {
                        embed.setColor(9936031);
                    }
                    else if (config.equalsIgnoreCase("LIGHT_GREY")) {
                        embed.setColor(12370112);
                    }
                    else if (config.equalsIgnoreCase("DARK_NAVY")) {
                        embed.setColor(2899536);
                    }
                    else if (config.equalsIgnoreCase("LUMINOUS_VIVID_PINK")) {
                        embed.setColor(16580705);
                    }
                    else if (config.equalsIgnoreCase("DARK_VIVID_PINK")) {
                        embed.setColor(12320855);
                    }

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

                    }


                }

                try {
                    e.getGuild().getTextChannelById(core.getConfig().getLong("welcomer_channel")).sendMessage(embed.build()).queue();
                } catch (NullPointerException ex) {
                    core.getLogger().warning("Channel ID in config option \"welcomer_channel\" led to an unknown channel.");
                }


            }

        }
        if (core.getConfig().getBoolean("join_message_to_online_players")) {
            String message = core.getConfig().getString("mc_welcomer_message");
            if (message != null) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', message)
                            .replace("[User_tag]", e.getMember().getUser().getAsTag())
                            .replace("[User_Name]", e.getMember().getUser().getName())
                            .replace("[Guild_Name]", e.getGuild().getName())
                    );
                }
            }
            else {
                core.getLogger().warning("Could not send welcomer message to online players, mc_welcomer_message is not set in the config.");
                PlayerUtil.sendToAuthorizedPlayers("&cError: &eCould not send welcomer message to online players, mc_welcomer_message is not set in the config.");
            }
        }
    }

}
