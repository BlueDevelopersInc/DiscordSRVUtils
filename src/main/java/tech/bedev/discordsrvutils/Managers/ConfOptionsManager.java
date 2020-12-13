package tech.bedev.discordsrvutils.Managers;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tech.bedev.discordsrvutils.DiscordSRVUtils;

public class ConfOptionsManager {
    private DiscordSRVUtils core;

    public ConfOptionsManager(DiscordSRVUtils core) {
        this.core = core;
    }
    public static JDA getJda() {
        return DiscordSRV.getPlugin().getJda();
    }

    public String getConfigWithPapi(Player p, String msg) {
        if (DiscordSRVUtils.PAPI) {
            if (p != null) {
                return PlaceholderAPI.setPlaceholders(p, msg);
            } else {
                return PlaceholderAPI.setPlaceholders(null, msg);
            }
        } else {
            return msg;
        }
    }


    public String StringListToString(@NotNull String path) {

        return String.join("\n", core.getConfig().getStringList(path));
    }

    public String configToColorCodes(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
    public String StringToColorCodes(String path) {
        return ChatColor.translateAlternateColorCodes('&', core.getConfig().getString(path));
    }

    public boolean getBoolean(String path) {
        if (core.getConfig().getBoolean(path)) {
            return true;
        }
        return false;
    }
}
