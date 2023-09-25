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
import dev.bluetree242.discordsrvutils.jooq.tables.TicketsTable;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import dev.bluetree242.discordsrvutils.systems.tickets.Ticket;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class TicketFirstMessageListener extends ListenerAdapter {
    private final DiscordSRVUtils core;

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent e) {
        if (e.getGuild().getIdLong() != core.getDiscordSRV().getMainGuild().getIdLong()
                || e.getAuthor().isBot() || e.getAuthor().isSystem() || e.getMessage().isWebhookMessage()) return;
        core.getAsyncManager().executeAsync(() -> {
            Ticket ticket = core.getTicketManager().getTicketByChannel(e.getChannel().getIdLong());
            if (ticket == null || ticket.getUserID() == e.getAuthor().getIdLong() || ticket.isClosed() || ticket.isFirstMessage())
                return;
            //store it
            core.getDatabaseManager().jooq().update(TicketsTable.TICKETS)
                    .set(TicketsTable.TICKETS.FIRSTMESSAGE, true)
                    .where(TicketsTable.TICKETS.CHANNEL.eq(e.getChannel().getIdLong()))
                    .execute();
            int delay = core.getTicketsConfig().firstmessage_ping_delay();
            if (delay <= 0) return;
            e.getChannel().sendMessage("<@" + ticket.getUserID() + ">").delay(delay, TimeUnit.SECONDS)
                    .flatMap(Message::delete).queue();
        });
    }
}
