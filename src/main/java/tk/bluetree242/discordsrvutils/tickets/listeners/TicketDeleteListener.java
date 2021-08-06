package tk.bluetree242.discordsrvutils.tickets.listeners;


import github.scarsz.discordsrv.dependencies.jda.api.events.channel.text.TextChannelDeleteEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;
import tk.bluetree242.discordsrvutils.tickets.Ticket;
import tk.bluetree242.discordsrvutils.tickets.TicketManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TicketDeleteListener extends ListenerAdapter {

    public void onTextChannelDelete(TextChannelDeleteEvent e) {
            DiscordSRVUtils.get().executeAsync(() -> {
                try (Connection conn = DiscordSRVUtils.get().getDatabase()) {
                    PreparedStatement p1 = conn.prepareStatement("DELETE FROM tickets WHERE Channel=?");
                    p1.setLong(1, e.getChannel().getIdLong());
                    p1.execute();
                } catch (SQLException ex) {
                    throw new UnCheckedSQLException(ex);
                }
            });
    }
}
