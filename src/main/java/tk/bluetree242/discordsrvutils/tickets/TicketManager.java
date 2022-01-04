/*
 *  LICENSE
 *  DiscordSRVUtils
 *  -------------
 *  Copyright (C) 2020 - 2021 BlueTree242
 *  -------------
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program.  If not, see
 *  <http://www.gnu.org/licenses/gpl-3.0.html>.
 *  END
 */

package tk.bluetree242.discordsrvutils.tickets;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Emoji;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.exceptions.ErrorResponseException;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.ActionRow;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;
import tk.bluetree242.discordsrvutils.utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class TicketManager {
    private static TicketManager main;
    private DiscordSRVUtils core = DiscordSRVUtils.get();

    public TicketManager() {
        main = this;
    }

    public static TicketManager get() {
        return main;
    }

    public static TicketManager getInstance() {
        return get();
    }

    public CompletableFuture<Panel> getPanelById(String id) {
        return core.completableFuture(() -> {
            try (Connection conn = core.getDatabase()) {
                PreparedStatement p1 = conn.prepareStatement("SELECT * FROM ticket_panels WHERE ID=?");
                p1.setString(1, id);
                ResultSet r1 = p1.executeQuery();
                if (!r1.next()) {
                    return null;
                }
                return getPanel(r1);
            } catch (SQLException ex) {
                throw new UnCheckedSQLException(ex);
            }
        });
    }

    public CompletableFuture<Set<Panel>> getPanels() {
        return core.completableFuture(() -> {
            try (Connection conn = core.getDatabase()) {
                PreparedStatement p1 = conn.prepareStatement("SELECT * FROM ticket_panels");
                ResultSet r1 = p1.executeQuery();
                Set<Panel> val = new HashSet<>();
                while (r1.next()) {
                    val.add(getPanel(r1));
                }
                return val;
            } catch (SQLException ex) {
                throw new UnCheckedSQLException(ex);
            }
        });
    }

    public Panel getPanel(ResultSet r) throws SQLException {
        Set<Long> allowedRoles = new HashSet<>();
        PreparedStatement p = r.getStatement().getConnection().prepareStatement("SELECT * FROM panel_allowed_roles WHERE PanelID=?");
        p.setString(1, r.getString("ID"));
        ResultSet r2 = p.executeQuery();
        while (r2.next()) {
            allowedRoles.add(r2.getLong("RoleID"));
        }
        return new Panel(r.getString("Name"),
                r.getString("ID"),
                r.getLong("MessageID"),
                r.getLong("Channel"),
                r.getLong("OpenedCategory"),
                r.getLong("ClosedCategory"),
                allowedRoles);
    }

    public CompletableFuture<Panel> getPanelByMessageId(long messageId) {
        return core.completableFuture(() -> {
            try (Connection conn = core.getDatabase()) {
                PreparedStatement p1 = conn.prepareStatement("SELECT * FROM ticket_panels WHERE MessageID=?");
                p1.setLong(1, messageId);
                ResultSet r1 = p1.executeQuery();
                if (!r1.next()) {
                    return null;
                }
                return getPanel(r1);
            } catch (SQLException e) {
                throw new UnCheckedSQLException(e);
            }
        });
    }

    public CompletableFuture<Ticket> getTicketByMessageId(long messageId) {
        return core.completableFuture(() -> {
            try (Connection conn = core.getDatabase()) {
                PreparedStatement p1 = conn.prepareStatement("SELECT * FROM tickets WHERE MessageID=?");
                p1.setLong(1, messageId);
                ResultSet r1 = p1.executeQuery();
                if (!r1.next()) {
                    return null;
                }
                return getTicket(r1);
            } catch (SQLException e) {
                throw new UnCheckedSQLException(e);
            }
        });
    }

    public CompletableFuture<Ticket> getTicketByChannel(long channelId) {
        return core.completableFuture(() -> {
            try (Connection conn = core.getDatabase()) {
                PreparedStatement p1 = conn.prepareStatement("SELECT * FROM tickets WHERE Channel=?");
                p1.setLong(1, channelId);
                ResultSet r1 = p1.executeQuery();
                if (!r1.next()) {
                    return null;
                }
                return getTicket(r1);
            } catch (SQLException e) {
                throw new UnCheckedSQLException(e);
            }
        });
    }

    protected Ticket getTicket(ResultSet r, Panel panel) throws SQLException {
        if (panel == null) {
            PreparedStatement p = r.getStatement().getConnection().prepareStatement("SELECT * FROM ticket_panels WHERE ID=?");
            p.setString(1, r.getString("ID"));
            ResultSet r1 = p.executeQuery();
            if (r1.next()) panel = getPanel(r1);
        }
        return new Ticket(r.getString("ID"), r.getLong("UserID"), r.getLong("Channel"), Utils.getDBoolean(r.getString("Closed")), panel, r.getLong("MessageID"));
    }

    protected Ticket getTicket(ResultSet r) throws SQLException {
        return getTicket(r, null);
    }

    public void fixTickets() {
        try (Connection conn = core.getDatabase()) {
            PreparedStatement p1 = conn.prepareStatement("SELECT * FROM tickets");
            ResultSet r1 = p1.executeQuery();
            while (r1.next()) {
                TextChannel channel = core.getGuild().getTextChannelById(r1.getLong("Channel"));
                if (channel == null) {
                    PreparedStatement p = conn.prepareStatement("DELETE FROM tickets WHERE Channel=?");
                    p.setLong(1, r1.getLong("Channel"));
                    p.execute();
                }
            }
            p1 = conn.prepareStatement("SELECT * FROM ticket_panels");
            r1 = p1.executeQuery();
            while (r1.next()) {
                Panel panel = TicketManager.get().getPanel(r1);
                try {
                    Message msg = core.getGuild().getTextChannelById(panel.getChannelId()).retrieveMessageById(panel.getMessageId()).complete();
                    if (msg.getButtons().isEmpty()) {
                        msg.clearReactions().queue();
                        msg.editMessage(msg).setActionRow(Button.secondary("open_ticket", Emoji.fromUnicode("\uD83C\uDFAB")).withLabel(core.getTicketsConfig().open_ticket_button())).queue();
                    } else if (!msg.getButtons().get(0).getLabel().equals(core.getTicketsConfig().open_ticket_button())) {
                        msg.editMessage(msg).setActionRows(ActionRow.of(Button.secondary("open_ticket", Emoji.fromUnicode("\uD83C\uDFAB")).withLabel(core.getTicketsConfig().open_ticket_button()))).queue();
                    }
                } catch (ErrorResponseException ex) {
                    panel.getEditor().apply();
                }
            }
        } catch (SQLException e) {
            throw new UnCheckedSQLException(e);
        }
    }

}