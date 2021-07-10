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

}
