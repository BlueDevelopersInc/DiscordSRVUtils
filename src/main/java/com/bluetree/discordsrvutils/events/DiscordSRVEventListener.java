package com.bluetree.discordsrvutils.events;

import com.bluetree.discordsrvutils.DiscordSRVUtils;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePreProcessEvent;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.OnlineStatus;

public class DiscordSRVEventListener {
    private final DiscordSRVUtils core;

    public DiscordSRVEventListener(DiscordSRVUtils core) {
        this.core = core;
    }


    public static JDA getJda() {
        return DiscordSRV.getPlugin().getJda();
    }

    @Subscribe
    public void onReady(DiscordReadyEvent e) {
        String status = core.getConfig().getString("bot_status");
        if (status != null) {
            switch (status.toUpperCase()) {
                case "DND":
                    getJda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                    break;
                case "IDLE":
                    getJda().getPresence().setStatus(OnlineStatus.IDLE);
                    break;
                case "ONLINE":
                    getJda().getPresence().setStatus(OnlineStatus.ONLINE);
                    break;
            }
        }
    }
}
