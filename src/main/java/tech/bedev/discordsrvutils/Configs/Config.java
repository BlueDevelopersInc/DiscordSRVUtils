package tech.bedev.discordsrvutils.Configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;

public interface Config {

    @ConfDefault.DefaultString("Test")
    @ConfComments("Hi")
    String Test();
}
