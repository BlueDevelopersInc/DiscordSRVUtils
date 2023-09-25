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
import dev.bluetree242.discordsrvutils.jooq.tables.PanelAllowedRolesTable;
import dev.bluetree242.discordsrvutils.jooq.tables.TicketPanelsTable;
import dev.bluetree242.discordsrvutils.jooq.tables.TicketsTable;
import dev.bluetree242.discordsrvutils.jooq.tables.records.PanelAllowedRolesRecord;
import dev.bluetree242.discordsrvutils.jooq.tables.records.TicketPanelsRecord;
import dev.bluetree242.discordsrvutils.jooq.tables.records.TicketsRecord;
import dev.bluetree242.discordsrvutils.utils.Utils;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Emoji;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.exceptions.ErrorResponseException;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.ActionRow;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class TicketManager {
    private final DiscordSRVUtils core;


    public Panel getPanelById(String id) {
        DSLContext conn = core.getDatabaseManager().jooq();
        TicketPanelsRecord record = conn
                .selectFrom(TicketPanelsTable.TICKET_PANELS)
                .where(TicketPanelsTable.TICKET_PANELS.ID.eq(id))
                .fetchOne();
        if (record == null) return null;
        return getPanel(record);
    }

    public Set<Panel> getPanels(DSLContext conn) {
        List<TicketPanelsRecord> records = conn
                .selectFrom(TicketPanelsTable.TICKET_PANELS)
                .fetch();
        Set<Panel> result = new HashSet<>();
        for (TicketPanelsRecord record : records) {
            result.add(getPanel(record));
        }
        return result;
    }

    public Panel getPanel(TicketPanelsRecord r) {
        Set<Long> allowedRoles = new HashSet<>();
        DSLContext conn = r.configuration().dsl();
        List<PanelAllowedRolesRecord> records = conn
                .selectFrom(PanelAllowedRolesTable.PANEL_ALLOWED_ROLES)
                .where(PanelAllowedRolesTable.PANEL_ALLOWED_ROLES.PANELID.eq(r.getId()))
                .fetch();
        for (PanelAllowedRolesRecord record : records) {
            allowedRoles.add(record.getRoleid());
        }
        return new Panel(core, r.getName(),
                r.getId(),
                r.getMessageid(),
                r.getChannel(),
                r.getOpenedcategory(),
                r.getClosedcategory(),
                allowedRoles);
    }

    public Panel getPanelByMessageId(long messageId) {
        DSLContext conn = core.getDatabaseManager().jooq();
        TicketPanelsRecord record = conn
                .selectFrom(TicketPanelsTable.TICKET_PANELS)
                .where(TicketPanelsTable.TICKET_PANELS.MESSAGEID.eq(messageId))
                .fetchOne();
        if (record == null) return null;
        return getPanel(record);
    }

    public Ticket getTicketByMessageId(long messageId) {
        DSLContext conn = core.getDatabaseManager().jooq();
        TicketsRecord record = conn
                .selectFrom(TicketsTable.TICKETS)
                .where(TicketsTable.TICKETS.MESSAGEID.eq(messageId))
                .fetchOne();
        if (record == null) return null;
        return getTicket(record);
    }

    public Ticket getTicketByChannel(long channelId) {
        DSLContext conn = core.getDatabaseManager().jooq();
        TicketsRecord record = conn
                .selectFrom(TicketsTable.TICKETS)
                .where(TicketsTable.TICKETS.CHANNEL.eq(channelId))
                .fetchOne();
        if (record == null) return null;
        return getTicket(record);
    }

    protected Ticket getTicket(TicketsRecord r, Panel panel) {
        if (panel == null) {
            DSLContext conn = r.configuration().dsl();
            panel = getPanelById(r.getId());
        }
        return new Ticket(core,
                r.getId(),
                r.getUserid(),
                r.getChannel(),
                Utils.getDBoolean(r.getClosed()),
                panel,
                r.getMessageid(), r.getFirstmessage());
    }

    protected Ticket getTicket(TicketsRecord r) {
        return getTicket(r, null);
    }

    public void fixTickets() {
        DSLContext jooq = core.getDatabaseManager().jooq();
        List<TicketsRecord> tickets = jooq
                .selectFrom(TicketsTable.TICKETS)
                .fetch();
        for (TicketsRecord record : tickets) {
            TextChannel channel = core.getPlatform().getDiscordSRV().getMainGuild().getTextChannelById(record.getChannel());
            if (channel == null) {
                jooq.deleteFrom(TicketsTable.TICKETS)
                        .where(TicketsTable.TICKETS.CHANNEL.eq(record.getChannel()))
                        .execute();
            }
        }

        //work with panels
        List<TicketPanelsRecord> panels = jooq
                .selectFrom(TicketPanelsTable.TICKET_PANELS)
                .fetch();
        for (TicketPanelsRecord record : panels) {
            Panel panel = getPanel(record);
            try {
                TextChannel channel = core.getPlatform().getDiscordSRV().getMainGuild().getTextChannelById(panel.getChannelId());
                if (channel == null) {
                    core.getLogger().severe("Ticket panel with ID " + panel.getId() + " (" + panel.getName() + ") channel was deleted. To fix this issue, change the channel or delete this ticket using /editpanel command.");
                    return;
                }
                Message msg = channel.retrieveMessageById(panel.getMessageId()).complete();
                if (msg.getButtons().isEmpty()) {
                    msg.clearReactions().queue();
                    msg.editMessage(msg).setActionRow(Button.secondary("open_ticket", Emoji.fromUnicode("\uD83C\uDFAB")).withLabel(core.getTicketsConfig().open_ticket_button())).queue();
                } else if (!msg.getButtons().get(0).getLabel().equals(core.getTicketsConfig().open_ticket_button())) {
                    msg.editMessage(msg).setActionRows(ActionRow.of(Button.secondary("open_ticket", Emoji.fromUnicode("\uD83C\uDFAB")).withLabel(core.getTicketsConfig().open_ticket_button()))).queue();
                }
            } catch (ErrorResponseException ex) {
                panel.getEditor().apply(jooq);
            }
        }
    }

}