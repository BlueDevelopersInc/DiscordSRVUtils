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

package dev.bluetree242.discordsrvutils.config;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

@ConfHeader("# https://wiki.discordsrvutils.xyz/suggestions/\n")
public interface SuggestionsConfig {


    @AnnotationBasedSorter.Order(9)
    @ConfComments("#Is Suggestions enabled?")
    @ConfDefault.DefaultBoolean(false)
    boolean enabled();

    @AnnotationBasedSorter.Order(10)
    @ConfComments("# Channel to send suggestions in")
    @ConfDefault.DefaultLong(0)
    Long suggestions_channel();


    @AnnotationBasedSorter.Order(20)
    @ConfComments("# Suggestion Message without notes or approval/decline")
    @ConfDefault.DefaultString("message:suggestion")
    String suggestions_message();

    @AnnotationBasedSorter.Order(30)
    @ConfComments("# The Reaction for community to react when they +1 vote the suggestion")
    @ConfDefault.DefaultString("white_check_mark")
    String yes_reaction();

    @AnnotationBasedSorter.Order(40)
    @ConfComments("# The Reaction for community to react when they -1 vote the suggestion")
    @ConfDefault.DefaultString("x")
    String no_reaction();


    @AnnotationBasedSorter.Order(50)
    @ConfComments("# Should the Submitter of suggestion be able to vote their own suggestion?")
    @ConfDefault.DefaultBoolean(false)
    Boolean allow_submitter_vote();


    @AnnotationBasedSorter.Order(60)
    @ConfComments("# Message when the suggestion is noted (only latest note is previewed)")
    @ConfDefault.DefaultString("message:suggestion-noted")
    String suggestion_noted_message();

    @AnnotationBasedSorter.Order(70)
    @ConfComments("# Message when the suggestion is noted and also approved (only latest note is previewed)")
    @ConfDefault.DefaultString("message:suggestion-noted-approved")
    String suggestion_noted_approved();

    @AnnotationBasedSorter.Order(80)
    @ConfComments("# Message when the suggestion is approved")
    @ConfDefault.DefaultString("message:suggestion-approved")
    String suggestion_approved();

    @AnnotationBasedSorter.Order(90)
    @ConfComments("# Message when the suggestion is noted and also denied (only latest note is previewed)")
    @ConfDefault.DefaultString("message:suggestion-noted-denied")
    String suggestion_noted_denied();

    @AnnotationBasedSorter.Order(90)
    @ConfComments("# Message when the suggestion is denied")
    @ConfDefault.DefaultString("message:suggestion-denied")
    String suggestion_denied();

    @AnnotationBasedSorter.Order(95)
    @ConfComments("# If a message is sent in the suggestions channel, do we delete it and make a new suggestion?\n#Note: Muted role and anti suggestion spam will not be applied here")
    @ConfDefault.DefaultBoolean(false)
    Boolean set_suggestion_from_channel();

    /*
    @AnnotationBasedSorter.Order(100)
    @ConfComments("# Should DM the submitter of a suggestion when a note is added to it?")
    @ConfDefault.DefaultBoolean(false)
    Boolean dm_submitter_when_note_added();

    @AnnotationBasedSorter.Order(110)
    @ConfComments("# Should DM the submitter of a suggestion when a approved/denied?")
    @ConfDefault.DefaultBoolean(false)
    Boolean dm_submitter_when_approved();
     */

    @AnnotationBasedSorter.Order(120)
    @ConfComments("# Role that if user have they can't make suggestions")
    @ConfDefault.DefaultLong(0)
    Long suggestion_muted_role();

    @AnnotationBasedSorter.Order(130)
    @ConfComments("# Mode of the suggestions. Set to BUTTONS to make it use buttons instead of reactions/n# Note that changing this is not affected until server restart./n#Another note is that your votes will be reset by changing this")
    @ConfDefault.DefaultString("REACTIONS")
    String suggestions_vote_mode();

    @AnnotationBasedSorter.Order(140)
    @ConfComments("# Message when User tries to vote their own suggestion. This message is only sent on BUTTONS mode.")
    @ConfDefault.DefaultString("You may not vote your own suggestion.")
    String vote_own_suggestion_message();
}
