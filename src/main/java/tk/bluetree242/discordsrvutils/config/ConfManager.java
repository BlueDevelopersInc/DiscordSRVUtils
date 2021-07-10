package tk.bluetree242.discordsrvutils.config;

import space.arim.dazzleconf.ConfigurationFactory;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.dazzleconf.error.ConfigFormatSyntaxException;
import space.arim.dazzleconf.error.InvalidConfigException;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlOptions;
import space.arim.dazzleconf.helper.ConfigurationHelper;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;
import tk.bluetree242.discordsrvutils.exceptions.ConfigurationLoadException;

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
                .useCommentingWriter(true)
                .commentFormat("%s")
                // Enables writing YAML comments
                .build();
        ConfManager val = new ConfManager<>(configFolder, fileName,
                new SnakeYamlConfigurationFactory<>(configClass, new ConfigurationOptions.Builder().sorter(new AnnotationBasedSorter()).build(), yamlOptions));
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

    public org.checkerframework.checker.units.qual.C getConfigData() {
        org.checkerframework.checker.units.qual.C configData = (org.checkerframework.checker.units.qual.C) this.configData;
        if (configData == null) {
            throw new IllegalStateException("Configuration has not been loaded yet");
        }
        return configData;
    }
}
