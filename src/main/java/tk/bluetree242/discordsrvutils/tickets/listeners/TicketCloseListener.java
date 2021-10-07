package tk.bluetree242.discordsrvutils.tickets.listeners;


import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.react.MessageReactionAddEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.tickets.TicketManager;

public class TicketCloseListener extends ListenerAdapter {

    private DiscordSRVUtils core = DiscordSRVUtils.get();

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (core.getMainConfig().bungee_mode()) return;
        core.handleCF(TicketManager.get().getTicketByMessageId(e.getMessageIdLong()), ticket -> {
            if (ticket != null) {
                if (e.getUser().isBot()) return;
                if (e.getReactionEmote().getName().equals("\uD83D\uDD12")) {
                    e.getReaction().removeReaction(e.getUser()).queue();
                    if (!ticket.isClosed())
                    ticket.close(e.getUser());
                } else if (e.getReactionEmote().getName().equals("\uD83D\uDDD1️")) {
                    e.getReaction().removeReaction(e.getUser()).queue();
                    if (ticket.isClosed()) {
                        ticket.delete();
                    }
                } if (e.getReactionEmote().getName().equals("\uD83D\uDD13")) {
                    e.getReaction().removeReaction(e.getUser()).queue();
                    if (ticket.isClosed()) {
                        core.handleCF(ticket.reopen(e.getUser()), null, ex -> {core.defaultHandle(ex);});
                    }
                }
            }
        }, null);
    }
}
