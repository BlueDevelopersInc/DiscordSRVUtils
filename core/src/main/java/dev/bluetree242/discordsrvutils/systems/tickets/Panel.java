/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2024 BlueTree242
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
import dev.bluetree242.discordsrvutils.jooq.tables.records.TicketsRecord;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import dev.bluetree242.discordsrvutils.utils.KeyGenerator;
import dev.bluetree242.discordsrvutils.utils.Utils;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Emoji;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.exceptions.ErrorResponseException;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.ChannelAction;
import github.scarsz.discordsrv.dependencies.jda.internal.utils.Checks;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;

import java.util.*;

public class Panel {

    public static Map<Long, String> runningProcesses = new HashMap<>();
    private final DiscordSRVUtils core;
    @Getter
    private final String id;
    @Getter
    private String name;
    @Getter
    private Long messageId;
    @Getter
    private Long channelId;
    @Getter
    private Long openedCategory;
    @Getter
    private Long closedCategory;
    @Getter
    private Set<Long> allowedRoles;

    public Panel(DiscordSRVUtils core, String name, String id, Long messageId, Long channelId, Long openedCategory, Long closedCategory, Set<Long> allowedRoles) {
        this.core = core;
        this.name = name;
        this.id = id;
        this.messageId = messageId;
        this.channelId = channelId;
        this.openedCategory = openedCategory;
        this.closedCategory = closedCategory;
        this.allowedRoles = allowedRoles;
    }

    private static void addAllowedRoles(DSLContext conn, Set<Long> allowedRoles, Panel panel) {
        InsertSetMoreStep query = null;
        if (allowedRoles.isEmpty()) return;
        for (Long r : allowedRoles) {
            if (query == null) query = conn.insertInto(PanelAllowedRolesTable.PANEL_ALLOWED_ROLES)
                    .set(PanelAllowedRolesTable.PANEL_ALLOWED_ROLES.PANELID, panel.id)
                    .set(PanelAllowedRolesTable.PANEL_ALLOWED_ROLES.ROLEID, r);
            else query = query.newRecord()
                    .set(PanelAllowedRolesTable.PANEL_ALLOWED_ROLES.PANELID, panel.id)
                    .set(PanelAllowedRolesTable.PANEL_ALLOWED_ROLES.ROLEID, r);
        }
        query.execute();
    }

    public void delete(DSLContext conn) {
        conn.deleteFrom(TicketPanelsTable.TICKET_PANELS)
                .where(TicketPanelsTable.TICKET_PANELS.ID.eq(id))
                .execute();
        conn.deleteFrom(PanelAllowedRolesTable.PANEL_ALLOWED_ROLES)
                .where(PanelAllowedRolesTable.PANEL_ALLOWED_ROLES.PANELID.eq(id))
                .execute();
        TextChannel channel = core.getPlatform().getDiscordSRV().getMainGuild().getTextChannelById(channelId);
        if (channel != null) {
            channel.retrieveMessageById(getMessageId()).queue(msg -> msg.delete().queue());
        }
        getTickets(conn).forEach(Ticket::delete);
    }

    public Set<Ticket> getTicketsForUser(User user, boolean includeClosed) {
        DSLContext conn = core.getDatabaseManager().jooq();
        Set<Ticket> result = new HashSet<>();
        List<TicketsRecord> records = conn
                .selectFrom(TicketsTable.TICKETS)
                .where(TicketsTable.TICKETS.ID.eq(id))
                .and(TicketsTable.TICKETS.USERID.eq(user.getIdLong()))
                .fetch();
        for (TicketsRecord record : records) {
            if (Utils.getDBoolean(record.getClosed())) {
                if (includeClosed)
                    core.getTicketManager().getTicket(record, this);
            } else {
                core.getTicketManager().getTicket(record, this);
            }
        }
        return result;
    }

    public @Nullable Ticket openTicket(User user) {
        DSLContext conn = core.getDatabaseManager().jooq();
        if (user.isBot()) return null;
        TicketsRecord check = conn
                .selectFrom(TicketsTable.TICKETS)
                .where(TicketsTable.TICKETS.USERID.eq(user.getIdLong()))
                .and(TicketsTable.TICKETS.CLOSED.eq("false"))
                .orderBy(TicketsTable.TICKETS.OPENTIME)
                .fetchOne();
        if (check != null) {
            return core.getTicketManager().getTicket(check, this);
        }
        if (runningProcesses.containsKey(user.getIdLong())) return null;
        runningProcesses.put(user.getIdLong(), id);
        try {
            ChannelAction<TextChannel> action = core.getPlatform().getDiscordSRV().getMainGuild().getCategoryById(openedCategory).createTextChannel("ticket-" + user.getName());
            action.addMemberPermissionOverride(user.getIdLong(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE), EnumSet.noneOf(Permission.class));
            for (Long role : allowedRoles) {
                action.addRolePermissionOverride(role, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_WRITE), null);
            }
            action.addPermissionOverride(core.getPlatform().getDiscordSRV().getMainGuild().getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL));
            TextChannel channel = action.complete();
            Message msg = channel.sendMessage(core.getMessageManager().getMessage(core.getTicketsConfig().ticket_opened_message(), PlaceholdObjectList.ofArray(core,
                    new PlaceholdObject(core, core.getPlatform().getDiscordSRV().getMainGuild(), "guild"),
                    new PlaceholdObject(core, core.getPlatform().getDiscordSRV().getMainGuild().getMember(user), "member"),
                    new PlaceholdObject(core, user, "user"),
                    new PlaceholdObject(core, this, "panel"),
                    new PlaceholdObject(core, core.getPlatform().getDiscordSRV().getMainGuild(), "guild")
            ), null).build()).setActionRow(Button.danger("close_ticket", Emoji.fromUnicode("\uD83D\uDD12")).withLabel(core.getTicketsConfig().ticket_close_button())).complete();
            conn.insertInto(TicketsTable.TICKETS)
                    .set(TicketsTable.TICKETS.ID, id)
                    .set(TicketsTable.TICKETS.CHANNEL, channel.getIdLong())
                    .set(TicketsTable.TICKETS.MESSAGEID, msg.getIdLong())
                    .set(TicketsTable.TICKETS.CLOSED, "false")
                    .set(TicketsTable.TICKETS.USERID, user.getIdLong())
                    .set(TicketsTable.TICKETS.OPENTIME, System.currentTimeMillis())
                    .set(TicketsTable.TICKETS.FIRSTMESSAGE, false)
                    .execute();
            runningProcesses.remove(user.getIdLong());
            return new Ticket(core, id, user.getIdLong(), channel.getIdLong(), false, this, msg.getIdLong(), false);
        } finally {
            runningProcesses.remove(user.getIdLong());
        }
    }

    public Panel.Editor getEditor() {
        return new Panel.Editor(core, this);
    }

    public Set<Ticket> getTickets(DSLContext conn) {
        Set<Ticket> result = new HashSet<>();
        List<TicketsRecord> records = conn
                .selectFrom(TicketsTable.TICKETS)
                .where(TicketsTable.TICKETS.ID.eq(id))
                .fetch();
        for (TicketsRecord record : records)
            result.add(core.getTicketManager().getTicket(record, this));
        return result;
    }

    @RequiredArgsConstructor
    public static class Builder {
        private final DiscordSRVUtils core;
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
            this.allowedRoles = allowedRoles;
        }


        public Panel create(DSLContext conn) {
            Checks.notNull(name, "Name");
            Checks.notNull(channelId, "Channel");
            Checks.notNull(openedCategory, "OpenedCategory");
            Checks.notNull(closedCategory, "ClosedCategory");
            if (core.getPlatform().getDiscordSRV().getMainGuild().getCategoryById(openedCategory) == null)
                throw new IllegalArgumentException("Opened Category was not found");
            if (core.getPlatform().getDiscordSRV().getMainGuild().getCategoryById(closedCategory) == null)
                throw new IllegalArgumentException("Closed Category was not found");
            TextChannel channel = core.getPlatform().getDiscordSRV().getMainGuild().getTextChannelById(channelId);
            if (channel == null) {
                throw new IllegalArgumentException("Channel was not found");
            }
            Panel panel = new Panel(core, name, new KeyGenerator().toString(), null, channelId, openedCategory, closedCategory, allowedRoles);
            Message msg = channel.sendMessage(core.getMessageManager().getMessage(core.getTicketsConfig().panel_message(), PlaceholdObjectList.ofArray(core, new PlaceholdObject(core, panel, "panel")), null).build()).setActionRow(Button.primary("open_ticket", Emoji.fromUnicode("📩")).withLabel(core.getTicketsConfig().open_ticket_button())).complete();
            panel.messageId = msg.getIdLong();
            conn.insertInto(TicketPanelsTable.TICKET_PANELS)
                    .set(TicketPanelsTable.TICKET_PANELS.NAME, name)
                    .set(TicketPanelsTable.TICKET_PANELS.ID, panel.id)
                    .set(TicketPanelsTable.TICKET_PANELS.CHANNEL, channelId)
                    .set(TicketPanelsTable.TICKET_PANELS.MESSAGEID, msg.getIdLong())
                    .set(TicketPanelsTable.TICKET_PANELS.OPENEDCATEGORY, openedCategory)
                    .set(TicketPanelsTable.TICKET_PANELS.CLOSEDCATEGORY, closedCategory)
                    .execute();
            addAllowedRoles(conn, allowedRoles, panel);
            return panel;
        }
    }

    public static class Editor {
        private final DiscordSRVUtils core;
        private final Panel panel;
        private String name;
        private Long channelId;
        private Long openedCategory;
        private Long closedCategory;
        private Set<Long> allowedRoles;

        public Editor(DiscordSRVUtils core, Panel panel) {
            this.core = core;
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

        public Panel apply(DSLContext conn) {
            Checks.notNull(name, "Name");
            Checks.notNull(channelId, "Channel");
            Checks.notNull(openedCategory, "OpenedCategory");
            Checks.notNull(closedCategory, "ClosedCategory");
            if (core.getPlatform().getDiscordSRV().getMainGuild().getCategoryById(openedCategory) == null)
                throw new IllegalArgumentException("Opened Category was not found");
            if (core.getPlatform().getDiscordSRV().getMainGuild().getCategoryById(closedCategory) == null)
                throw new IllegalArgumentException("Closed Category was not found");
            TextChannel channel = core.getPlatform().getDiscordSRV().getMainGuild().getTextChannelById(channelId);
            if (channel == null) {
                throw new IllegalArgumentException("Channel was not found");
            }
            Message msg;
            try {
                if (!panel.name.equals(name)) {
                    msg = channel.sendMessage(core.getMessageManager().getMessage(core.getTicketsConfig().panel_message(), PlaceholdObjectList.ofArray(core, new PlaceholdObject(core, panel, "panel")), null).build()).setActionRow(Button.primary("open_ticket", Emoji.fromUnicode("📩")).withLabel(core.getTicketsConfig().open_ticket_button())).complete();
                } else
                    msg = channel.retrieveMessageById(panel.messageId).complete();
            } catch (ErrorResponseException ex) {
                msg = channel.sendMessage(core.getMessageManager().getMessage(core.getTicketsConfig().panel_message(), PlaceholdObjectList.ofArray(core, new PlaceholdObject(core, panel, "panel")), null).build()).setActionRow(Button.primary("open_ticket", Emoji.fromUnicode("📩")).withLabel(core.getTicketsConfig().open_ticket_button())).complete();
            }
            conn.update(TicketPanelsTable.TICKET_PANELS)
                    .set(TicketPanelsTable.TICKET_PANELS.NAME, name)
                    .set(TicketPanelsTable.TICKET_PANELS.CHANNEL, channelId)
                    .set(TicketPanelsTable.TICKET_PANELS.MESSAGEID, msg.getIdLong())
                    .set(TicketPanelsTable.TICKET_PANELS.OPENEDCATEGORY, openedCategory)
                    .set(TicketPanelsTable.TICKET_PANELS.CLOSEDCATEGORY, closedCategory)
                    .where(TicketPanelsTable.TICKET_PANELS.ID.eq(panel.id))
                    .execute();
            if (!panel.allowedRoles.equals(allowedRoles)) {
                conn.deleteFrom(PanelAllowedRolesTable.PANEL_ALLOWED_ROLES)
                        .where(PanelAllowedRolesTable.PANEL_ALLOWED_ROLES.PANELID.eq(panel.id))
                        .execute();
                addAllowedRoles(conn, allowedRoles, panel);
            }
            panel.messageId = msg.getIdLong();
            panel.name = name;
            panel.channelId = channelId;
            panel.openedCategory = openedCategory;
            panel.closedCategory = closedCategory;
            panel.allowedRoles = allowedRoles;
            return panel;
        }
    }
}