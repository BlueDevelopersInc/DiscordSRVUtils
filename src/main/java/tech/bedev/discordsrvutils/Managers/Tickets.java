package tech.bedev.discordsrvutils.managers;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import tech.bedev.discordsrvutils.DiscordSRVUtils;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Tickets {

    private DiscordSRVUtils core;

    public Tickets(DiscordSRVUtils core) {
        this.core = core;
    }
    public static JDA getJda() {
        return DiscordSRV.getPlugin().getJda();
    }

    public void createTicket(int id, TextChannel channel, Long openedCategory, Long closedCategory, String name, List<Long> roles) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(name);
        embed.setDescription("React with \uD83D\uDCE9 to create a ticket.");
        embed.setColor(Color.CYAN);
        channel.sendMessage(embed.build()).queue(msg -> {
            try (Connection conn = core.getDatabaseFile()) {
                PreparedStatement p1 = conn.prepareStatement(
                        "INSERT INTO discordsrvutils_tickets (TicketID, MessageId, Opened_Category, Closed_Category, Name, ChannelID) " +
                                "VALUES (?, ?, ?, ?, ?, ?)");
                p1.setInt(1, id);
                p1.setLong(2, msg.getIdLong());
                p1.setLong(3, openedCategory);
                p1.setLong(4, closedCategory);
                p1.setString(5, name);
                p1.setLong(6, channel.getIdLong());
                p1.execute();
                for (Long RoleID : roles) {
                    PreparedStatement p2 = conn.prepareStatement(
                            "INSERT INTO discordsrvutils_ticket_allowed_roles (TicketID, RoleID) " +
                                    "VALUES (?, ?)");
                    p2.setInt(1, id);
                    p2.setLong(2, RoleID);
                    p2.execute();
                }
                msg.addReaction("\uD83D\uDCE9").queue();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
      }

      public void deleteMemoryTicketCreation(Long channelID, Long userID) {
        try (Connection mconn = core.getMemoryConnection()) {
            PreparedStatement last = mconn.prepareStatement("DELETE FROM tickets_creating WHERE Channel_id=? AND UserID=?");
            last.setLong(1, channelID);
            last.setLong(2, userID);
            last.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
      }
}
