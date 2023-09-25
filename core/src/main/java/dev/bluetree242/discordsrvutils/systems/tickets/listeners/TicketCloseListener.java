/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2022 BlueTree242
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

package dev.bluetree242.discordsrvutils.systems.tickets.listeners;


import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.ButtonClickEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import dev.bluetree242.discordsrvutils.systems.tickets.Ticket;

@RequiredArgsConstructor
public class TicketCloseListener extends ListenerAdapter {

    private final DiscordSRVUtils core;

    public void onButtonClick(@NotNull ButtonClickEvent e) {
        if (core.getMainConfig().bungee_mode()) return;
        core.getAsyncManager().executeAsync(() -> {
            Ticket ticket = core.getTicketManager().getTicketByMessageId(e.getMessageIdLong());
            if (ticket != null) {
                if (e.getUser().isBot()) return;
                if (e.getButton().getId().equals("close_ticket")) {
                    e.deferEdit().queue();
                    if (!ticket.isClosed())
                        ticket.close(e.getUser());
                } else if (e.getButton().getId().equals("delete_ticket")) {
                    e.deferEdit().queue();
                    if (ticket.isClosed()) {
                        ticket.delete();
                    }
                }
                if (e.getButton().getId().equals("reopen_ticket")) {
                    e.deferEdit().queue();
                    if (ticket.isClosed()) {
                        ticket.reopen(e.getUser());
                    }
                }
            }
        });
    }
}
