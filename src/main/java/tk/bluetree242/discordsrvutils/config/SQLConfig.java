package tk.bluetree242.discordsrvutils.config;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

@ConfHeader("#Database config. Only MySQL & MariaDB are supported")
public interface SQLConfig {


    @ConfDefault.DefaultBoolean(false)
    @ConfComments("#Should we use a database?")
    @AnnotationBasedSorter.Order(1)
    boolean isEnabled();

    @ConfDefault.DefaultString("localhost")
    @ConfComments("\n#Host for your database, usually localhost.")
    @AnnotationBasedSorter.Order(2)
    String Host();

    @ConfDefault.DefaultInteger(3306)
    @ConfComments("\n#Port for your Database, usually 3306")
    @AnnotationBasedSorter.Order(3)
    int Port();

    @ConfDefault.DefaultString("root")
    @ConfComments("\n#Username used to login to database.")
    @AnnotationBasedSorter.Order(4)
    String UserName();

    @ConfDefault.DefaultString("password")
    @ConfComments("\n#Password used to login to database.")
    @AnnotationBasedSorter.Order(5)
    String Password();

    @ConfDefault.DefaultString("DiscordSRVUtilsData")
    @ConfComments("\n#Database name. The host should tell you the name normally.")
    @AnnotationBasedSorter.Order(6)
    String DatabaseName();
}
