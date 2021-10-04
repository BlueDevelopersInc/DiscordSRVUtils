package tk.bluetree242.discordsrvutils.commands.discord;

import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandCategory;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandType;
import tk.bluetree242.discordsrvutils.tickets.TicketManager;

public class CloseCommand extends Command {
    public CloseCommand() {
        super("close", CommandType.GUILDS, "Close the ticket command executed on", "[P]close", null, CommandCategory.TICKETS, "closeticket");
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        DiscordSRVUtils.get().handleCF(TicketManager.get().getTicketByChannel(e.getChannel().getIdLong()), ticket -> {
            if (ticket == null) {
                e.replyErr("You are not in a ticket").queue();
                return;
            }
            if (ticket.isClosed()) {
                e.replyErr("Ticket is already closed").queue();
            } else {
                DiscordSRVUtils.get().handleCF(ticket.close(e.getAuthor()), null, err -> {DiscordSRVUtils.get().defaultHandle(err);});
            }
        }, err -> {DiscordSRVUtils.get().defaultHandle(err);});
    }
}
