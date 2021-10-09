/*
 *  LICENSE
 *  DiscordSRVUtils
 *  -------------
 *  Copyright (C) 2020 - 2021 BlueTree242
 *  -------------
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program.  If not, see
 *  <http://www.gnu.org/licenses/gpl-3.0.html>.
 *  END
 */

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
        if (core.getMainConfig().bungee_mode()) return;
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
