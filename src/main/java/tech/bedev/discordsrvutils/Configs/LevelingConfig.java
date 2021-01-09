package tech.bedev.discordsrvutils.Configs;

import org.springframework.core.annotation.Order;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultString;
import space.arim.dazzleconf.annote.ConfDefault.DefaultStrings;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public interface LevelingConfig {



    @ConfDefault.DefaultBoolean(true)
    @ConfComments("#Should we make leveling? (Synced with Discord, Discord user must be linked to increase in levek using Discord)")
    @AnnotationBasedSorter.Order(10)
    boolean Leveling_Enabled();

    @ConfDefault.DefaultStrings({"&2-----------------------------------------------------", "                        &kk&6LEVEL UP!", "                        &7You  are now on level &a[Level]&7!", "&2-----------------------------------------------------"})
    @ConfComments("\n#Message send to Player when he is Increased in level in Minecraft.")
    @AnnotationBasedSorter.Order(20)
    List<String> levelup_minecraft();

    @DefaultStrings({"Congratulations [User_Mention] You leveled up to Level [Level]!"})
    @ConfComments("\n#Message sent when levels up on Discord.")
    @AnnotationBasedSorter.Order(30)
    List<String> levelup_Discord();

    @ConfDefault.DefaultLong(000000000000000000L)
    @ConfComments("\n#Where to send the levelup messages.")
    @Order(40)
    Long levelup_channel();

    @DefaultString("[Minecraft_Name] - Level [Level] - [XP] XP - [TotalMessages] Msges")
    @ConfComments("\n#Format for leaderboard command. You can use [TotalMessages], [DiscordMessages], [MinecraftMessages]")
    @Order(50)
    String leaderboard_format();


}
