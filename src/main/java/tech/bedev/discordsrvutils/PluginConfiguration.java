package tech.bedev.discordsrvutils;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

public class PluginConfiguration {
    @NotNull
    public static YamlConfiguration loadConfiguration(@NotNull File file) throws InvalidConfigurationException, IOException {
        Validate.notNull(file, "File cannot be null");

        YamlConfiguration config = new YamlConfiguration();


        config.load(file);


        return config;
    }

    @NotNull
    public static YamlConfiguration loadConfiguration(@NotNull Reader reader) throws InvalidConfigurationException, IOException{
        Validate.notNull(reader, "Stream cannot be null");

        YamlConfiguration config = new YamlConfiguration();


        config.load(reader);


        return config;
    }
}
