/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2023 BlueTree242
 * -------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * END
 */

package dev.bluetree242.discordsrvutils.systems.tickets;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.jooq.tables.TicketsTable;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import dev.bluetree242.discordsrvutils.utils.Utils;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import lombok.Getter;
import org.jooq.DSLContext;

public class Ticket {
    private final DiscordSRVUtils core;
    private final String id;
    private final Long userID;
    private final Long channelID;
    private final boolean closed;
    private final Panel panel;
    @Getter
    private final boolean firstMessage;
    private Long messageID;

    public Ticket(DiscordSRVUtils core, String id, Long userID, Long channelID, boolean closed, Panel panel, Long messageID, boolean firstMessage) {
        this.core = core;
        this.id = id;
        this.userID = userID;
        this.channelID = channelID;
        this.closed = closed;
        this.panel = panel;
        this.messageID = messageID;
        this.firstMessage = firstMessage;
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


    public void close(User userWhoClosed) {
        DSLContext conn = core.getDatabaseManager().jooq();
        if (closed) return;
        //set it without the message id
        conn.update(TicketsTable.TICKETS)
                .set(TicketsTable.TICKETS.CLOSED, "true")
                .where(TicketsTable.TICKETS.CHANNEL.eq(channelID))
                .execute();
        User user = core.getJDA().retrieveUserById(userID).complete();
        Member member = Utils.retrieveMember(core.getDiscordSRV().getMainGuild(), userID);
        core.getPlatform().getDiscordSRV().getMainGuild().getTextChannelById(channelID).getManager().setParent(core.getPlatform().getDiscordSRV().getMainGuild().getCategoryById(panel.getClosedCategory())).setName("ticket-" + user.getName()).queue();
        PermissionOverride override = member == null ? null : core.getPlatform().getDiscordSRV().getMainGuild().getTextChannelById(channelID).getPermissionOverride(member);
        if (override != null) {
            override.getManager().deny(Permission.VIEW_CHANNEL).deny(Permission.MESSAGE_WRITE).queue();
        }
        Message msg = core.getPlatform().getDiscordSRV().getMainGuild().getTextChannelById(channelID).sendMessage(core.getMessageManager().getMessage(core.getTicketsConfig().ticket_closed_message(), PlaceholdObjectList.ofArray(core,
                new PlaceholdObject(core, userWhoClosed, "user"),
                new PlaceholdObject(core, core.getPlatform().getDiscordSRV().getMainGuild().getMember(userWhoClosed), "member"),
                new PlaceholdObject(core, core.getPlatform().getDiscordSRV().getMainGuild(), "guild"),
                new PlaceholdObject(core, panel, "panel")
        ), null).build()).setActionRow(
                Button.success("reopen_ticket", Emoji.fromUnicode("\uD83D\uDD13")).withLabel(core.getTicketsConfig().ticket_reopen_button()),
                Button.danger("delete_ticket", Emoji.fromUnicode("\uD83D\uDDD1️")).withLabel(core.getTicketsConfig().delete_ticket_button())
        ).complete();
        messageID = msg.getIdLong();
        //now do it with msg id
        conn.update(TicketsTable.TICKETS)
                .set(TicketsTable.TICKETS.MESSAGEID, messageID)
                .set(TicketsTable.TICKETS.CLOSED, "true")
                .set(TicketsTable.TICKETS.OPENTIME, System.currentTimeMillis())
                .where(TicketsTable.TICKETS.CHANNEL.eq(channelID))
                .execute();
    }

    public void reopen(User userWhoOpened) {
        DSLContext conn = core.getDatabaseManager().jooq();
        if (!closed) return;
        conn.update(TicketsTable.TICKETS)
                .set(TicketsTable.TICKETS.CLOSED, "false")
                .where(TicketsTable.TICKETS.CHANNEL.eq(channelID))
                .execute();
        User user = core.getJDA().retrieveUserById(userID).complete();
        Member member = Utils.retrieveMember(core.getDiscordSRV().getMainGuild(), userID);
        core.getPlatform().getDiscordSRV().getMainGuild().getTextChannelById(channelID).getManager().setParent(core.getPlatform().getDiscordSRV().getMainGuild().getCategoryById(panel.getOpenedCategory())).setName("ticket-" + user.getName()).queue();
        PermissionOverride override = member == null ? null : core.getPlatform().getDiscordSRV().getMainGuild().getTextChannelById(channelID).getPermissionOverride(member);
        if (override != null) {
            override.getManager().setAllow(Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE).queue();
        } else {
            return;
        }
        Message msg = core.getPlatform().getDiscordSRV().getMainGuild().getTextChannelById(channelID).sendMessage(core.getMessageManager().getMessage(core.getTicketsConfig().ticket_reopen_message(), PlaceholdObjectList.ofArray(core,
                new PlaceholdObject(core, userWhoOpened, "user"),
                new PlaceholdObject(core, core.getPlatform().getDiscordSRV().getMainGuild().getMember(userWhoOpened), "member"),
                new PlaceholdObject(core, core.getPlatform().getDiscordSRV().getMainGuild(), "guild"),
                new PlaceholdObject(core, panel, "panel")
        ), null).build()).setActionRow(Button.danger("close_ticket", Emoji.fromUnicode("\uD83D\uDD12")).withLabel(core.getTicketsConfig().ticket_close_button())).complete();
        messageID = msg.getIdLong();
        conn.update(TicketsTable.TICKETS)
                .set(TicketsTable.TICKETS.MESSAGEID, messageID)
                .set(TicketsTable.TICKETS.CLOSED, "false")
                .set(TicketsTable.TICKETS.OPENTIME, System.currentTimeMillis())
                .where(TicketsTable.TICKETS.CHANNEL.eq(channelID))
                .execute();
    }

    public void delete() {
        TextChannel channel = core.getPlatform().getDiscordSRV().getMainGuild().getTextChannelById(channelID);
        if (channel != null) {
            channel.delete().queue();
        }
    }
}