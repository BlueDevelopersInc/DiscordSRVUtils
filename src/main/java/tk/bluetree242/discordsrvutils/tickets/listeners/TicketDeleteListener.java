package tk.bluetree242.discordsrvutils.tickets.listeners;


import github.scarsz.discordsrv.dependencies.jda.api.events.channel.text.TextChannelDeleteEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.tickets.Ticket;
import tk.bluetree242.discordsrvutils.tickets.TicketManager;

public class TicketDeleteListener extends ListenerAdapter {

    public void onTextChannelDelete(TextChannelDeleteEvent e) {
            DiscordSRVUtils.get().handleCF(TicketManager.get().getTicketByChannel(e.getChannel().getIdLong()), ticket -> {
               if (ticket != null) {
                   ticket.delete();
               }
            }, failure -> {
                DiscordSRVUtils.get().defaultHandle(failure);
            });
    }
}
