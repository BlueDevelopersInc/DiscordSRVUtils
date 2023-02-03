/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2023 BlueTree242
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

package tk.bluetree242.discordsrvutils.systems.suggestions.listeners;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.ButtonClickEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.jooq.tables.SuggestionsVotesTable;
import tk.bluetree242.discordsrvutils.systems.suggestions.Suggestion;
import tk.bluetree242.discordsrvutils.systems.suggestions.SuggestionManager;
import tk.bluetree242.discordsrvutils.systems.suggestions.SuggestionVote;
import tk.bluetree242.discordsrvutils.utils.Emoji;
import tk.bluetree242.discordsrvutils.utils.Utils;

@RequiredArgsConstructor
public class SuggestionListener extends ListenerAdapter {


    private final DiscordSRVUtils core;

    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent e) {
        if (core.getMainConfig().bungee_mode()) return;
        if (e.getUser().isBot()) return;
        core.getAsyncManager().executeAsync(() -> {
            Suggestion suggestion = core.getSuggestionManager().getSuggestionByMessageID(e.getMessageIdLong());
            if (suggestion == null) return;
            Message msg = e.getChannel().retrieveMessageById(e.getMessageIdLong()).complete();
            Emoji yes = Utils.getEmoji(core.getSuggestionsConfig().yes_reaction(), new Emoji("✅"));
            Emoji no = Utils.getEmoji(core.getSuggestionsConfig().no_reaction(), new Emoji("❌"));
            if (core.getSuggestionManager().loading) {
                e.getReaction().removeReaction(e.getUser()).queue();
                return;
            }
            if (!core.getSuggestionsConfig().allow_submitter_vote()) {
                if (e.getUser().getIdLong() == suggestion.getSubmitter()) {
                    e.getReaction().removeReaction(e.getUser()).queue();
                    return;
                }
            }

            if (e.getReactionEmote().getName().equals(yes.getName())) {
                msg.removeReaction(no.getNameInReaction(), e.getUser()).queue();
            } else if (e.getReactionEmote().getName().equals(no.getName())) {
                msg.removeReaction(yes.getNameInReaction(), e.getUser()).queue();
            }
            msg.editMessage(suggestion.getCurrentMsg()).queue();
        });
    }

    public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent e) {
        if (core.getMainConfig().bungee_mode()) return;
        core.getAsyncManager().executeAsync(() -> {
            Suggestion suggestion = core.getSuggestionManager().getSuggestionByMessageID(e.getMessageIdLong());
            if (suggestion == null) return;
            Message msg = suggestion.getMessage();
            if (!msg.isEdited() || (System.currentTimeMillis() - msg.getTimeEdited().toEpochSecond()) > 1000) {
                msg.editMessage(suggestion.getCurrentMsg()).queue();
            }
        });
    }


    public void onButtonClick(@NotNull ButtonClickEvent e) {
        if (core.getMainConfig().bungee_mode()) return;
        if (e.getUser().isBot()) return;
        core.getAsyncManager().executeAsync(() -> {
            DSLContext jooq = core.getDatabaseManager().jooq();
            Suggestion suggestion = core.getSuggestionManager().getSuggestionByMessageID(e.getMessageIdLong());
            if (suggestion == null) return;
            Message msg = e.getChannel().retrieveMessageById(e.getMessageIdLong()).complete();
            Emoji yes = Utils.getEmoji(core.getSuggestionsConfig().yes_reaction(), new Emoji("✅"));
            Emoji no = Utils.getEmoji(core.getSuggestionsConfig().no_reaction(), new Emoji("❌"));
            if (core.getSuggestionManager().loading) {
                return;
            }
            if (!core.getSuggestionsConfig().allow_submitter_vote()) {
                if (e.getUser().getIdLong() == suggestion.getSubmitter()) {
                    e.deferReply(true).setContent(core.getSuggestionsConfig().vote_own_suggestion_message()).queue();
                    return;
                }
            }
            jooq.deleteFrom(SuggestionsVotesTable.SUGGESTIONS_VOTES)
                    .where(SuggestionsVotesTable.SUGGESTIONS_VOTES.USERID.eq(e.getUser().getIdLong()))
                    .and(SuggestionsVotesTable.SUGGESTIONS_VOTES.SUGGESTIONNUMBER.eq((long) suggestion.getNumber()))
                    .execute();
            switch (e.getButton().getId()) {
                case "yes":
                    jooq.insertInto(SuggestionsVotesTable.SUGGESTIONS_VOTES)
                            .set(SuggestionsVotesTable.SUGGESTIONS_VOTES.USERID, e.getUser().getIdLong())
                            .set(SuggestionsVotesTable.SUGGESTIONS_VOTES.SUGGESTIONNUMBER, (long) suggestion.getNumber())
                            .set(SuggestionsVotesTable.SUGGESTIONS_VOTES.AGREE, "true")
                            .execute();
                    suggestion.getVotes().removeIf(vote -> vote.getId() == e.getUser().getIdLong());
                    suggestion.getVotes().add(new SuggestionVote(e.getUser().getIdLong(), suggestion.getNumber(), true));
                    e.deferEdit().queue();
                    break;
                case "no":
                    jooq.insertInto(SuggestionsVotesTable.SUGGESTIONS_VOTES)
                            .set(SuggestionsVotesTable.SUGGESTIONS_VOTES.USERID, e.getUser().getIdLong())
                            .set(SuggestionsVotesTable.SUGGESTIONS_VOTES.SUGGESTIONNUMBER, (long) suggestion.getNumber())
                            .set(SuggestionsVotesTable.SUGGESTIONS_VOTES.AGREE, "false")
                            .execute();
                    suggestion.getVotes().removeIf(vote -> vote.getId() == e.getUser().getIdLong());
                    suggestion.getVotes().add(new SuggestionVote(e.getUser().getIdLong(), suggestion.getNumber(), false));

                    e.deferEdit().queue();
                    break;
                case "reset":
                    suggestion.getVotes().removeIf(vote -> vote.getId() == e.getUser().getIdLong());
                    e.deferEdit().queue();
                    break;
            }
            msg.editMessage(suggestion.getCurrentMsg()).setActionRows(SuggestionManager.getActionRow(suggestion.getYesCount(), suggestion.getNoCount())).queue();
        });
    }

    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        core.getAsyncManager().executeAsync(() -> {
            if (e.getAuthor().isBot()) return;
            if (e.getMessage().isWebhookMessage()) return;
            if (!core.getSuggestionsConfig().enabled()) return;
            if (e.getChannel().getIdLong() == core.getSuggestionsConfig().suggestions_channel()) {
                if (core.getSuggestionsConfig().set_suggestion_from_channel()) {
                    e.getMessage().delete().queue();
                    core.getSuggestionManager().makeSuggestion(e.getMessage().getContentDisplay(), e.getMessage().getAuthor().getIdLong());
                }
            }
        });
    }


}
