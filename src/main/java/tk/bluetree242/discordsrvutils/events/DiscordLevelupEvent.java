package tk.bluetree242.discordsrvutils.events;

import github.scarsz.discordsrv.api.events.Event;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import tk.bluetree242.discordsrvutils.leveling.PlayerStats;

public class DiscordLevelupEvent extends Event {
    private PlayerStats stats;
    private TextChannel channel;
    private User user;

    public DiscordLevelupEvent(PlayerStats stats, TextChannel channel, User user) {
        this.stats = stats;
        this.channel = channel;
        this.user = user;
    }

    public PlayerStats getStats() {
        return stats;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public User getUser() {
        return user;
    }
}
