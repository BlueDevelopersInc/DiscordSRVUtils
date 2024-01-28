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

package dev.bluetree242.discordsrvutils.commands.discord.tickets;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.systems.commands.discord.CommandCategory;
import dev.bluetree242.discordsrvutils.systems.commands.discord.CommandEvent;
import dev.bluetree242.discordsrvutils.systems.tickets.Ticket;

public class CloseCommand extends TicketCommand {
    public CloseCommand(DiscordSRVUtils core) {
        super(core, "close", "Close the ticket command is executed on", "", null, CommandCategory.TICKETS, "closeticket");
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        Ticket ticket = core.getTicketManager().getTicketByChannel(e.getChannel().getIdLong());
        if (ticket == null) {
            e.replyErr("You are not in a ticket").queue();
            return;
        }
        if (ticket.isClosed()) {
            e.replyErr("Ticket is already closed").queue();
        } else {
            if (ticket.getUserID() == e.getAuthor().getIdLong() && core.getTicketsConfig().block_self_close() && !core.getJdaManager().isAdmin(ticket.getUserID())) {
                e.reply("You cannot close your own ticket.").setEphemeral(true).queue();
                return;
            }
            e.reply("Closing Ticket...").setEphemeral(true).queue();
            ticket.close(e.getAuthor());
        }
    }
}