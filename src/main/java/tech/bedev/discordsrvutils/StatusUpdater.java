package tech.bedev.discordsrvutils;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Activity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimerTask;

public class StatusUpdater extends TimerTask {
    private DiscordSRVUtils core;
    public StatusUpdater(DiscordSRVUtils core) {
        this.core = core;
    }

    @Override
    public void run() {
        if (core.getConfig().getBoolean("update_status")) {
            try (Connection conn = core.getMemoryConnection()) {
                PreparedStatement p1 = conn.prepareStatement("SELECT * FROM status");
                p1.execute();
                ResultSet r1 = p1.executeQuery();
                if (r1.next()) {
                    String current = core.getConfig().getStringList("status_updates").get(r1.getInt("Status"));

                    if (current.startsWith("Playing ")) {
                        core.getJda().getPresence().setActivity(Activity.playing(current.replaceFirst("Playing ", "")));
                    } else if (current.startsWith("Watching ")) {
                        core.getJda().getPresence().setActivity(Activity.watching(current.replaceFirst("Watching ", "")));
                    } else if (current.startsWith("Listening to ")) {
                        core.getJda().getPresence().setActivity(Activity.listening(current.replaceFirst("Listening to ", "")));
                    } else {
                        core.getJda().getPresence().setActivity(Activity.playing(current));
                    }
                    if (core.getConfig().getStringList("status_updates").size() -1 == r1.getInt("Status")) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE status SET Status=0 WHERE Status=?");
                        p2.setInt(1, r1.getInt("Status"));
                        p2.execute();
                    } else {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE status SET Status=? WHERE Status=?");
                        p2.setInt(1, r1.getInt("Status") +1);
                        p2.setInt(2, r1.getInt("Status"));
                        p2.execute();

                    }

                }
            }catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
