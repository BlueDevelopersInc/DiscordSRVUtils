package tk.bluetree242.discordsrvutils.config;


import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.List;

public interface Config {


    @AnnotationBasedSorter.Order(10)
    @ConfComments("#Admins who can use the bot without limits, Only IDs")
    @ConfDefault.DefaultLongs({})
    List<Long> admins();

    @AnnotationBasedSorter.Order(20)
    @ConfComments("#Prefix used for Commands")
    @ConfDefault.DefaultString("!")
    String prefix();

    @AnnotationBasedSorter.Order(30)
    @ConfComments("#OnlineStatus for the bot. can be ONLINE, DND, or IDLE")
    @ConfDefault.DefaultString("ONLINE")
    String onlinestatus();

    @AnnotationBasedSorter.Order(31)
    @ConfKey("remove-discordsrv-link-listener")
    @ConfComments("#Should we remove DiscordSRV's account link listener?? bot won't respond to dm link codes")
    @ConfDefault.DefaultBoolean(false)
    boolean remove_discordsrv_link_listener();

    @AnnotationBasedSorter.Order(40)
    @ConfKey("welcomer.enabled")
    @ConfComments("#Should we do Welcomer?")
    @ConfDefault.DefaultBoolean(false)
    boolean welcomer_enabled();

    @AnnotationBasedSorter.Order(50)
    @ConfKey("welcomer.channel")
    @ConfComments("#Channel to send welcomer message, Use ID, No need to change this if dms are enabled")
    @ConfDefault.DefaultLong(0)
    long welcomer_channel();

    @AnnotationBasedSorter.Order(60)
    @ConfKey("welcomer.dm_user")
    @ConfComments("#Should we DM the User?")
    @ConfDefault.DefaultBoolean(false)
    boolean welcomer_dm_user();

    @AnnotationBasedSorter.Order(70)
    @ConfKey("welcomer.message")
    @ConfComments("#Welcomer message")
    @ConfDefault.DefaultString("message:welcome")
    String welcomer_message();

    @AnnotationBasedSorter.Order(80)
    @ConfKey("goodbye.enabled")
    @ConfComments("#Should we send goodbye messages?")
    @ConfDefault.DefaultBoolean(false)
    boolean goodbye_enabled();

    @AnnotationBasedSorter.Order(90)
    @ConfKey("goodbye.channel")
    @ConfDefault.DefaultLong(0)
    @ConfComments("#Channel to send message in, DM unavailable because when they leave there is a big chance of no mutual servers")
    long goodbye_channel();

    @AnnotationBasedSorter.Order(100)
    @ConfKey("goodbye.message")
    @ConfComments("#Message to send")
    @ConfDefault.DefaultString("GoodBye **[user.asTag]**. Hope you come back later")
    String goodbye_message();


    @AnnotationBasedSorter.Order(110)
    @ConfKey("afk.enabled")
    @ConfComments("#Should we send afk (and no longer afk) messages?")
    @ConfDefault.DefaultBoolean(true)
    boolean afk_message_enabled();

    @AnnotationBasedSorter.Order(120)
    @ConfKey("afk.channel")
    @ConfComments("#AFK Channel. Leave 0 for default channel for discordsrv")
    @ConfDefault.DefaultLong(0)
    long afk_channel();

    @AnnotationBasedSorter.Order(130)
    @ConfKey("afk.message")
    @ConfComments("#AFK Message")
    @ConfDefault.DefaultString("message:afk")
    String afk_message();

    @AnnotationBasedSorter.Order(140)
    @ConfKey("afk.no-longer-afk-message")
    @ConfDefault.DefaultString("message:no-longer-afk")
    String no_longer_afk_message();

}
