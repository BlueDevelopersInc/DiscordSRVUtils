/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2024 BlueTree242
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

package dev.bluetree242.discordsrvutils.systems.suggestions;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.jooq.tables.SuggestionNotesTable;
import dev.bluetree242.discordsrvutils.jooq.tables.SuggestionsTable;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import dev.bluetree242.discordsrvutils.utils.Emoji;
import dev.bluetree242.discordsrvutils.utils.Utils;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import lombok.Getter;
import org.jooq.DSLContext;

import java.util.*;
import java.util.stream.Collectors;

public class Suggestion {

    @Getter
    protected final String text;
    @Getter
    protected final int number;
    @Getter
    protected final Long submitter;
    @Getter
    protected final Long channelID;
    @Getter
    protected final Long creationTime;
    @Getter
    protected final Set<SuggestionNote> notes;
    @Getter
    protected final Long MessageID;
    @Getter
    protected final DiscordSRVUtils core;
    protected Boolean Approved;
    protected Message msg;
    @Getter
    protected Long approver;
    @Getter
    Set<SuggestionVote> votes;

    public Suggestion(DiscordSRVUtils core, String text, int number, Long submitter, Long channelID, Long creationTime, Set<SuggestionNote> notes, Long MessageID, Boolean Approved, Long approver, Set<SuggestionVote> votes) {
        this.core = core;
        this.text = text;
        this.number = number;
        this.submitter = submitter;
        this.channelID = channelID;
        this.creationTime = creationTime;
        this.notes = notes;
        this.MessageID = MessageID;
        this.Approved = Approved;
        this.approver = approver;
        this.votes = votes;

    }

    /**
     * @return null if not approved or declined yet, true if approved, false if declined.
     **/
    public Boolean isApproved() {
        return Approved;
    }


    public SuggestionNote addNote(Long staff, String note) {
        DSLContext conn = core.getDatabaseManager().jooq();
        conn.insertInto(SuggestionNotesTable.SUGGESTION_NOTES)
                .set(SuggestionNotesTable.SUGGESTION_NOTES.STAFFID, staff)
                .set(SuggestionNotesTable.SUGGESTION_NOTES.NOTETEXT, Utils.b64Encode(note))
                .set(SuggestionNotesTable.SUGGESTION_NOTES.SUGGESTIONNUMBER, number)
                .set(SuggestionNotesTable.SUGGESTION_NOTES.CREATIONTIME, System.currentTimeMillis())
                .execute();
        SuggestionNote suggestionNote = new SuggestionNote(staff, note, number, System.currentTimeMillis());
        notes.add(suggestionNote);
        getMessage().editMessage(getCurrentMsg()).setActionRows(core.getSuggestionManager().voteMode == SuggestionVoteMode.BUTTONS ? Collections.singletonList(SuggestionManager.getActionRow(getYesCount(), getNoCount())) : Collections.emptyList()).queue();
        return suggestionNote;
    }

    public void setApproved(boolean approved, Long staffID) {
        DSLContext conn = core.getDatabaseManager().jooq();
        conn.update(SuggestionsTable.SUGGESTIONS)
                .set(SuggestionsTable.SUGGESTIONS.APPROVED, Utils.getDBoolean(approved))
                .set(SuggestionsTable.SUGGESTIONS.APPROVER, staffID)
                .where(SuggestionsTable.SUGGESTIONS.SUGGESTIONNUMBER.eq(number))
                .execute();
        this.Approved = approved;
        this.approver = staffID;
        getMessage().editMessage(getCurrentMsg()).setActionRows(core.getSuggestionManager().voteMode == SuggestionVoteMode.BUTTONS ? Collections.singletonList(SuggestionManager.getActionRow(getYesCount(), getNoCount())) : Collections.emptyList()).queue();
    }

    public Message getMessage() {
        if (core.getJDA().getTextChannelById(channelID) == null) return null;
        return msg == null ? msg = core.getJDA().getTextChannelById(channelID).retrieveMessageById(MessageID).complete() : msg;
    }

    public int getYesCount() {
        if (MessageID == null) return 0;
        if (core.getSuggestionManager().voteMode == SuggestionVoteMode.BUTTONS) {
            List<SuggestionVote> votes = getVotes().stream().filter(SuggestionVote::isAgree).collect(Collectors.toList());
            return votes.size();
        } else
            return getMessage().getReactions().stream().filter(reaction -> reaction.getReactionEmote().getName().equals(Utils.getEmoji(core.getSuggestionsConfig().yes_reaction(), new Emoji("✅")).getName())).collect(Collectors.toList()).get(0).getCount() - 1;
    }

    public int getNoCount() {
        if (MessageID == null) return 0;
        if (core.getSuggestionManager().voteMode == SuggestionVoteMode.BUTTONS) {
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
        noteList.sort((o1, o2) -> new Date(o2.getCreationTime()).compareTo(new Date(o1.getCreationTime())));
        return noteList.get(0);
    }
}
