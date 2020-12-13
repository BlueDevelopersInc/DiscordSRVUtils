package tech.bedev.discordsrvutils.Configs;

import org.springframework.core.annotation.Order;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;
import space.arim.dazzleconf.annote.ConfDefault.DefaultString;
import space.arim.dazzleconf.annote.ConfKey;

import static space.arim.dazzleconf.annote.ConfDefault.*;

public interface Config {

    @DefaultBoolean(false)
    @ConfComments("Should we use a database?")
    @Order(1)
    boolean isEnabled();

    @DefaultString("localhost")
    @ConfComments("\nHost for your database, usually localhost.")
    @Order(2)
    String Host();

    @DefaultInteger(3306)
    @ConfComments("\nPort for your Database, usually 3306")
    @Order(3)
    int Port();

    @DefaultString("DiscordSRVUtilsData")
    @ConfComments("\nDatabase name. The host should tell you the name normally.")
    @Order(4)
    String DatabaseName();
}
