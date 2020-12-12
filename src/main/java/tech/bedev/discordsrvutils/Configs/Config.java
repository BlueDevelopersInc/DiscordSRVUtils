package tech.bedev.discordsrvutils.Configs;

import org.springframework.core.annotation.Order;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfDefault.DefaultString;
import space.arim.dazzleconf.annote.ConfKey;

public interface Config {

    @ConfKey("isEnabled")
    @ConfDefault.DefaultBoolean(false)
    @ConfComments("Should we use a database?")
    @Order(1)
    boolean isEnabled();

    @ConfKey("Host")
    @DefaultString("localhost")
    @ConfComments("\nHost for your database, usually localhost.")
    @Order(2)
    String Host();

    @ConfKey("Port")
    @ConfDefault.DefaultInteger(3306)
    @ConfComments("\nPort for your Database, usually 3306")
    @Order(3)
    int Port();

    @ConfKey("DatabaseName")
    @DefaultString("DiscordSRVUtilsData")
    @ConfComments("\nDatabase name. The host should tell you the name normally.")
    @Order(4)
    String DatabaseName();
}
