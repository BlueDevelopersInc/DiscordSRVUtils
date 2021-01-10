package tech.bedev.discordsrvutils.events;


import github.scarsz.discordsrv.DiscordSRV;
import me.clip.placeholderapi.PlaceholderAPI;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tech.bedev.discordsrvutils.DiscordSRVUtils;
<<<<<<< Updated upstream
import tech.bedev.discordsrvutils.Managers.ConfOptionsManager;
import tech.bedev.discordsrvutils.utils.PlayerUtil;
=======
import tech.bedev.discordsrvutils.managers.ConfOptionsManager;

>>>>>>> Stashed changes

public class EssentialsAfk implements Listener {

    private final ConfOptionsManager conf;

    public EssentialsAfk(DiscordSRVUtils core) {
        this.conf = new ConfOptionsManager(core);

    }

    @EventHandler
    public void onPlayerAfkChange(AfkStatusChangeEvent e) {
        if (e.getAffected().isVanished()) return;
        if (!e.getAffected().isAfk()) {
            if (DiscordSRVUtils.Config.isEssentialsAfkMessages()) {
                DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(DiscordSRVUtils.BotSettingsconfig.chat_channel()).sendMessage(conf.getConfigWithPapi(e.getAffected().getBase().getUniqueId(), String.join("\n", DiscordSRVUtils.Config.EssentialsAfkMessage()))
                .replace("[Player_Name]", e.getAffected().getBase().getName())
                ).queue();

            }
        } else {
            if (DiscordSRVUtils.Config.isEssentialsAfkMessages()) {
                DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(DiscordSRVUtils.BotSettingsconfig.chat_channel()).sendMessage(conf.getConfigWithPapi(e.getAffected().getBase().getUniqueId(), String.join("\n", DiscordSRVUtils.Config.EssentialsNoLongerAfkMessage()))
                        .replace("[Player_Name]", e.getAffected().getBase().getName())
                ).queue();
            }
        }
    }
    }

