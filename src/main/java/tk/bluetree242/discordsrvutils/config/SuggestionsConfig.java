package tk.bluetree242.discordsrvutils.config;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

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

    @AnnotationBasedSorter.Order(50)
    @ConfComments("# Should users be able to vote both yes and no?")
    @ConfDefault.DefaultBoolean(false)
    Boolean allow_both_vote();


    @AnnotationBasedSorter.Order(60)
    @ConfComments("# Message when the suggestion is noted (only latest note is previewed)")
    @ConfDefault.DefaultString("message:suggestion-noted")
    String suggestion_noted_message();
}
