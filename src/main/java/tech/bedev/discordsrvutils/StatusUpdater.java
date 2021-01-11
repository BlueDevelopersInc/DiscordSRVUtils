package tech.bedev.discordsrvutils;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Activity;
import me.clip.placeholderapi.PlaceholderAPI;

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
        if (DiscordSRVUtils.BotSettingsconfig.isStatusUpdates()) {
            try (Connection conn = core.getMemoryConnection()) {
                PreparedStatement p1 = conn.prepareStatement("SELECT * FROM status");
                p1.execute();
                ResultSet r1 = p1.executeQuery();
                if (r1.next()) {
                    String current = DiscordSRVUtils.BotSettingsconfig.Statuses().get(r1.getInt("Status"));

                    if (current.startsWith("Playing ")) {
                        if (!DiscordSRVUtils.PAPI) {
                            core.getJda().getPresence().setActivity(Activity.playing(current.replaceFirst("Playing ", "")));
                        } else {
                            core.getJda().getPresence().setActivity(Activity.playing(PlaceholderAPI.setPlaceholders(null, current.replaceFirst("Playing ", ""))));
                        }
                    } else if (current.startsWith("Watching ")) {
                        if (!DiscordSRVUtils.PAPI) {
                            core.getJda().getPresence().setActivity(Activity.watching(current.replaceFirst("Watching ", "")));
                        } else {
                            core.getJda().getPresence().setActivity(Activity.watching(PlaceholderAPI.setPlaceholders(null, current.replaceFirst("Watching ", ""))));
                        }
                    } else if (current.startsWith("Listening to ")) {
                        if (!DiscordSRVUtils.PAPI) {
                            core.getJda().getPresence().setActivity(Activity.listening(current.replaceFirst("Listening to ", "")));
                        } else {
                            core.getJda().getPresence().setActivity(Activity.listening(PlaceholderAPI.setPlaceholders(null, current.replaceFirst("Listening to ", ""))));
                        }
                    } else {
                        if (!DiscordSRVUtils.PAPI) {
                            core.getJda().getPresence().setActivity(Activity.playing(current));
                        } else {
                            core.getJda().getPresence().setActivity(Activity.playing(PlaceholderAPI.setPlaceholders(null, current)));
                        }
                    }
                    if (DiscordSRVUtils.BotSettingsconfig.Statuses().size() - 1 == r1.getInt("Status")) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE status SET Status=0 WHERE Status=?");
                        p2.setInt(1, r1.getInt("Status"));
                        p2.execute();
                    } else {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE status SET Status=? WHERE Status=?");
                        p2.setInt(1, r1.getInt("Status") + 1);
                        p2.setInt(2, r1.getInt("Status"));
                        p2.execute();

                    }

                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
