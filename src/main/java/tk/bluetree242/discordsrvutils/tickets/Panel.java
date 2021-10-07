package tk.bluetree242.discordsrvutils.tickets;

import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.exceptions.ErrorResponseException;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.ChannelAction;
import github.scarsz.discordsrv.dependencies.jda.internal.utils.Checks;
import org.jetbrains.annotations.Nullable;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;
import tk.bluetree242.discordsrvutils.messages.MessageManager;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import tk.bluetree242.discordsrvutils.utils.KeyGenerator;
import tk.bluetree242.discordsrvutils.utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
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

    public static Map<Long, String> runningProcesses = new HashMap<>();

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
                TextChannel channel = core.getGuild().getTextChannelById(channelId);
                if (channel != null) {
                    channel.retrieveMessageById(getMessageId()).queue(msg -> {msg.delete().queue();});
                }
                core.handleCFOnAnother(getTickets()).forEach(t -> {t.delete();});
                PreparedStatement p3 = conn.prepareStatement("DELETE FROM tickets WHERE ID=?");
                p3.setString(1, id);
                p3.execute();
            }catch (SQLException ex) {
                throw new UnCheckedSQLException(ex);
            }
        });
    }

    public CompletableFuture<Set<Ticket>> getTicketsForUser(User user, boolean includeClosed) {
        return core.completableFuture(() -> {
            try (Connection conn = core.getDatabase()) {
                Set<Ticket> result = new HashSet<>();
                PreparedStatement p1 = conn.prepareStatement("SELECT * FROM tickets WHERE UserID=?");
                p1.setLong(1, user.getIdLong());
                ResultSet r1 = p1.executeQuery();
                while (r1.next()) {
                    if (Utils.getDBoolean(r1.getString("Closed"))) {
                        if (includeClosed)
                            TicketManager.get().getTicket(r1, this);
                    } else {
                        TicketManager.get().getTicket(r1, this);
                    }
                }
                return result;

            } catch (SQLException e) {
                throw new UnCheckedSQLException(e);
            }
        });
    }

    public CompletableFuture<@Nullable Ticket> openTicket(User user) {
        return core.completableFuture(() -> {
            if (user.isBot()) return null;
            try (Connection conn = core.getDatabase()) {
                PreparedStatement check = conn.prepareStatement("SELECT * FROM tickets WHERE UserID=? ORDER BY OpenTime");
                check.setLong(1, user.getIdLong());
                ResultSet r = check.executeQuery();
                while (r.next()) {
                    if (!Utils.getDBoolean(r.getString("Closed"))) {
                        return TicketManager.get().getTicket(r, this);
                    }
                }
                if (runningProcesses.containsKey(user.getIdLong())) return null;
                runningProcesses.put(user.getIdLong(), id);
                ChannelAction<TextChannel> action = core.getGuild().getCategoryById(openedCategory).createTextChannel("ticket-" + user.getName());
                action.addMemberPermissionOverride(user.getIdLong(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE), EnumSet.noneOf(Permission.class));
                for (Long role : allowedRoles) {
                    action.addRolePermissionOverride(role, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE), null);
                }
                action.addPermissionOverride(core.getGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL));
                TextChannel channel = action.complete();
                Message msg = channel.sendMessage(MessageManager.get().getMessage(core.getTicketsConfig().ticket_opened_message(), PlaceholdObjectList.ofArray(
                        new PlaceholdObject(core.getGuild(), "guild"),
                        new PlaceholdObject(core.getGuild().getMember(user), "member"),
                        new PlaceholdObject(user, "user"),
                        new PlaceholdObject(this, "panel"),
                        new PlaceholdObject(core.getGuild(), "guild")
                ),null).build()).complete();
                msg.addReaction("\uD83D\uDD12").queue();
                PreparedStatement p1 = conn.prepareStatement("INSERT INTO tickets (ID, Channel, MessageID, Closed, UserID, OpenTime) VALUES (?, ?, ?, ?, ?, ?)");
                p1.setString(1, id);
                p1.setLong(2, channel.getIdLong());
                p1.setLong(3, msg.getIdLong());
                p1.setString(4, "false");
                p1.setLong(5, user.getIdLong());
                p1.setLong(6, System.currentTimeMillis());
                p1.execute();
                runningProcesses.remove(user.getIdLong());
                return new Ticket(id, user.getIdLong(), channel.getIdLong(), false, this, msg.getIdLong());
            } catch (SQLException e) {
                throw new UnCheckedSQLException(e);
            }
        }).handle((e, x) -> {
            runningProcesses.remove(user.getIdLong());
            return e;
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

    public Panel.Editor getEditor() {
        return new Panel.Editor(this);
    }
    public static class Editor {
        private final DiscordSRVUtils core = DiscordSRVUtils.get();
        private String name;
        private Panel panel;
        private Long channelId;
        private Long openedCategory;
        private Long closedCategory;
        private Set<Long> allowedRoles;

        public Editor(Panel panel) {
            this.panel = panel;
            this.name = panel.name;
            this.channelId = panel.channelId;
            this.openedCategory = panel.openedCategory;
            this.closedCategory = panel.closedCategory;
            this.allowedRoles = panel.allowedRoles;
        }

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
            this.allowedRoles = allowedRoles;
        }

        public CompletableFuture<Panel> apply() {
            return core.completableFuture(() -> {
                try (Connection conn = core.getDatabase()) {
                    Checks.notNull(name, "Name");
                    Checks.notNull(channelId, "Channel");
                    Checks.notNull(openedCategory, "OpenedCategory");
                    Checks.notNull(closedCategory, "ClosedCategory");
                    if (core.getGuild().getCategoryById(openedCategory) == null)
                        throw new IllegalArgumentException("Opened Category was not found");
                    if (core.getGuild().getCategoryById(closedCategory) == null)
                        throw new IllegalArgumentException("Closed Category was not found");
                    TextChannel channel = core.getGuild().getTextChannelById(channelId);
                    if (channel == null) {
                        throw new IllegalArgumentException("Channel was not found");
                    }
                    Message msg;
                    try {
                        if (!panel.name.equals(name)) {
                            msg = channel.sendMessage(MessageManager.get().getMessage(core.getTicketsConfig().panel_message(), PlaceholdObjectList.ofArray(new PlaceholdObject(panel, "panel")), null).build()).complete();
                            msg.addReaction("\uD83C\uDFAB").queue();
                        } else
                            msg = channel.retrieveMessageById(panel.messageId).complete();
                    } catch (ErrorResponseException ex) {
                        msg = channel.sendMessage(MessageManager.get().getMessage(core.getTicketsConfig().panel_message(), PlaceholdObjectList.ofArray(new PlaceholdObject(panel, "panel")), null).build()).complete();
                        msg.addReaction("\uD83C\uDFAB").queue();
                    }
                    PreparedStatement p1 = conn.prepareStatement("UPDATE ticket_panels SET Name=?, Channel=?, MessageID=?, OpenedCategory=?, ClosedCategory=? WHERE ID=?");
                    p1.setString(1, name);
                    p1.setLong(2, channel.getIdLong());
                    p1.setLong(3, msg.getIdLong());
                    p1.setLong(4, openedCategory);
                    p1.setLong(5, closedCategory);
                    p1.setString(6, panel.id);
                    p1.execute();
                    if (!panel.allowedRoles.equals(allowedRoles)) {
                        PreparedStatement p2 = conn.prepareStatement("DELETE FROM panel_allowed_roles WHERE PanelID=?");
                        p2.setString(1, panel.id);
                        p2.execute();
                        for (Long r : allowedRoles) {
                            PreparedStatement p3 = conn.prepareStatement("INSERT INTO panel_allowed_roles(RoleID, PanelID) VALUES (?, ?)");
                            p3.setLong(1, r);
                            p3.setString(2, panel.id);
                            p3.execute();
                        }
                    }
                    panel.messageId = msg.getIdLong();
                    panel.name = name;
                    panel.channelId = channelId;
                    panel.openedCategory = openedCategory;
                    panel.closedCategory = closedCategory;
                    panel.allowedRoles = allowedRoles;
                    return panel;
                } catch (SQLException ex) {
                    throw new UnCheckedSQLException(ex);
                }
            });
        }
    }
}