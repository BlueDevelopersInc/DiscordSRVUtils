package tech.bedev.discordsrvutils;

import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageReaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimerTask;

public class helpDeleter extends TimerTask {
    private DiscordSRVUtils core;
    public helpDeleter(DiscordSRVUtils core) {
        this.core = core;
    }
    @Override
    public void run() {
        try (Connection conn = core.getMemoryConnection()) {
            PreparedStatement p1 = conn.prepareStatement("SELECT * FROM helpmsges");
            ResultSet r1 = p1.executeQuery();
            while (r1.next()) {
                Long lastOutput = r1.getLong("LastOutput");
                if ((System.currentTimeMillis() - lastOutput) > 180000L) {
                    PreparedStatement p2 = conn.prepareStatement("DELETE FROM helpmsges WHERE MessageID=?");
                    p2.setLong(1, r1.getLong("MessageID"));
                    p2.execute();
                    DiscordSRVUtils.getJda().getTextChannelById(r1.getLong("Channel")).editMessageById(r1.getLong("MessageID"), ":timer: Timed out").embed(null).override(true).queue(msg -> {
                        for (MessageReaction reaction : msg.getReactions()) {
                            reaction.removeReaction().queue();
                        }
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
