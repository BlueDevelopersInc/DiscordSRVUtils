package tk.bluetree242.discordsrvutils.config;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.List;

public interface LevelingConfig{

        @AnnotationBasedSorter.Order(9)
        @ConfComments("#Is leveling enabled?")
        @ConfDefault.DefaultBoolean(true)
        boolean enabled();

        @ConfKey("minecraft-levelup-message")
        @ConfComments("#Message when a minecraft player levelup")
        @AnnotationBasedSorter.Order(10)
        @ConfDefault.DefaultStrings({
                "&e-----------------------------------------------------&r",
                "          &cCongratulations! &eYou leveled up to level [stats.level]!",
                "&e-----------------------------------------------------&r"
        })
        List<String> minecraft_levelup_message();


        @ConfKey("antispam-messages")
        @AnnotationBasedSorter.Order(20)
        @ConfDefault.DefaultBoolean(true)
        Boolean antispam_messages();

        @ConfKey("discord-message")
        @ConfComments("#Message when a Discord user levelup")
        @AnnotationBasedSorter.Order(30)
        @ConfDefault.DefaultString("Congratulations [user.asMention]! You leveled up to level [stats.level]")
        String discord_message();

        @ConfKey("discord-channel")
        @ConfComments("#ID of channel for discord leveling messages, 0 for discordsrv default and -1 for the channel where user leveled up")
        @AnnotationBasedSorter.Order(30)
        @ConfDefault.DefaultLong(-1)
        long discord_channel();
}
