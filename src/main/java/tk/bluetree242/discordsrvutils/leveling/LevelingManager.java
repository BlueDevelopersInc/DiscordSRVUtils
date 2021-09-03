package tk.bluetree242.discordsrvutils.leveling;

import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.Bukkit;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;
import tk.bluetree242.discordsrvutils.leveling.listeners.bukkit.BukkitLevelingListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LevelingManager {
    public final Long MAP_EXPIRATION_NANOS = Duration.ofSeconds(60L).toNanos();
    public final Map<UUID, Long> antispamMap = new HashMap<>();
    private DiscordSRVUtils core = DiscordSRVUtils.get();
    private static LevelingManager main;
    public static LevelingManager get() {
        return main;
    }

    public LevelingManager() {
        main = this;
    }

    public CompletableFuture<PlayerStats> getPlayerStats(UUID uuid) {
        return core.completableFuture(() -> {
           try (Connection conn = core.getDatabase()) {
               PreparedStatement p1 = conn.prepareStatement("SELECT * FROM leveling WHERE UUID=?");
               p1.setString(1, uuid.toString());
               ResultSet r1 = p1.executeQuery();
               if (!r1.next()) return null;
               return getPlayerStats(r1);
           } catch (SQLException e) {
               throw new UnCheckedSQLException(e);
           }
        });
    }

    public CompletableFuture<PlayerStats> getPlayerStats(long discordID) {
        return core.completableFuture(() -> {
           UUID uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(discordID + "");
           if (uuid == null) return null;
           return core.handleCFOnAnother(getPlayerStats(uuid));
        });
    }

    public PlayerStats getPlayerStats(ResultSet r) throws SQLException {
        return new PlayerStats(UUID.fromString(r.getString("UUID")), r.getString("Name"), r.getInt("level"), r.getInt("xp"), r.getInt("MinecraftMessages"), r.getInt("DiscordMessages"));
    }
}
