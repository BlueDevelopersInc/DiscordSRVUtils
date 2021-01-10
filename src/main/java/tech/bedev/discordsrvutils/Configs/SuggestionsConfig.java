package tech.bedev.discordsrvutils.Configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

@ConfHeader("#The config for the suggestions System.")
public interface SuggestionsConfig {

    @ConfKey("enabled")
    @ConfDefault.DefaultBoolean(true)
    @ConfComments("\n#Should your bot use our Suggestions System?")
    @AnnotationBasedSorter.Order(10)
    boolean isEnabled();

    @ConfKey("suggestions-channel")
    @ConfDefault.DefaultLong(0000000L)
    @ConfComments("\n#The channel that suggestions are writen at.")
    @AnnotationBasedSorter.Order(20)
    Long channel();

    @ConfKey("yes-reaction")
    @ConfDefault.DefaultString("white_check_mark")
    @AnnotationBasedSorter.Order(30)
    @ConfComments("\n#Emoji to react in suggestions if \"yes\". Use name for that.")
    String emoji_yes();

    @ConfKey("no-reaction")
    @ConfDefault.DefaultString("x")
    @AnnotationBasedSorter.Order(40)
    @ConfComments("\n#Emoji to react in suggestions if \"no\". Use name for that.")
    String emoji_no();


}
