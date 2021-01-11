package tech.bedev.discordsrvutils;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import tech.bedev.discordsrvutils.Managers.TimerManager;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

public class TimeHandler extends TimerTask {
    private final DiscordSRVUtils core;

    public TimeHandler(DiscordSRVUtils core) {
        this.core = core;
    }

    public TimerManager getTimerManager() {
        return new TimerManager();
    }

    @Override
    public void run() {
        if (DiscordSRVUtils.BotSettingsconfig.isBungee()) return;
        if (!DiscordSRVUtils.isReady) return;
        Iterator it = core.tempmute.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Long userID = (Long) pair.getKey();
            Long expiration = (Long) pair.getValue();
            if (expiration <= getTimerManager().getCurrentTime()) {
                Member persontounmute = DiscordSRV.getPlugin().getMainGuild().getMemberById(userID);
                if (persontounmute != null) {
                    if (persontounmute.getRoles().contains(DiscordSRV.getPlugin().getMainGuild().getRoleById(DiscordSRVUtils.Moderationconfig.MutedRole()))) {
                        DiscordSRV.getPlugin().getMainGuild().removeRoleFromMember(persontounmute, DiscordSRV.getPlugin().getMainGuild().getRoleById(DiscordSRVUtils.Moderationconfig.MutedRole())).queue();
                    }
                }
                core.tempmute.remove(userID);
            }

        }

    }
}
