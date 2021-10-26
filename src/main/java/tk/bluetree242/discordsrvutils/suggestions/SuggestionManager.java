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

package tk.bluetree242.discordsrvutils.suggestions;

import github.scarsz.discordsrv.dependencies.jda.api.MessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.ActionRow;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;
import tk.bluetree242.discordsrvutils.messages.MessageManager;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import tk.bluetree242.discordsrvutils.utils.Emoji;
import tk.bluetree242.discordsrvutils.utils.SuggestionVoteMode;
import tk.bluetree242.discordsrvutils.utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SuggestionManager {

    private static SuggestionManager main;
    private DiscordSRVUtils core = DiscordSRVUtils.get();
    public boolean loading = false;

    public static SuggestionManager get() {
        return main;
    }

    public SuggestionManager() {
        main = this;
    }

    public CompletableFuture<Suggestion> getSuggestionByNumber(int num) {
        return core.completableFuture(() -> {
            try (Connection conn = core.getDatabase()) {
                return getSuggestionByNumber(num, conn);
            } catch (SQLException e) {
                throw new UnCheckedSQLException(e);
            }
        });
    }



    public CompletableFuture<Suggestion> getSuggestionByMessageID(Long MessageID) {
        return core.completableFuture(() -> {
            try (Connection conn = core.getDatabase()) {
                return getSuggestionByMessageID(MessageID, conn);
            } catch (SQLException e) {
                throw new UnCheckedSQLException(e);
            }
        });
    }

    public Suggestion getSuggestionByNumber(int number, Connection conn) throws SQLException{
        PreparedStatement p = conn.prepareStatement("SELECT * FROM suggestions WHERE SuggestionNumber=?");
        p.setInt(1, number);
        ResultSet r = p.executeQuery();
        if (!r.next()) return null;
        return getSuggestion(r);
    }

    public Suggestion getSuggestionByMessageID(Long MessageID, Connection conn) throws SQLException{
        PreparedStatement p = conn.prepareStatement("SELECT * FROM suggestions WHERE MessageID=?");
        p.setLong(1, MessageID);
        ResultSet r = p.executeQuery();
        if (!r.next()) return null;
        return getSuggestion(r);
    }


    public Suggestion getSuggestion(ResultSet r) throws SQLException {
        return getSuggestion(r, null, null);
    }

    public Suggestion getSuggestion(ResultSet r, ResultSet notesr, ResultSet votesr) throws SQLException {
        if (notesr == null) {
            PreparedStatement p1 = r.getStatement().getConnection().prepareStatement("SELECT * FROM suggestion_notes WHERE SuggestionNumber=?");
            p1.setInt(1, r.getInt("SuggestionNumber"));
            notesr = p1.executeQuery();
        }
        if (core.voteMode != SuggestionVoteMode.REACTIONS)
        if (votesr == null) {
            PreparedStatement p1 = r.getStatement().getConnection().prepareStatement("SELECT * FROM suggestions_votes WHERE SuggestionNumber=?");
            p1.setInt(1, r.getInt("SuggestionNumber"));
            votesr = p1.executeQuery();
        }
        Set<SuggestionNote> notes = new HashSet<>();
        Set<SuggestionVote> votes = new HashSet<>();
        while (notesr.next()) {
            notes.add(new SuggestionNote(
                    notesr.getLong("StaffID"),
                    Utils.b64Decode(notesr.getString("NoteText")),
                    notesr.getInt("SuggestionNumber"),
                    notesr.getLong("CreationTime")
            ));
        }
        Suggestion suggestion = new Suggestion(
                Utils.b64Decode(r.getString("SuggestionText")),
                r.getInt("SuggestionNumber"),
                r.getLong("Submitter"),
                r.getLong("ChannelID"), r.getLong("CreationTime"), notes, r.getLong("MessageID"),
                r.getString("Approved") == null ? null : Utils.getDBoolean(r.getString("Approved")), r.getLong("Approver"), votes);
        if (core.voteMode == SuggestionVoteMode.BUTTONS) {
            while (votesr.next()) {
                votes.add(new SuggestionVote(votesr.getLong("UserID"), votesr.getInt("SuggestionNumber"), Utils.getDBoolean(votesr.getString("Agree"))));
            }

        }else {
            /*
            for (MessageReaction reaction : suggestion.getMessage().getReactions()) {
                if (reaction.getReactionEmote().getName().equals(SuggestionManager.getYesEmoji().getName())) {
                    List<User> users = reaction.retrieveUsers().complete();
                    for (User user : users) {
                        if (!user.isBot())
                        votes.add(new SuggestionVote(user.getIdLong(), suggestion.getNumber(), true));
                    }
                } else if (reaction.getReactionEmote().getName().equals(SuggestionManager.getNoEmoji().getName())) {
                    List<User> users = reaction.retrieveUsers().complete();
                    for (User user : users) {
                        if (!user.isBot())
                            votes.add(new SuggestionVote(user.getIdLong(), suggestion.getNumber(), true));
                    }
                }
            }

             */
        }
        return suggestion;
    }



    public CompletableFuture<Suggestion> makeSuggestion(String text, Long SubmitterID) {
        if (!core.getSuggestionsConfig().enabled()) {
            throw new IllegalStateException("Suggestions are not enabled");
        }
        Long channelId = core.getSuggestionsConfig().suggestions_channel();
        if (channelId == 0) {
            throw new IllegalStateException("Suggestions Channel set to 0... Please change it");
        }
        TextChannel channel = core.getGuild().getTextChannelById(channelId);
        if (channel == null) {
            throw new IllegalStateException("Suggestions Channel not found");
        }

        return core.completableFuture(() -> {
            try (Connection conn = core.getDatabase()) {
                PreparedStatement p1 = conn.prepareStatement("SELECT * FROM suggestions ORDER BY SuggestionNumber DESC ");
                ResultSet r1 = p1.executeQuery();
                int num = 1;
                if (r1.next()) {
                    num = r1.getInt("SuggestionNumber") + 1;
                }

                Suggestion suggestion = new Suggestion(text, num, SubmitterID, channelId, System.currentTimeMillis(), new HashSet<>(), null, null, null, new HashSet<>());
                User submitter = core.getJDA().retrieveUserById(SubmitterID).complete();
                MessageBuilder builder = MessageManager.get().getMessage(core.getSuggestionsConfig().suggestions_message(),
                        PlaceholdObjectList.ofArray(new PlaceholdObject(suggestion, "suggestion"), new PlaceholdObject(submitter, "submitter"))
                        ,null);
                if (core.voteMode == SuggestionVoteMode.BUTTONS) {
                    builder.setActionRows(ActionRow.of(
                            Button.success("yes", SuggestionManager.getYesEmoji().toJDAEmoji()),
                            Button.danger("no", SuggestionManager.getNoEmoji().toJDAEmoji())));
                }
                Message msg = core.queueMsg(builder.build(), channel).complete();
                PreparedStatement p2 = conn.prepareStatement("INSERT INTO suggestions(suggestionnumber, suggestiontext, submitter, messageid, channelid, creationtime) VALUES (?,?,?,?,?,?)");
                p2.setInt(1, num);
                p2.setString(2, Utils.b64Encode(text));
                p2.setLong(3, SubmitterID);
                p2.setLong(4, msg.getIdLong());
                p2.setLong(5, channelId);
                p2.setLong(6, System.currentTimeMillis());
                p2.execute();
                if (core.voteMode == SuggestionVoteMode.REACTIONS) {
                    msg.addReaction(getYesEmoji().getNameInReaction()).queue();
                    msg.addReaction(getNoEmoji().getNameInReaction()).queue();
                }
                return suggestion;
            } catch (SQLException e) {
                throw new UnCheckedSQLException(e);
            }
        });
    }

    public static Emoji getYesEmoji() {
        return Utils.getEmoji(DiscordSRVUtils.get().getSuggestionsConfig().yes_reaction(), new Emoji("✅"));
    }
    public static Emoji getNoEmoji() {
        return Utils.getEmoji(DiscordSRVUtils.get().getSuggestionsConfig().no_reaction(), new Emoji("❌"));
    }

    public static ActionRow getActionRow() {
        return ActionRow.of(Button.success("yes", SuggestionManager.getYesEmoji().toJDAEmoji()),
                Button.danger("no", SuggestionManager.getNoEmoji().toJDAEmoji()),
                Button.secondary("reset", github.scarsz.discordsrv.dependencies.jda.api.entities.Emoji.fromUnicode("⬜")));
    }







}
