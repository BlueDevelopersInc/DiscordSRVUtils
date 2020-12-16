package tech.bedev.discordsrvutils.Configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

public interface BotSettingsConfig{

    @ConfDefault.DefaultString("!")
    @ConfComments("Prefix used to execute commands.")
    @AnnotationBasedSorter.Order(10)
    String BotPrefix();

    @ConfDefault.DefaultString("global")
    @ConfComments("Channel used to send messages. Depends on your DiscordSRV Config")
    @AnnotationBasedSorter.Order(20)
    String chat_channel();
}
