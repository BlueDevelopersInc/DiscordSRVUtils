package tk.bluetree242.discordsrvutils.bukkit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.platform.PlatformPlayer;

import java.util.UUID;

@RequiredArgsConstructor
public class BukkitOfflinePlayer extends PlatformPlayer {
    @Getter private final OfflinePlayer player;
    private final DiscordSRVUtils core;
    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public void sendMessage(String msg) {
        //offline
    }

    @Override
    public boolean hasPermission(String node) {
        return false; //false for security
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public String placeholders(String s) {
        return core.getPlatform().placehold(this, s);
    }
}
