package tk.bluetree242.discordsrvutils.leveling.listeners.bukkit;

import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.config.LevelingConfig;
import tk.bluetree242.discordsrvutils.events.MinecraftLevelupEvent;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;
import tk.bluetree242.discordsrvutils.leveling.LevelingManager;
import tk.bluetree242.discordsrvutils.leveling.MessageType;
import tk.bluetree242.discordsrvutils.leveling.PlayerStats;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import tk.bluetree242.discordsrvutils.utils.Utils;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BukkitLevelingListener implements Listener {
    private DiscordSRVUtils core = DiscordSRVUtils.get();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e) {
        core.executeAsync(() -> {
            try (Connection conn = core.getDatabase()) {
                PreparedStatement p1 = conn.prepareStatement("SELECT * FROM leveling WHERE UUID=?");
                p1.setString(1, e.getPlayer().getUniqueId().toString());
                ResultSet r1 = p1.executeQuery();
                if (!r1.next()) {
                    PreparedStatement p2 = conn.prepareStatement("INSERT INTO leveling (UUID, Name, Level, XP) VALUES (?, ?, ?, ?)");
                    p2.setString(1, e.getPlayer().getUniqueId().toString());
                    p2.setString(2, e.getPlayer().getName());
                    p2.setInt(3, 0);
                    p2.setInt(4, 0);
                    p2.execute();
                } else {
                    if (!r1.getString("name").equals(e.getPlayer().getName())) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE leveling SET Name=? WHERE UUID=?");
                        p2.setString(1, e.getPlayer().getName());
                        p2.setString(2, e.getPlayer().getUniqueId().toString());
                        p2.execute();
                    }
                }
            } catch (SQLException ex) {
                throw new UnCheckedSQLException(ex);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (!core.getLevelingConfig().enabled()) return;
        if (e.isCancelled()) return;
            core.handleCF(LevelingManager.get().getPlayerStats(e.getPlayer().getUniqueId()), stats -> {
                if (stats == null) {
                    return;
                }
                if (core.getLevelingConfig().antispam_messages()) {
                    Long val = LevelingManager.get().antispamMap.get(stats.getUuid());
                    if (val == null) {
                        LevelingManager.get().antispamMap.put(stats.getUuid(), System.nanoTime());
                    } else {
                        if (!(System.nanoTime() - val >= LevelingManager.get().MAP_EXPIRATION_NANOS)) return;
                        LevelingManager.get().antispamMap.remove(stats.getUuid());
                        LevelingManager.get().antispamMap.put(stats.getUuid(), System.nanoTime());
                    }
                }
                int toAdd = new SecureRandom().nextInt(50);
                boolean leveledUp = core.handleCFOnAnother(stats.setXP(stats.getXp() + toAdd));
                core.handleCFOnAnother(stats.addMessage(MessageType.MINECRAFT));
                if (leveledUp) {
                    try {
                        DiscordSRV.api.callEvent(new MinecraftLevelupEvent(stats, e.getPlayer()));
                    } catch (Exception x) {}
                    e.getPlayer().sendMessage(Utils.colors(PlaceholdObjectList.ofArray(new PlaceholdObject(stats, "stats"),  new PlaceholdObject(e.getPlayer(), "player")).apply(String.join("\n", core.getLevelingConfig().minecraft_levelup_message()), e.getPlayer())));


                }
            }, null);
    }
}
