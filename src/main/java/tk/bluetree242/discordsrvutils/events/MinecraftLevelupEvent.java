package tk.bluetree242.discordsrvutils.events;

import github.scarsz.discordsrv.api.events.Event;
import org.bukkit.entity.Player;
import tk.bluetree242.discordsrvutils.leveling.PlayerStats;

public class MinecraftLevelupEvent extends Event {
    private PlayerStats stats;
    private Player player;

    public MinecraftLevelupEvent(PlayerStats stats, Player player) {
        this.stats = stats;
        this.player = player;
    }

    public PlayerStats getStats() {
        return stats;
    }

    public Player getPlayer() {
        return player;
    }
}
