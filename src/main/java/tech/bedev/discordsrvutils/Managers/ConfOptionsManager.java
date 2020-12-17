package tech.bedev.discordsrvutils.Managers;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tech.bedev.discordsrvutils.DiscordSRVUtils;

import java.util.UUID;

public class ConfOptionsManager {
    private DiscordSRVUtils core;

    public ConfOptionsManager(DiscordSRVUtils core) {
        this.core = core;
    }
    public static JDA getJda() {
        return DiscordSRV.getPlugin().getJda();
    }

    public String getConfigWithPapi(UUID uuid, String msg) {
        if (DiscordSRVUtils.PAPI) {
            if (uuid != null) {
                return PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(uuid), msg);
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
    public String StringListToStringColorCodes(@NotNull String path) {

        return ChatColor.translateAlternateColorCodes('&', String.join("\n", core.getConfig().getStringList(path)));
    }

    public String StringToColorCodes(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
    public String ConfigToColorCodes(String path) {
        return ChatColor.translateAlternateColorCodes('&', core.getConfig().getString(path));
    }

    public boolean getBoolean(String path) {
        return core.getConfig().getBoolean(path);
    }
}
