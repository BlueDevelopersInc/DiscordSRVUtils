package com.bluetree.discordsrvutils.events;


import com.bluetree.discordsrvutils.DiscordSRVUtils;
import github.scarsz.discordsrv.DiscordSRV;
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
                if (core.getConfig().getStringList("essentials_player_afk_message") == null) {
                    JDALISTENER.sendToPeopleWithPerms("&cError: &eCould not send essentials afk toggle message because essentials_player_afk_message doesn't exist in the config.");

                }
                else {
                    //send message
                    DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("global").sendMessage(String.join("\n", core.getConfig().getStringList("essentials_player_afk_message"))
                            .replace("[Player_Name]", e.getAffected().getName())
                            .replace("[Player_DisplayName]", e.getAffected().getBase().getDisplayName())
                    ).queue();
                }
            }
        } else {
            if (core.getConfig().getBoolean("essentials_afk_to_discord")) {
                if (core.getConfig().getString("essentials_player_no_longer_afk_message") == null) {
                    JDALISTENER.sendToPeopleWithPerms("&cError: &eCould not send essentials afk toggle message because essentials_player_no_longer_afk_message doesn't exist in the config.");
                } else {
                    DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("global").sendMessage(String.join("\n", core.getConfig().getStringList("essentials_player_no_longer_afk_message"))
                    .replace("[Player_Name]", e.getAffected().getName())
                            .replace("[Player_DisplayName]", e.getAffected().getBase().getDisplayName())
                    ).queue();
                    //send message
                }

                }


            }
    }


}
