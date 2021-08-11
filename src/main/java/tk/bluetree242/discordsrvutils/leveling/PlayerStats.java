package tk.bluetree242.discordsrvutils.leveling;

import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerStats {
    private DiscordSRVUtils core = DiscordSRVUtils.get();
    private UUID uuid;
    private String name;
    private int level;
    private int xp;

    public PlayerStats(UUID uuid, String name, int level, int xp) {
        this.uuid = uuid;
        this.name = name;
        this.level = level;
        this.xp = xp;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public CompletableFuture<Void> setLevel(int level) {
        return core.completableFutureRun(() -> {
           try (Connection conn = core.getDatabase()) {
               PreparedStatement p1 = conn.prepareStatement("UPDATE leveling SET Level=? WHERE UUID=?");
               p1.setInt(1, level);
               p1.setString(2, uuid.toString());
               p1.execute();
               this.level = level;
           } catch (SQLException e) {
               throw new UnCheckedSQLException(e);
           }
        });
    }

    /**
     * @param xp XP to add
     * @return true if player leveled up, false if not
     */
    public CompletableFuture<Boolean> setXP(int xp) {
        return core.completableFuture(() -> {
            try (Connection conn = core.getDatabase()) {
                if (xp >= 300) {
                    PreparedStatement p1 = conn.prepareStatement("UPDATE leveling SET XP=0, Level=? WHERE UUID=?");
                    p1.setInt(1, level + 1);
                    p1.setString(2, uuid.toString());
                    p1.execute();
                    this.level = level + 1;
                    this.xp = 0;
                    return true;
                }
                PreparedStatement p1 = conn.prepareStatement("UPDATE leveling SET XP=? WHERE UUID=?");
                p1.setInt(1, xp);
                p1.setString(2, uuid.toString());
                p1.execute();
                this.xp = xp;
                return false;

            } catch (SQLException ex) {
                throw new UnCheckedSQLException(ex);
            }
        });
    }
}
