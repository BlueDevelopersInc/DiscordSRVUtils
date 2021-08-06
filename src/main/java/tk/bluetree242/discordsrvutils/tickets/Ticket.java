package tk.bluetree242.discordsrvutils.tickets;

import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;

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


    public CompletableFuture<Void> close() {
        return core.completableFutureRun(() -> {
            try (Connection conn = core.getDatabase()) {
              PreparedStatement p1 = conn.prepareStatement("UPDATE tickets SET Closed='true' WHERE ID=? AND UserID=?");
              p1.setString(1, id);
              p1.setLong(2, userID);
              p1.execute();
              User user = core.getJDA().retrieveUserById(userID).complete();
              Member member = core.getGuild().getMember(user);
              core.getGuild().getTextChannelById(channelID).getManager().setParent(core.getGuild().getCategoryById(panel.getClosedCategory())).setName("ticket-" + user.getName()).queue();
              core.getGuild().getTextChannelById(channelID).getPermissionOverride(member).getManager().deny(Permission.VIEW_CHANNEL).queue();

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
