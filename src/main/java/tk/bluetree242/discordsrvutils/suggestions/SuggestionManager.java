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

import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;
import tk.bluetree242.discordsrvutils.messages.MessageManager;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import tk.bluetree242.discordsrvutils.utils.Emoji;
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
        return getSuggestion(r, null);
    }

    public Suggestion getSuggestion(ResultSet r, ResultSet notesr) throws SQLException {
        if (notesr == null) {
            PreparedStatement p1 = r.getStatement().getConnection().prepareStatement("SELECT * FROM suggestion_notes WHERE SuggestionNumber=?");
            p1.setInt(1, r.getInt("SuggestionNumber"));
            notesr = p1.executeQuery();
        }
        Set<SuggestionNote> notes = new HashSet<>();
        while (notesr.next()) {
            notes.add(new SuggestionNote(
                    notesr.getLong("StaffID"),
                    Utils.b64Decode(notesr.getString("NoteText")),
                    notesr.getInt("SuggestionNumber"),
                    notesr.getLong("CreationTime")
            ));
        }
        return new Suggestion(
                Utils.b64Decode(r.getString("SuggestionText")),
                r.getInt("SuggestionNumber"),
                r.getLong("Submitter"),
                r.getLong("ChannelID"), r.getLong("CreationTime"), notes, r.getLong("MessageID"),
                r.getString("Approved") == null ? null : Utils.getDBoolean(r.getString("Approved")), r.getLong("Approver"));
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

                Suggestion suggestion = new Suggestion(text, num, SubmitterID, channelId, System.currentTimeMillis(), new HashSet<>(), null, null, null);
                User submitter = core.getJDA().retrieveUserById(SubmitterID).complete();
                Message msg = core.queueMsg(MessageManager.get().getMessage(core.getSuggestionsConfig().suggestions_message(),
                        PlaceholdObjectList.ofArray(new PlaceholdObject(suggestion, "suggestion"), new PlaceholdObject(submitter, "submitter"))
                        ,null).build(), channel).complete();
                PreparedStatement p2 = conn.prepareStatement("INSERT INTO suggestions(suggestionnumber, suggestiontext, submitter, messageid, channelid, creationtime) VALUES (?,?,?,?,?,?)");
                p2.setInt(1, num);
                p2.setString(2, Utils.b64Encode(text));
                p2.setLong(3, SubmitterID);
                p2.setLong(4, msg.getIdLong());
                p2.setLong(5, channelId);
                p2.setLong(6, System.currentTimeMillis());
                p2.execute();
                msg.addReaction(Utils.getEmoji(core.getSuggestionsConfig().yes_reaction(), new Emoji("✅")).getNameInReaction()).queue();
                msg.addReaction(Utils.getEmoji(core.getSuggestionsConfig().no_reaction(), new Emoji("❌")).getNameInReaction()).queue();
                return suggestion;
            } catch (SQLException e) {
                throw new UnCheckedSQLException(e);
            }
        });
    }






}
