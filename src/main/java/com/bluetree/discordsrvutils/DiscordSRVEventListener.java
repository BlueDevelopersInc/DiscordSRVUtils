package com.bluetree.discordsrvutils;

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
    public static JDA jda;

    @Subscribe
    public void onDiscordGuildMessagePreProccessEvent(DiscordGuildMessagePreProcessEvent e) {


    }
    @Subscribe
    public void onDISCORDSRVReady(DiscordReadyEvent e) {
        if (core.getConfig().getString("bot_status") == null) {

        }
        else {
            if (core.getConfig().getString("bot_status").equalsIgnoreCase("DND")) {
                getJda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
            }
            else if (core.getConfig().getString("bot_status").equalsIgnoreCase("IDLE")) {
                getJda().getPresence().setStatus(OnlineStatus.IDLE);
            }
            else if (core.getConfig().getString("bot_status").equalsIgnoreCase("ONLINE")) {
                getJda().getPresence().setStatus(OnlineStatus.ONLINE);
            }

        }
        getJda().addEventListener(core.JDALISTENER);

    }
}
