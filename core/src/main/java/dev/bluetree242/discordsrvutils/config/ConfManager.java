/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2024 BlueTree242
 * -------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * END
 */

package dev.bluetree242.discordsrvutils.config;

import dev.bluetree242.discordsrvutils.exceptions.ConfigurationLoadException;
import space.arim.dazzleconf.ConfigurationFactory;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.dazzleconf.error.ConfigFormatSyntaxException;
import space.arim.dazzleconf.error.InvalidConfigException;
import space.arim.dazzleconf.ext.snakeyaml.CommentMode;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlOptions;
import space.arim.dazzleconf.helper.ConfigurationHelper;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.io.IOException;
import java.nio.file.Path;

public class ConfManager<C> extends ConfigurationHelper<C> {
    private String confname;
    private volatile C configData;

    private ConfManager(Path configFolder, String fileName, ConfigurationFactory<C> factory) {
        super(configFolder, fileName, factory);
    }

    public static <C> ConfManager<C> create(Path configFolder, String fileName, Class<C> configClass) {
        // SnakeYaml example
        SnakeYamlOptions yamlOptions = new SnakeYamlOptions.Builder()
                .commentMode(CommentMode.alternativeWriter("%s"))
                // Enables writing YAML comments
                .build();
        ConfManager val = new ConfManager<>(configFolder, fileName,
                SnakeYamlConfigurationFactory.create(configClass, new ConfigurationOptions.Builder().sorter(new AnnotationBasedSorter()).build(), yamlOptions));
        val.confname = fileName;
        return val;
    }

    public void reloadConfig() {
        try {
            configData = reloadConfigData();
        } catch (IOException ex) {
            throw new ConfigurationLoadException(ex, confname);

        } catch (ConfigFormatSyntaxException ex) {
            configData = getFactory().loadDefaults();

            throw new ConfigurationLoadException(ex, confname);

        } catch (InvalidConfigException ex) {
            configData = getFactory().loadDefaults();

            throw new ConfigurationLoadException(ex, confname);
        }
    }

    public C getConfigData() {
        C configData = (C) this.configData;
        if (configData == null) {
            throw new IllegalStateException("Configuration has not been loaded yet");
        }
        return configData;
    }
}
