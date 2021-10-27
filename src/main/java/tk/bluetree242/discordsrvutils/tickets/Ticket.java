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

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
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
            if (closed) return;
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
                      override.getManager().deny(Permission.VIEW_CHANNEL).deny(Permission.MESSAGE_WRITE).queue();
                  }
              Message msg = core.getGuild().getTextChannelById(channelID).sendMessage(MessageManager.get().getMessage(core.getTicketsConfig().ticket_closed_message(), PlaceholdObjectList.ofArray(
                      new PlaceholdObject(userWhoClosed, "user"),
                      new PlaceholdObject(core.getGuild().getMember(userWhoClosed), "member"),
                      new PlaceholdObject(core.getGuild(), "guild"),
                      new PlaceholdObject(panel, "panel")
              ),null).build()).setActionRow(
                      Button.success("reopen_ticket", Emoji.fromUnicode("\uD83D\uDD13")).withLabel("Reopen Ticket"),
                      Button.danger("delete_ticket", Emoji.fromUnicode("\uD83D\uDDD1️")).withLabel("Delete Ticket")
                      ).complete();
              messageID = msg.getIdLong();
              PreparedStatement p2 = conn.prepareStatement("UPDATE tickets SET MessageID=?, Closed='true', OpenTime=? WHERE UserID=? AND ID=? ");
              p2.setLong(1, messageID);
                p2.setLong(2, System.currentTimeMillis());
                p2.setLong(3, userID);
                p2.setString(4, id);
                p2.execute();

            } catch (SQLException e) {
                throw new UnCheckedSQLException(e);
            }
        });
    }

    public CompletableFuture<Boolean> reopen(User userWhoOpened) {
        return core.completableFuture(() -> {
            if (!closed) return false;
            try (Connection conn = core.getDatabase()) {
                PreparedStatement p1 = conn.prepareStatement("UPDATE tickets SET Closed='true' WHERE ID=? AND UserID=?");
                p1.setString(1, id);
                p1.setLong(2, userID);
                p1.execute();
                User user = core.getJDA().retrieveUserById(userID).complete();
                Member member = core.getGuild().getMember(user);
                core.getGuild().getTextChannelById(channelID).getManager().setParent(core.getGuild().getCategoryById(panel.getOpenedCategory())).setName("ticket-" + user.getName()).queue();
                PermissionOverride override = core.getGuild().getTextChannelById(channelID).getPermissionOverride(member);
                if (override != null) {
                    override.getManager().setAllow(Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE).queue();
                } else {
                    return false;
                }
                Message msg = core.getGuild().getTextChannelById(channelID).sendMessage(MessageManager.get().getMessage(core.getTicketsConfig().ticket_reopen_message(), PlaceholdObjectList.ofArray(
                        new PlaceholdObject(userWhoOpened, "user"),
                        new PlaceholdObject(core.getGuild().getMember(userWhoOpened), "member"),
                        new PlaceholdObject(core.getGuild(), "guild"),
                        new PlaceholdObject(panel, "panel")
                ), null).build()).setActionRow(Button.danger("close_ticket", Emoji.fromUnicode("\uD83D\uDD12")).withLabel("Close Ticket")).complete();
                messageID = msg.getIdLong();
                PreparedStatement p2 = conn.prepareStatement("UPDATE tickets SET MessageID=?, Closed='false' WHERE UserID=? AND ID=? ");
                p2.setLong(1, messageID);
                p2.setLong(2, userID);
                p2.setString(3, id);
                p2.execute();
                return true;

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