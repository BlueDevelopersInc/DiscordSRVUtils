package tech.bedev.discordsrvutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimerTask;

public class SuggestionsIdleHandler extends TimerTask {
    private DiscordSRVUtils core;
    public SuggestionsIdleHandler(DiscordSRVUtils core) {
        this.core = core;
    }
    @Override
    public void run() {
     try (Connection conn = core.getMemoryConnection()) {
         PreparedStatement p1 = conn.prepareStatement("SELECT * FROM suggestions_Awaiting");
         ResultSet r1 = p1.executeQuery();
         while (r1.next()) {
             if ((System.currentTimeMillis() - r1.getLong("LastOutput")) >= 180000L) {
                 PreparedStatement p2 = conn.prepareStatement("DELETE FROM suggestions_Awaiting WHERE userid=? AND channel=?");
                 p2.setLong(1, r1.getLong("userid"));
                 p2.setLong(2, r1.getLong("channel"));
                 p2.execute();
                 DiscordSRVUtils.getJda().getTextChannelById(r1.getLong("channel")).sendMessage("Automaticlly Cancelled.").queue();
             }
         }
     } catch (SQLException ex) {
         ex.printStackTrace();
     }

    }
}
