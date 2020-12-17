package tech.bedev.discordsrvutils.Configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;
import space.arim.dazzleconf.annote.ConfDefault.DefaultString;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

import static space.arim.dazzleconf.annote.ConfDefault.*;

public interface SQLConfig {

    @DefaultBoolean(false)
    @ConfComments("Should we use a database?")
    @AnnotationBasedSorter.Order(1)
    boolean isEnabled();

    @DefaultString("localhost")
    @ConfComments("Host for your database, usually localhost.")
    @AnnotationBasedSorter.Order(2)
    String Host();

    @DefaultInteger(3306)
    @ConfComments("Port for your Database, usually 3306")
    @AnnotationBasedSorter.Order(3)
    int Port();

    @DefaultString("root")
    @ConfComments("Username used to login to database.")
    @Order(4)
    String UserName();

    @DefaultString("password")
    @ConfComments("Password used to login to database.")
    @Order(5)
    String Password();

    @DefaultString("DiscordSRVUtilsData")
    @ConfComments("Database name. The host should tell you the name normally.")
    @AnnotationBasedSorter.Order(6)
    String DatabaseName();
}
