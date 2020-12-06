package tech.bedev.discordsrvutils.events;


import github.scarsz.discordsrv.DiscordSRV;
import me.clip.placeholderapi.PlaceholderAPI;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tech.bedev.discordsrvutils.DiscordSRVUtils;
import tech.bedev.discordsrvutils.utils.PlayerUtil;

public class EssentialsAfk implements Listener {
    private final DiscordSRVUtils core;

    public EssentialsAfk(DiscordSRVUtils core) {
        this.core = core;

    }

    @EventHandler
    public void onPlayerAfkChange(AfkStatusChangeEvent e) {
        if (e.getAffected().isVanished()) return;
        if (!e.getAffected().isAfk()) {
            if (core.getConfig().getBoolean("essentials_afk_to_discord")) {
                if (DiscordSRVUtils.PAPI) {

                if (!core.getConfig().getStringList("essentials_player_afk_message").isEmpty()) {
                    DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(core.getConfig().getString("chat_channel")).sendMessage(PlaceholderAPI.setPlaceholders(e.getAffected().getBase(), String.join("\n", core.getConfig().getStringList("essentials_player_afk_message"))
                            .replace("[Player_Name]", e.getAffected().getName())
                            .replace("[Player_DisplayName]", e.getAffected().getBase().getDisplayName()))
                    ).queue();

                } else {
                    PlayerUtil.sendToAuthorizedPlayers("&cError: &eCould not send essentials afk toggle message because essentials_player_afk_message doesn't exist in the config.");
                }
            } else {
                    if (!core.getConfig().getStringList("essentials_player_afk_message").isEmpty()) {
                        DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(core.getConfig().getString("chat_channel")).sendMessage(String.join("\n", core.getConfig().getStringList("essentials_player_afk_message"))
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
                        DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(core.getConfig().getString("chat_channel")).sendMessage(PlaceholderAPI.setPlaceholders(e.getAffected().getBase(), String.join("\n", core.getConfig().getStringList("essentials_player_no_longer_afk_message"))
                                .replace("[Player_Name]", e.getAffected().getName())
                                .replace("[Player_DisplayName]", e.getAffected().getBase().getDisplayName()))
                        ).queue();

                    } else {
                        PlayerUtil.sendToAuthorizedPlayers("&cError: &eCould not send essentials afk toggle message because essentials_player_afk_message doesn't exist in the config.");
                    }
                } else {
                    if (!core.getConfig().getStringList("essentials_player_afk_message").isEmpty()) {
                        DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(core.getConfig().getString("chat_channel")).sendMessage(String.join("\n", core.getConfig().getStringList("essentials_player_no_longer_afk_message"))
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

