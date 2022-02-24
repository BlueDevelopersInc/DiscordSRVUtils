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

package tk.bluetree242.discordsrvutils.systems.suggestions;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import tk.bluetree242.discordsrvutils.utils.Emoji;
import tk.bluetree242.discordsrvutils.utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Suggestion {

    protected final String text;
    protected final int number;
    protected final Long submitter;
    protected final Long ChannelID;
    protected final Long creationTime;
    protected final Set<SuggestionNote> notes;
    protected final Long MessageID;
    protected final DiscordSRVUtils core;
    protected Boolean Approved;
    protected Message msg;
    protected Long approver;
    Set<SuggestionVote> votes;
    public Suggestion(DiscordSRVUtils core, String text, int number, Long submitter, Long channelID, Long creationTime, Set<SuggestionNote> notes, Long MessageID, Boolean Approved, Long approver, Set<SuggestionVote> votes) {
        this.core = core;
        this.text = text;
        this.number = number;
        this.submitter = submitter;
        ChannelID = channelID;
        this.creationTime = creationTime;
        this.notes = notes;
        this.MessageID = MessageID;
        this.Approved = Approved;
        this.approver = approver;
        this.votes = votes;

    }

    public Set<SuggestionVote> getVotes() {
        return votes;
    }

    public Long getApprover() {
        return approver;
    }

    /**
     * @return null if not approved or declined yet, true if approved false if declined
     **/
    public Boolean isApproved() {
        return Approved;
    }


    public Long getMessageID() {
        return MessageID;
    }

    public DiscordSRVUtils getCore() {
        return core;
    }

    public String getText() {
        return text;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public Set<SuggestionNote> getNotes() {
        return notes;
    }

    public int getNumber() {
        return number;
    }

    public Long getSubmitter() {
        return submitter;
    }

    public Long getChannelID() {
        return ChannelID;
    }

    public CompletableFuture<SuggestionNote> addNote(Long staff, String note) {
        return core.getAsyncManager().completableFuture(() -> {
            try (Connection conn = core.getDatabaseManager().getConnection()) {
                PreparedStatement p1 = conn.prepareStatement("INSERT INTO suggestion_notes(staffid, notetext, suggestionnumber, creationtime) VALUES (?,?,?,?)");
                p1.setLong(1, staff);
                p1.setString(2, Utils.b64Encode(note));
                p1.setInt(3, number);
                p1.setLong(4, System.currentTimeMillis());
                p1.execute();
                SuggestionNote suggestionNote = new SuggestionNote(staff, note, number, System.currentTimeMillis());
                notes.add(suggestionNote);
                getMessage().editMessage(getCurrentMsg()).setActionRows(core.voteMode == SuggestionVoteMode.BUTTONS ? List.of(SuggestionManager.getActionRow(getYesCount(), getNoCount())) : Collections.emptyList()).queue();
                return suggestionNote;
            } catch (SQLException ex) {
                throw new UnCheckedSQLException(ex);
            }
        });
    }

    public CompletableFuture<Void> setApproved(boolean approved, Long staffID) {
        return core.getAsyncManager().completableFutureRun(() -> {
            try (Connection conn = core.getDatabaseManager().getConnection()) {
                PreparedStatement p1 = conn.prepareStatement("UPDATE suggestions SET Approved=?, Approver=? WHERE SuggestionNumber=?");
                p1.setString(1, Utils.getDBoolean(approved));
                p1.setLong(2, staffID);
                p1.setInt(3, number);
                p1.execute();
                this.Approved = approved;
                this.approver = staffID;
                getMessage().editMessage(getCurrentMsg()).setActionRows(core.voteMode == SuggestionVoteMode.BUTTONS ? List.of(SuggestionManager.getActionRow(getYesCount(), getNoCount())) : Collections.emptyList()).queue();

            } catch (SQLException e) {
                throw new UnCheckedSQLException(e);
            }
        });
    }

    public Message getMessage() {
        if (core.getJDA().getTextChannelById(ChannelID) == null) return null;
        return msg == null ? msg = core.getJDA().getTextChannelById(ChannelID).retrieveMessageById(MessageID).complete() : msg;
    }

    public int getYesCount() {
        if (core.voteMode == SuggestionVoteMode.BUTTONS) {
            List<SuggestionVote> votes = getVotes().stream().filter(v -> v.isAgree()).collect(Collectors.toList());
            return votes.size();
        } else
            return getMessage().getReactions().stream().filter(reaction -> reaction.getReactionEmote().getName().equals(Utils.getEmoji(core.getSuggestionsConfig().yes_reaction(), new Emoji("✅")).getName())).collect(Collectors.toList()).get(0).getCount() - 1;
    }

    public int getNoCount() {
        if (core.voteMode == SuggestionVoteMode.BUTTONS) {
            List<SuggestionVote> votes = getVotes().stream().filter(v -> !v.isAgree()).collect(Collectors.toList());
            return votes.size();
        } else
            return getMessage().getReactions().stream().filter(reaction -> reaction.getReactionEmote().getName().equals(Utils.getEmoji(core.getSuggestionsConfig().no_reaction(), new Emoji("❌")).getName())).collect(Collectors.toList()).get(0).getCount() - 1;
    }

    public Message getCurrentMsg() {
        PlaceholdObjectList holders = PlaceholdObjectList.ofArray(core, new PlaceholdObject(core, this, "suggestion"), new PlaceholdObject(core, core.getJDA().retrieveUserById(submitter).complete(), "submitter"));
        if (!notes.isEmpty()) {
            holders.add(new PlaceholdObject(core, getLatestNote(), "note"));
            holders.add(new PlaceholdObject(core, core.getJDA().retrieveUserById(getLatestNote().getStaffID()).complete(), "staff"));
        }

        if (isApproved() == null) {
            if (!notes.isEmpty()) {
                return core.getMessageManager().getMessage(core.getSuggestionsConfig().suggestion_noted_message(), holders, null).build();
            } else {
                return core.getMessageManager().getMessage(core.getSuggestionsConfig().suggestions_message(), holders, null).build();
            }
        } else if (isApproved()) {
            holders.add(new PlaceholdObject(core, core.getJDA().retrieveUserById(approver).complete(), "approver"));
            if (!notes.isEmpty()) {
                return core.getMessageManager().getMessage(core.getSuggestionsConfig().suggestion_noted_approved(), holders, null).build();
            } else {
                return core.getMessageManager().getMessage(core.getSuggestionsConfig().suggestion_approved(), holders, null).build();
            }
        } else {
            holders.add(new PlaceholdObject(core, core.getJDA().retrieveUserById(approver).complete(), "approver"));
            if (!notes.isEmpty()) {
                return core.getMessageManager().getMessage(core.getSuggestionsConfig().suggestion_noted_denied(), holders, null).build();
            } else {
                return core.getMessageManager().getMessage(core.getSuggestionsConfig().suggestion_denied(), holders, null).build();
            }
        }
    }

    public SuggestionNote getLatestNote() {
        List<SuggestionNote> noteList = new ArrayList<>(notes);
        Collections.sort(noteList, new Comparator<SuggestionNote>() {
            @Override
            public int compare(SuggestionNote o1, SuggestionNote o2) {
                return new Date(o2.getCreationTime()).compareTo(new Date(o1.getCreationTime()));
            }
        });
        return noteList.get(0);
    }
}
