package tech.bedev.discordsrvutils.Configs;

import jdk.jfr.BooleanFlag;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

import java.util.List;

public interface BotSettingsConfig{

    @ConfDefault.DefaultString("!")
    @ConfComments("Prefix used to execute commands.")
    @AnnotationBasedSorter.Order(10)
    String BotPrefix();

    @ConfDefault.DefaultString("global")
    @ConfComments("Channel used to send messages. Depends on your DiscordSRV Config")
    @AnnotationBasedSorter.Order(20)
    String chat_channel();

    @ConfDefault.DefaultBoolean(true)
    @ConfComments("Should we update the status?")
    @AnnotationBasedSorter.Order(30)
    boolean isStatusUpdates();

    @ConfDefault.DefaultInteger(12)
    @ConfComments("Status will update every 12 seconds for example")
    @AnnotationBasedSorter.Order(40)
    int Status_Update_Interval();

    @ConfDefault.DefaultStrings({"Playing Minecraft", "Watching online players", "Listening to People chatting"})
    @ConfComments("Status that will update.")
    @AnnotationBasedSorter.Order(50)
    List<String> Statuses();

    @ConfDefault.DefaultString("ONLINE")
    @ConfComments("Status for your bot, DND or ONLINE or IDLE")
    @AnnotationBasedSorter.Order(60)
    String status();

    @ConfDefault.DefaultBoolean(false)
    @ConfComments("If you use bungee, enable this in all your servers except lobby (Should use mySQL)")
    @Order(70)
    boolean isBungee();
}
