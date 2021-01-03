package tech.bedev.discordsrvutils.events;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AccountLinkedEvent;
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePreProcessEvent;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.OnlineStatus;
import me.leoko.advancedban.shaded.org.apache.commons.lang3.ObjectUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import tech.bedev.discordsrvutils.DiscordSRVUtils;
import tech.bedev.discordsrvutils.StatusUpdater;
import tech.bedev.discordsrvutils.TimeHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;

public class DiscordSRVEventListener {
    private final DiscordSRVUtils core;

    public DiscordSRVEventListener(DiscordSRVUtils core) {
        this.core = core;
    }


    public static JDA getJda() {
        return DiscordSRV.getPlugin().getJda();
    }

    @Subscribe
    public void onReady(DiscordReadyEvent e) {

        String status = DiscordSRVUtils.BotSettingsconfig.status();
        if (status != null) {
            switch (status.toUpperCase()) {
                case "DND":
                    getJda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                    break;
                case "IDLE":
                    getJda().getPresence().setStatus(OnlineStatus.IDLE);
                    break;
                case "ONLINE":
                    getJda().getPresence().setStatus(OnlineStatus.ONLINE);
                    break;
            }
        }
        getJda().addEventListener(core.JDALISTENER);

        try (Connection conn = core.getMemoryConnection()) {
            PreparedStatement p1 = conn.prepareStatement("SELECT * FROM status");
            p1.execute();
            ResultSet r1 = p1.executeQuery();
            if (r1.next()) {
                PreparedStatement p2 = conn.prepareStatement("UPDATE status SET Status=0 WHERE Status=?");
                p2.setInt(1, r1.getInt("Status"));
            } else {
                PreparedStatement p2 = conn.prepareStatement("INSERT INTO status (Status) VALUES (0)");
                p2.execute();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();

        }
        DiscordSRVUtils.timer.cancel();
        DiscordSRVUtils.timer = new Timer();
        if (DiscordSRVUtils.BotSettingsconfig.isStatusUpdates()) {
            String l = DiscordSRVUtils.BotSettingsconfig.Status_Update_Interval() + "000";
            DiscordSRVUtils.timer.schedule(new StatusUpdater(core), 0, Integer.parseInt(l));
        }
        DiscordSRVUtils.isReady = true;
    }

    @Subscribe
    public void  onLink(AccountLinkedEvent e) {
        try (Connection conn = core.getDatabaseFile()) {
            PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
            p1.setString(1, Bukkit.getOfflinePlayer(e.getPlayer().getName()).getUniqueId().toString());
            p1.execute();
            ResultSet r1 = p1.executeQuery();
            if (r1.next()) {
                PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET userID=? WHERE unique_id=?");
                p2.setLong(1, e.getUser().getIdLong());
                p2.setString(2, Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()).getUniqueId().toString());
                p2.execute();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Subscribe
    public void onUnlink(AccountUnlinkedEvent e) {
        try (Connection conn = core.getDatabaseFile()) {
            PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
            p1.setString(1, Bukkit.getOfflinePlayer(e.getPlayer().getName()).getUniqueId().toString());
            p1.execute();
            ResultSet r1 = p1.executeQuery();
            if (r1.next()) {
                PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET userID=NULL WHERE unique_id=?");
                p2.setString(1, Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()).getUniqueId().toString());
                p2.execute();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
