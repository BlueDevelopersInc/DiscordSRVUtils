package tk.bluetree242.discordsrvutils.config;

import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;

import java.util.List;

public interface LevelingConfig {

    @ConfKey("minecraft-levelup-message")
    @ConfDefault.DefaultStrings({
            "&e-----------------------------------------------------&r",
            "          &cCongratulations! &eYou leveled up to level [stats.level]!",
            "&e-----------------------------------------------------&r"
    })
    List<String> minecraft_levelup_message();
}
