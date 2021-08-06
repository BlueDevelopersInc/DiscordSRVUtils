package tk.bluetree242.discordsrvutils.tickets;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;
import tk.bluetree242.discordsrvutils.messages.MessageManager;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class Ticket {
    private DiscordSRVUtils core = DiscordSRVUtils.get();
    private String id;
    private Long userID;
    private Long channelID;
    private boolean closed;
    private Panel panel;
    private Long messageID;

    public Ticket(String id, Long userID, Long channelID, boolean closed, Panel panel, Long messageID) {
        this.id = id;
        this.userID = userID;
        this.channelID = channelID;
        this.closed = closed;
        this.panel = panel;
        this.messageID = messageID;
    }

    public String getId() {
        return id;
    }

    public Long getUserID() {
        return userID;
    }

    public Long getChannelID() {
        return channelID;
    }

    public boolean isClosed() {
        return closed;
    }
    public Panel getPanel() {
        return panel;
    }
    public long getMessageID() {
        return messageID;
    }


    public CompletableFuture<Void> close(User userWhoClosed) {
        return core.completableFutureRun(() -> {
            try (Connection conn = core.getDatabase()) {
              PreparedStatement p1 = conn.prepareStatement("UPDATE tickets SET Closed='true' WHERE ID=? AND UserID=?");
              p1.setString(1, id);
              p1.setLong(2, userID);
              p1.execute();
              User user = core.getJDA().retrieveUserById(userID).complete();
              Member member = core.getGuild().getMember(user);
              core.getGuild().getTextChannelById(channelID).getManager().setParent(core.getGuild().getCategoryById(panel.getClosedCategory())).setName("ticket-" + user.getName()).queue();
              PermissionOverride override = core.getGuild().getTextChannelById(channelID).getPermissionOverride(member);
              if (override != null) {
                  override.getManager().deny(Permission.VIEW_CHANNEL).queue();
              }
              core.getGuild().getTextChannelById(channelID).sendMessage(MessageManager.get().getMessage(core.getTicketsConfig().ticket_closed_message(), PlaceholdObjectList.ofArray(
                      new PlaceholdObject(userWhoClosed, "user"),
                      new PlaceholdObject(core.getGuild().getMember(userWhoClosed), "member"),
                      new PlaceholdObject(core.getGuild(), "guild")
              ),null).build()).queue();
              Message msg = core.getGuild().getTextChannelById(channelID).sendMessage(new EmbedBuilder().setColor(Color.ORANGE).setDescription("\uD83D\uDDD1️ Delete Ticked").setFooter("More Options coming soon").build()).complete();
              msg.addReaction("\uD83D\uDDD1️").queue();
              messageID = msg.getIdLong();
              PreparedStatement p2 = conn.prepareStatement("UPDATE tickets SET MessageID=? WHERE UserID=? AND ID=?");
              p2.setLong(1, messageID);
              p2.setLong(2, userID);
              p2.setString(3, id);
              p2.execute();

            } catch (SQLException e) {
                throw new UnCheckedSQLException(e);
            }
        });
    }
    public CompletableFuture<Void> delete() {
        return core.completableFutureRun(() -> {
            TextChannel channel = core.getGuild().getTextChannelById(channelID);
            if (channel != null) {
                channel.delete().queue();
            }
        });
    }
}
