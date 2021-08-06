package tk.bluetree242.discordsrvutils.tickets;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.internal.utils.Checks;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;
import tk.bluetree242.discordsrvutils.messages.MessageManager;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import tk.bluetree242.discordsrvutils.utils.KeyGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class Panel {

    private final DiscordSRVUtils core = DiscordSRVUtils.get();
    private String name;
    private String id;
    private Long messageId;
    private Long channelId;
    private Long openedCategory;
    private Long closedCategory;
    private Set<Long> allowedRoles;

    public Panel(String name, String id, Long messageId, Long channelId, Long openedCategory, Long closedCategory, Set<Long> allowedRoles) {
        this.name = name;
        this.id = id;
        this.messageId = messageId;
        this.channelId = channelId;
        this.openedCategory = openedCategory;
        this.closedCategory = closedCategory;
        this.allowedRoles = allowedRoles;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Long getMessageId() {
        return messageId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public Long getOpenedCategory() {
        return openedCategory;
    }

    public Long getClosedCategory() {
        return closedCategory;
    }

    public Set<Long> getAllowedRoles() {
        return allowedRoles;
    }

    public CompletableFuture<Void> delete() {
        return core.completableFutureRun(() -> {
           try (Connection conn = core.getDatabase()) {
               PreparedStatement p1 = conn.prepareStatement("DELETE FROM ticket_panels WHERE ID=?");
               p1.setString(1, id);
               p1.execute();
               PreparedStatement p2 = conn.prepareStatement("DELETE FROM panel_allowed_roles WHERE PanelID=?");
               p2.setString(1, id);
               p2.execute();
               PreparedStatement p3 = conn.prepareStatement("DELETE FROM ticket_panels WHERE ID=?");
               p3.setString(1, id);
               p3.execute();
           }catch (SQLException ex) {
               throw new UnCheckedSQLException(ex);
           }
        });
    }

    public CompletableFuture<Set<Ticket>> getTickets() {
        return core.completableFuture(() -> {
            try (Connection conn = core.getDatabase()) {
                Set<Ticket> val = new HashSet<>();
                PreparedStatement p1 = conn.prepareStatement("SELECT * FROM tickets WHERE ID=?");
                p1.setString(1, id);
                ResultSet r1 = p1.executeQuery();
                while (r1.next())
                val.add(TicketManager.get().getTicket(r1, this));
                return val;
            }  catch (SQLException e) {
                throw new UnCheckedSQLException(e);
            }
        });
    }


    public static class Builder {
        private final DiscordSRVUtils core = DiscordSRVUtils.get();
        private String name;
        private Long channelId;
        private Long openedCategory;
        private Long closedCategory;
        private Set<Long> allowedRoles = new HashSet<>();


        public void setName(String name) {
            this.name = name;
        }


        public void setChannelId(Long channelId) {
            this.channelId = channelId;
        }

        public void setOpenedCategory(Long openedCategory) {
            this.openedCategory = openedCategory;
        }

        public void setClosedCategory(Long closedCategory) {
            this.closedCategory = closedCategory;
        }

        public void setAllowedRoles(Set<Long> allowedRoles) {
            this.allowedRoles= allowedRoles;
        }


        public CompletableFuture<Panel> create() {
            return core.completableFuture(() -> {
                Checks.notNull(name, "Name");
                Checks.notNull(channelId, "Channel");
                Checks.notNull(openedCategory, "OpenedCategory");
                Checks.notNull(closedCategory, "ClosedCategory");
                if (core.getGuild().getCategoryById(openedCategory) == null) throw new IllegalArgumentException("Opened Category was not found");
                if (core.getGuild().getCategoryById(closedCategory) == null) throw new IllegalArgumentException("Closed Category was not found");
                TextChannel channel = core.getGuild().getTextChannelById(channelId);
                if (channel == null) {
                    throw new IllegalArgumentException("Channel was not found");
                }
                Panel panel = new Panel(name, new KeyGenerator().toString(), null, channelId, openedCategory, closedCategory, allowedRoles);
                Message msg = channel.sendMessage(MessageManager.get().getMessage(core.getTicketsConfig().panel_message(), PlaceholdObjectList.ofArray(new PlaceholdObject(panel, "panel")), null).build()).complete();
                msg.addReaction("\uD83C\uDFAB").queue();
                panel.messageId = msg.getIdLong();
                try (Connection conn = core.getDatabase()) {
                    PreparedStatement p1 = conn.prepareStatement("INSERT INTO ticket_panels(Name, ID, Channel, MessageID, OpenedCategory, ClosedCategory) VALUES (?, ?, ?, ?, ?, ?)");
                    p1.setString(1, name);
                    p1.setString(2, panel.id);
                    p1.setLong(3, channelId);
                    p1.setLong(4, msg.getIdLong());
                    p1.setLong(5, openedCategory);
                    p1.setLong(6, closedCategory);
                    p1.execute();
                    for (Long r : allowedRoles) {
                        PreparedStatement p2 = conn.prepareStatement("INSERT INTO panel_allowed_roles(RoleID, PanelID) VALUES (?, ?)");
                        p2.setLong(1, r);
                        p2.setString(2, panel.id);
                        p2.execute();
                    }
                    return panel;
                } catch (SQLException e) {
                    throw new UnCheckedSQLException(e);
                }
            });
        }
    }
}
