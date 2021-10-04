package tk.bluetree242.discordsrvutils.commands.discord;

import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandCategory;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandType;
import tk.bluetree242.discordsrvutils.tickets.TicketManager;

public class ReopenCommand extends Command {
    public ReopenCommand() {
        super("reopen", CommandType.GUILDS, "Reopen the ticket command executed on", "[P]reopen", null, CommandCategory.TICKETS, "reopenticket");
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        DiscordSRVUtils.get().handleCF(TicketManager.get().getTicketByChannel(e.getChannel().getIdLong()), ticket -> {
            if (ticket == null) {
                e.replyErr("You are not in a ticket").queue();
                return;
            }
            if (!ticket.isClosed()) {
                e.replyErr("Ticket is already opened").queue();
            } else {
                DiscordSRVUtils.get().handleCF(ticket.reopen(e.getAuthor()), null, err -> {DiscordSRVUtils.get().defaultHandle(err);});
            }
        }, err -> {DiscordSRVUtils.get().defaultHandle(err);});
    }
}
