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

package tk.bluetree242.discordsrvutils.systems.suggestions.listeners;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.ButtonClickEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.systems.suggestions.SuggestionManager;
import tk.bluetree242.discordsrvutils.systems.suggestions.SuggestionVote;
import tk.bluetree242.discordsrvutils.utils.Emoji;
import tk.bluetree242.discordsrvutils.utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SuggestionListener extends ListenerAdapter {


    private final DiscordSRVUtils core = DiscordSRVUtils.get();

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (core.getMainConfig().bungee_mode()) return;
        if (e.getUser().isBot()) return;
        core.handleCF(SuggestionManager.get().getSuggestionByMessageID(e.getMessageIdLong()), suggestion -> {

            Message msg = e.getChannel().retrieveMessageById(e.getMessageIdLong()).complete();
            Emoji yes = Utils.getEmoji(core.getSuggestionsConfig().yes_reaction(), new Emoji("✅"));
            Emoji no = Utils.getEmoji(core.getSuggestionsConfig().no_reaction(), new Emoji("❌"));
            if (SuggestionManager.get().loading) {
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
        }, error -> {
            core.defaultHandle(error);
        });
    }

    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent e) {
        core.handleCF(SuggestionManager.get().getSuggestionByMessageID(e.getMessageIdLong()), suggestion -> {
            Message msg = suggestion.getMessage();
            if ((System.currentTimeMillis() - msg.getTimeEdited().toEpochSecond()) > 1000) {
                msg.editMessage(suggestion.getCurrentMsg()).queue();
            }
        }, error -> {
            core.defaultHandle(error);
        });
    }


    public void onButtonClick(ButtonClickEvent e) {
        if (core.getMainConfig().bungee_mode()) return;
        if (e.getUser().isBot()) return;
        core.handleCF(SuggestionManager.get().getSuggestionByMessageID(e.getMessageIdLong()), suggestion -> {

            Message msg = e.getChannel().retrieveMessageById(e.getMessageIdLong()).complete();
            Emoji yes = Utils.getEmoji(core.getSuggestionsConfig().yes_reaction(), new Emoji("✅"));
            Emoji no = Utils.getEmoji(core.getSuggestionsConfig().no_reaction(), new Emoji("❌"));
            if (SuggestionManager.get().loading) {
                return;
            }
            if (!core.getSuggestionsConfig().allow_submitter_vote()) {
                if (e.getUser().getIdLong() == suggestion.getSubmitter()) {
                    e.deferReply(true).setContent("You may not vote your own suggestion").queue();
                    return;
                }
            }
            if (e.getButton().getId().equals("yes")) {
                try (Connection conn = core.getDatabase()) {
                    PreparedStatement p1 = conn.prepareStatement("DELETE FROM suggestions_votes WHERE UserID=? AND SuggestionNumber=?");
                    p1.setLong(1, e.getUser().getIdLong());
                    p1.setInt(2, suggestion.getNumber());
                    p1.execute();
                    p1 = conn.prepareStatement("INSERT INTO suggestions_votes (UserID, SuggestionNumber, Agree) VALUES (?,?,?)");
                    p1.setLong(1, e.getUser().getIdLong());
                    p1.setInt(2, suggestion.getNumber());
                    p1.setString(3, "true");
                    p1.execute();
                    for (SuggestionVote vote : suggestion.getVotes()) {
                        if (vote.getId() == e.getUser().getIdLong()) suggestion.getVotes().remove(vote);
                    }
                    suggestion.getVotes().add(new SuggestionVote(e.getUser().getIdLong(), suggestion.getNumber(), true));
                } catch (SQLException ex) {
                    core.defaultHandle(ex);
                    return;
                }
                e.deferEdit().queue();
            } else if (e.getButton().getId().equals("no")) {
                try (Connection conn = core.getDatabase()) {
                    PreparedStatement p1 = conn.prepareStatement("DELETE FROM suggestions_votes WHERE UserID=? AND SuggestionNumber=?");
                    p1.setLong(1, e.getUser().getIdLong());
                    p1.setInt(2, suggestion.getNumber());
                    p1.execute();
                    p1 = conn.prepareStatement("INSERT INTO suggestions_votes (UserID, SuggestionNumber, Agree) VALUES (?,?,?)");
                    p1.setLong(1, e.getUser().getIdLong());
                    p1.setInt(2, suggestion.getNumber());
                    p1.setString(3, "true");
                    p1.execute();
                    for (SuggestionVote vote : suggestion.getVotes()) {
                        if (vote.getId() == e.getUser().getIdLong()) suggestion.getVotes().remove(vote);
                    }
                    suggestion.getVotes().add(new SuggestionVote(e.getUser().getIdLong(), suggestion.getNumber(), false));

                } catch (SQLException ex) {
                    core.defaultHandle(ex);
                    return;
                }
                e.deferEdit().queue();
            } else if (e.getButton().getId().equals("reset")) {
                try (Connection conn = core.getDatabase()) {
                    PreparedStatement p1 = conn.prepareStatement("DELETE FROM suggestions_votes WHERE UserID=? AND SuggestionNumber=?");
                    p1.setLong(1, e.getUser().getIdLong());
                    p1.setInt(2, suggestion.getNumber());
                    p1.execute();
                    for (SuggestionVote vote : suggestion.getVotes()) {
                        if (vote.getId() == e.getUser().getIdLong()) suggestion.getVotes().remove(vote);
                    }
                    e.deferEdit().queue();
                } catch (SQLException ex) {
                    core.defaultHandle(ex);
                    return;
                }
            }
            msg.editMessage(suggestion.getCurrentMsg()).setActionRows(SuggestionManager.getActionRow(suggestion.getYesCount(), suggestion.getNoCount())).queue();
        }, error -> {
            error.printStackTrace();
        });
    }

    public void onMessageReceived(MessageReceivedEvent e) {
        core.executeAsync(() -> {
            if (e.getAuthor().isBot()) return;
            if (e.getMessage().isWebhookMessage()) return;
            if (!core.getSuggestionsConfig().enabled()) return;
            if (e.getChannel().getIdLong() == core.getSuggestionsConfig().suggestions_channel()) {
                if (core.getSuggestionsConfig().set_suggestion_from_channel()) {
                    e.getMessage().delete().queue();
                    SuggestionManager.get().makeSuggestion(e.getMessage().getContentDisplay(), e.getMessage().getAuthor().getIdLong());
                }
            }
        });
    }


}
