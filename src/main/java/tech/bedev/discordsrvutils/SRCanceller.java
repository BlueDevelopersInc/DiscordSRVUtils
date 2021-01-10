package tech.bedev.discordsrvutils;

import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageReaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimerTask;

public class SRCanceller extends TimerTask {
    private DiscordSRVUtils core;
    public SRCanceller(DiscordSRVUtils core) {
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
                    PreparedStatement p2 = conn.prepareStatement("DELETE FROM srmsgesreply WHERE Channel=? AND userid=?");
                    p2.setLong(1, r1.getLong("Channel"));
                    p2.setLong(2, r1.getLong("userid"));
                    p2.execute();
                DiscordSRVUtils.getJda().getTextChannelById(r1.getLong("Channel")).sendMessage("Cancelled.").queue();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
