package tk.bluetree242.discordsrvutils.suggestions.listeners;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.suggestions.SuggestionManager;
import tk.bluetree242.discordsrvutils.tickets.TicketManager;
import tk.bluetree242.discordsrvutils.utils.Emoji;
import tk.bluetree242.discordsrvutils.utils.Utils;

public class SuggestionReactionListener extends ListenerAdapter {


    private DiscordSRVUtils core= DiscordSRVUtils.get();
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (e.getUser().isBot()) return;
        core.handleCF(SuggestionManager.get().getSuggestionByMessageID(e.getMessageIdLong()), suggestion -> {

            Message msg = e.getChannel().retrieveMessageById(e.getMessageIdLong()).complete();
            Emoji yes = Utils.getEmoji(core.getSuggestionsConfig().yes_reaction(), new Emoji("✅"));
            Emoji no = Utils.getEmoji(core.getSuggestionsConfig().no_reaction(), new Emoji("❌"));
            if(!core.getSuggestionsConfig().allow_submitter_vote()) {
            if (e.getUser().getIdLong() == suggestion.getSubmitter()) {
                e.getReaction().removeReaction(e.getUser()).queue();
                return;
            }}
            if (!core.getSuggestionsConfig().allow_both_vote()) {
                if (e.getReactionEmote().getName().equals(yes.getName())) {
                    msg.removeReaction(no.getNameInReaction(), e.getUser()).queue();
                } else if (e.getReactionEmote().getName().equals(no.getName())) {
                    msg.removeReaction(yes.getNameInReaction(), e.getUser()).queue();
                }
            }
        }, error -> {
            error.printStackTrace();
        });
    }
}
