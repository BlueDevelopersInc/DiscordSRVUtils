package tk.bluetree242.discordsrvutils.tickets.listeners;


import github.scarsz.discordsrv.dependencies.jda.api.events.message.react.MessageReactionAddEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.tickets.TicketManager;

public class PanelReactListener extends ListenerAdapter {
    private DiscordSRVUtils core= DiscordSRVUtils.get();
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        core.handleCF(TicketManager.get().getPanelByMessageId(e.getMessageIdLong()), panel -> {
            if (panel != null) {
                e.getReaction().removeReaction(e.getUser()).queue();
                core.handleCF(panel.openTicket(e.getUser()), null, er -> {core.defaultHandle(er);});
            }
        }, error -> {
            core.defaultHandle(error);
        });
    }

}
