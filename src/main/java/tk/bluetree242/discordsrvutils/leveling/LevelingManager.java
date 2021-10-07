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
import java.util.*;
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
               return getPlayerStats(conn, uuid);
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

    public CompletableFuture<PlayerStats> getPlayerStats(String name) {
        return core.completableFuture(() -> {
            try (Connection conn = core.getDatabase()) {
                return getPlayerStats(conn, name);
            } catch (SQLException e) {
                throw new UnCheckedSQLException(e);
            }
        });
    }


    public PlayerStats getPlayerStats(Connection conn, UUID uuid) throws SQLException{
        PreparedStatement p1 = conn.prepareStatement("SELECT * FROM leveling ORDER BY Level DESC");
        ResultSet r1 = p1.executeQuery();
        int num = 0;
        while (r1.next()) {
            num++;
            if (r1.getString("UUID").equals(uuid.toString())) {
                return getPlayerStats(r1, num);
            }
        }
        return null;
    }

    public PlayerStats getPlayerStats(Connection conn, String name) throws SQLException{
        PreparedStatement p1 = conn.prepareStatement("SELECT * FROM leveling ORDER BY Level DESC ");
        ResultSet r1 = p1.executeQuery();
        int num = 0;
        while (r1.next()) {
            num++;
            if (r1.getString("Name").equalsIgnoreCase(name)) {
                return getPlayerStats(r1, num);
            }
        }
        return null;
    }
    public PlayerStats getPlayerStats(ResultSet r, int rank) throws SQLException {
        return new PlayerStats(UUID.fromString(r.getString("UUID")), r.getString("Name"), r.getInt("level"), r.getInt("xp"), r.getInt("MinecraftMessages"), r.getInt("DiscordMessages"), rank);
    }

    public CompletableFuture<List<PlayerStats>> getLeaderboard(int max) {
        return core.completableFuture(() -> {
            try (Connection conn = core.getDatabase()) {
                PreparedStatement p1 = conn.prepareStatement("SELECT * FROM leveling ORDER BY Level DESC ");
                List<PlayerStats> leaderboard = new ArrayList<>();
                ResultSet r1 = p1.executeQuery();
                int num = 0;
                while (r1.next()) {
                    num++;
                    if (num <= max) {
                        leaderboard.add(getPlayerStats(r1, num));
                    }
                }
                return leaderboard;
            } catch (SQLException e) {
                throw new UnCheckedSQLException(e);
            }
        });
    }
}
