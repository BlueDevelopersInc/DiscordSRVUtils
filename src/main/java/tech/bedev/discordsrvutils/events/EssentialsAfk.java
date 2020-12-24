package tech.bedev.discordsrvutils.events;


import github.scarsz.discordsrv.DiscordSRV;
import me.clip.placeholderapi.PlaceholderAPI;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tech.bedev.discordsrvutils.DiscordSRVUtils;
import tech.bedev.discordsrvutils.Managers.ConfOptionsManager;
import tech.bedev.discordsrvutils.utils.PlayerUtil;

public class EssentialsAfk implements Listener {

    private ConfOptionsManager conf;
    private final DiscordSRVUtils core;

    public EssentialsAfk(DiscordSRVUtils core) {
        this.core = core;
        this.conf = new ConfOptionsManager(core);

    }

    @EventHandler
    public void onPlayerAfkChange(AfkStatusChangeEvent e) {
        if (e.getAffected().isVanished()) return;
        if (!e.getAffected().isAfk()) {
            if (DiscordSRVUtils.Config.isEssentialsAfkMessages()) {
                DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(DiscordSRVUtils.BotSettingsconfig.chat_channel()).sendMessage(conf.getConfigWithPapi(e.getAffected().getBase().getUniqueId(), String.join("\n", DiscordSRVUtils.Config.WelcomerMessage()))
                .replace("[Player_Name]", e.getAffected().getBase().getName())
                ).queue();

            }
        } else {
            if (DiscordSRVUtils.Config.isEssentialsAfkMessages()) {
                DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(DiscordSRVUtils.BotSettingsconfig.chat_channel()).sendMessage(conf.getConfigWithPapi(e.getAffected().getBase().getUniqueId(), String.join("\n", DiscordSRVUtils.Config.WelcomerMessage()))
                        .replace("[Player_Name]", e.getAffected().getBase().getName())
                ).queue();
            }
        }
    }
    }

