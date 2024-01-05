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
import dev.bluetree242.discordsrvutils.jooq.tables.SuggestionsVotesTable;
import dev.bluetree242.discordsrvutils.jooq.tables.records.SuggestionNotesRecord;
import dev.bluetree242.discordsrvutils.jooq.tables.records.SuggestionsRecord;
import dev.bluetree242.discordsrvutils.jooq.tables.records.SuggestionsVotesRecord;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import dev.bluetree242.discordsrvutils.utils.Emoji;
import dev.bluetree242.discordsrvutils.utils.Utils;
import github.scarsz.discordsrv.dependencies.jda.api.MessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.exceptions.ErrorResponseException;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.ActionRow;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class SuggestionManager {

    private final DiscordSRVUtils core;
    public boolean loading = false;

    //Mode for suggestions voting
    @Getter
    public SuggestionVoteMode voteMode;


    public static Emoji getYesEmoji() {
        return Utils.getEmoji(DiscordSRVUtils.get().getSuggestionsConfig().yes_reaction(), new Emoji("✅"));
    }

    public static Emoji getNoEmoji() {
        return Utils.getEmoji(DiscordSRVUtils.get().getSuggestionsConfig().no_reaction(), new Emoji("❌"));
    }

    public static ActionRow getActionRow(int yes, int no) {
        return ActionRow.of(Button.success("yes", SuggestionManager.getYesEmoji().toJDAEmoji()).withLabel(yes + ""),
                Button.danger("no", SuggestionManager.getNoEmoji().toJDAEmoji()).withLabel(no + ""),
                Button.secondary("reset", github.scarsz.discordsrv.dependencies.jda.api.entities.Emoji.fromUnicode("⬜")));
    }


    public Suggestion getSuggestionByNumber(int number) {
        DSLContext conn = core.getDatabaseManager().jooq();
        SuggestionsRecord record = conn.selectFrom(SuggestionsTable.SUGGESTIONS)
                .where(SuggestionsTable.SUGGESTIONS.SUGGESTIONNUMBER.eq(number)).fetchOne();
        if (record == null) return null;
        return getSuggestion(record);
    }

    public Suggestion getSuggestionByMessageID(Long MessageID) {
        DSLContext conn = core.getDatabaseManager().jooq();
        SuggestionsRecord record = conn.selectFrom(SuggestionsTable.SUGGESTIONS)
                .where(SuggestionsTable.SUGGESTIONS.MESSAGEID.eq(MessageID)).fetchOne();
        if (record == null) return null;
        return getSuggestion(record);
    }

    public Suggestion getSuggestion(SuggestionsRecord r) {
        return getSuggestion(r, null, null);
    }

    public Suggestion getSuggestion(SuggestionsRecord r, List<SuggestionNotesRecord> notesr, List<SuggestionsVotesRecord> votesr) {
        DSLContext conn = r.configuration().dsl();
        if (notesr == null) {
            notesr = conn.selectFrom(SuggestionNotesTable.SUGGESTION_NOTES)
                    .where(SuggestionNotesTable.SUGGESTION_NOTES.SUGGESTIONNUMBER.eq(r.getSuggestionnumber())).fetch();
        }
        if (voteMode != SuggestionVoteMode.REACTIONS)
            if (votesr == null) {
                votesr = conn.selectFrom(SuggestionsVotesTable.SUGGESTIONS_VOTES)
                        .where(SuggestionsVotesTable.SUGGESTIONS_VOTES.SUGGESTIONNUMBER.eq(r.getSuggestionnumber().longValue()))
                        .fetch();
            }
        Set<SuggestionNote> notes = new HashSet<>();
        Set<SuggestionVote> votes = new HashSet<>();
        for (SuggestionNotesRecord record : notesr) {
            notes.add(new SuggestionNote(
                    record.getStaffid(),
                    Utils.b64Decode(record.getNotetext()),
                    record.getSuggestionnumber(),
                    record.getCreationtime()
            ));
        }
        Suggestion suggestion = new Suggestion(core,
                Utils.b64Decode(r.getSuggestiontext()),
                r.getSuggestionnumber(),
                r.getSubmitter(),
                r.getChannelid(), r.getCreationtime(), notes, r.getMessageid(),
                r.getApproved() == null ? null : Utils.getDBoolean(r.getApproved()), r.getApprover(), votes);
        if (voteMode == SuggestionVoteMode.BUTTONS) {
            for (SuggestionsVotesRecord record : votesr) {
                votes.add(new SuggestionVote(record.getUserid(), record.getSuggestionnumber().intValue(), Utils.getDBoolean(record.getAgree())));
            }

        }
        return suggestion;
    }

    public Suggestion makeSuggestion(String text, Long SubmitterID) {
        DSLContext conn = core.getDatabaseManager().jooq();
        if (!core.getSuggestionsConfig().enabled()) {
            throw new IllegalStateException("Suggestions are not enabled");
        }
        Long channelId = core.getSuggestionsConfig().suggestions_channel();
        if (channelId == 0) {
            throw new IllegalStateException("Suggestions Channel set to 0... Please change it");
        }
        TextChannel channel = core.getPlatform().getDiscordSRV().getMainGuild().getTextChannelById(channelId);
        if (channel == null) {
            throw new IllegalStateException("Suggestions Channel not found");
        }

        SuggestionsRecord check = conn.selectFrom(SuggestionsTable.SUGGESTIONS)
                .orderBy(SuggestionsTable.SUGGESTIONS.SUGGESTIONNUMBER.desc())
                .limit(1)
                .fetchOne();
        int num = 1;
        if (check != null) {
            num = check.getSuggestionnumber() + 1;
        }

        Suggestion suggestion = new Suggestion(core, text, num, SubmitterID, channelId, System.currentTimeMillis(), new HashSet<>(), null, null, null, new HashSet<>());
        User submitter = core.getJDA().retrieveUserById(SubmitterID).complete();
        MessageBuilder builder = core.getMessageManager().getMessage(core.getSuggestionsConfig().suggestions_message(),
                PlaceholdObjectList.ofArray(core, new PlaceholdObject(core, suggestion, "suggestion"), new PlaceholdObject(core, submitter, "submitter"))
                , null);
        if (voteMode == SuggestionVoteMode.BUTTONS) {
            builder.setActionRows(getActionRow(0, 0));
        }
        Message msg = core.queueMsg(builder.build(), channel).complete();
        conn.insertInto(SuggestionsTable.SUGGESTIONS)
                .set(SuggestionsTable.SUGGESTIONS.SUGGESTIONNUMBER, num)
                .set(SuggestionsTable.SUGGESTIONS.SUGGESTIONTEXT, Utils.b64Encode(text))
                .set(SuggestionsTable.SUGGESTIONS.SUBMITTER, SubmitterID)
                .set(SuggestionsTable.SUGGESTIONS.MESSAGEID, msg.getIdLong())
                .set(SuggestionsTable.SUGGESTIONS.CHANNELID, channelId)
                .set(SuggestionsTable.SUGGESTIONS.CREATIONTIME, System.currentTimeMillis())
                .set(SuggestionsTable.SUGGESTIONS.VOTE_MODE, voteMode.name())
                .execute();
        if (voteMode == SuggestionVoteMode.REACTIONS) {
            msg.addReaction(getYesEmoji().getNameInReaction()).queue();
            msg.addReaction(getNoEmoji().getNameInReaction()).queue();
        }
        return suggestion;
    }

    public void migrateSuggestions() {
        try {
            voteMode = SuggestionVoteMode.valueOf(core.getSuggestionsConfig().suggestions_vote_mode().toUpperCase());
            String warnmsg = "Suggestions are being migrated to the new Suggestions Mode. Users may not vote for suggestions during this time";
            boolean sent = false;
            loading = true;
            DSLContext jooq = core.getDatabaseManager().jooq();
            List<SuggestionsRecord> records = jooq
                    .selectFrom(SuggestionsTable.SUGGESTIONS)
                    .where(SuggestionsTable.SUGGESTIONS.VOTE_MODE.notEqual(voteMode.name()))
                    .or(SuggestionsTable.SUGGESTIONS.VOTE_MODE.isNull())
                    .fetch();
            for (SuggestionsRecord record : records) {
                Suggestion suggestion = core.getSuggestionManager().getSuggestion(record);
                try {
                    Message msg = suggestion.getMessage();
                    if (msg != null) {
                        if (msg.getButtons().isEmpty()) {
                            if (voteMode == SuggestionVoteMode.REACTIONS) {
                            } else {
                                if (!sent) {
                                    core.logger.info(warnmsg);
                                    sent = true;
                                    core.getSuggestionManager().loading = true;
                                }
                                msg.clearReactions().queue();
                                msg.editMessage(suggestion.getCurrentMsg()).setActionRow(
                                        Button.success("yes", SuggestionManager.getYesEmoji().toJDAEmoji()),
                                        Button.danger("no", SuggestionManager.getNoEmoji().toJDAEmoji()),
                                        Button.secondary("reset", github.scarsz.discordsrv.dependencies.jda.api.entities.Emoji.fromUnicode("⬜"))).queue();
                            }
                        } else {
                            if (voteMode == SuggestionVoteMode.REACTIONS) {
                                if (!sent) {
                                    core.getSuggestionManager().loading = true;
                                    core.logger.info(warnmsg);
                                    sent = true;
                                }
                                msg.addReaction(SuggestionManager.getYesEmoji().getNameInReaction()).queue();
                                msg.addReaction(SuggestionManager.getNoEmoji().getNameInReaction()).queue();
                                msg.editMessage(msg).setActionRows(Collections.EMPTY_LIST).queue();
                            }
                        }
                        jooq.update(SuggestionsTable.SUGGESTIONS)
                                .set(SuggestionsTable.SUGGESTIONS.VOTE_MODE, voteMode.name())
                                .where(SuggestionsTable.SUGGESTIONS.SUGGESTIONNUMBER.eq(suggestion.getNumber()))
                                .execute();
                    }
                } catch (ErrorResponseException ignored) {
                }
            }
            if (sent) {
                core.logger.info("Suggestions Migration has finished.");
            }
        } catch (Throwable ex) {
            core.getErrorHandler().defaultHandle(ex);
            core.getLogger().severe("Failed to update suggestions system. Suggestions may not work as expected.");
        }
        core.getSuggestionManager().loading = false;
    }


}
