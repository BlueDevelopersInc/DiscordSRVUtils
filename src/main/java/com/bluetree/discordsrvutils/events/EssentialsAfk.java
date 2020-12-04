package com.bluetree.discordsrvutils.events;


import com.bluetree.discordsrvutils.DiscordSRVUtils;
import com.bluetree.discordsrvutils.utils.PlayerUtil;
import github.scarsz.discordsrv.DiscordSRV;
import me.clip.placeholderapi.PlaceholderAPI;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EssentialsAfk implements Listener {
    private final DiscordSRVUtils core;

    public EssentialsAfk(DiscordSRVUtils core) {
        this.core = core;

    }

    @EventHandler
    public void onPlayerAfkChange(AfkStatusChangeEvent e) {
        if (!e.getAffected().isAfk()) {
            if (core.getConfig().getBoolean("essentials_afk_to_discord")) {
                if (DiscordSRVUtils.PAPI) {

                if (!core.getConfig().getStringList("essentials_player_afk_message").isEmpty()) {
                    DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("global").sendMessage(PlaceholderAPI.setPlaceholders(e.getAffected().getBase(), String.join("\n", core.getConfig().getStringList("essentials_player_afk_message"))
                            .replace("[Player_Name]", e.getAffected().getName())
                            .replace("[Player_DisplayName]", e.getAffected().getBase().getDisplayName()))
                    ).queue();

                } else {
                    PlayerUtil.sendToAuthorizedPlayers("&cError: &eCould not send essentials afk toggle message because essentials_player_afk_message doesn't exist in the config.");
                }
            } else {
                    if (!core.getConfig().getStringList("essentials_player_afk_message").isEmpty()) {
                        DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("global").sendMessage(String.join("\n", core.getConfig().getStringList("essentials_player_afk_message"))
                                .replace("[Player_Name]", e.getAffected().getName())
                                .replace("[Player_DisplayName]", e.getAffected().getBase().getDisplayName())
                        ).queue();

                    } else {
                        PlayerUtil.sendToAuthorizedPlayers("&cError: &eCould not send essentials afk toggle message because essentials_player_afk_message doesn't exist in the config.");
                    }

                }
            }
        } else {
            if (core.getConfig().getBoolean("essentials_afk_to_discord")) {
                if (DiscordSRVUtils.PAPI) {

                    if (!core.getConfig().getStringList("essentials_player_no_longer_afk_message").isEmpty()) {
                        DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("global").sendMessage(PlaceholderAPI.setPlaceholders(e.getAffected().getBase(), String.join("\n", core.getConfig().getStringList("essentials_player_no_longer_afk_message"))
                                .replace("[Player_Name]", e.getAffected().getName())
                                .replace("[Player_DisplayName]", e.getAffected().getBase().getDisplayName()))
                        ).queue();

                    } else {
                        PlayerUtil.sendToAuthorizedPlayers("&cError: &eCould not send essentials afk toggle message because essentials_player_afk_message doesn't exist in the config.");
                    }
                } else {
                    if (!core.getConfig().getStringList("essentials_player_afk_message").isEmpty()) {
                        DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("global").sendMessage(String.join("\n", core.getConfig().getStringList("essentials_player_no_longer_afk_message"))
                                .replace("[Player_Name]", e.getAffected().getName())
                                .replace("[Player_DisplayName]", e.getAffected().getBase().getDisplayName())
                        ).queue();

                    } else {
                        PlayerUtil.sendToAuthorizedPlayers("&cError: &eCould not send essentials afk toggle message because essentials_player_afk_message doesn't exist in the config.");
                    }

                }
            }
        }
        }
    }

