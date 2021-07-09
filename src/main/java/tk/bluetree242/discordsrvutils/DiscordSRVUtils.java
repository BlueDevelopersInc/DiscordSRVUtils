package tk.bluetree242.discordsrvutils;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.dazzleconf.error.InvalidConfigException;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandListener;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandManager;
import tk.bluetree242.discordsrvutils.commands.TestEmbedCommand;
import tk.bluetree242.discordsrvutils.config.ConfManager;
import tk.bluetree242.discordsrvutils.config.Config;
import tk.bluetree242.discordsrvutils.config.SQLConfig;
import tk.bluetree242.discordsrvutils.embeds.EmbedManager;
import tk.bluetree242.discordsrvutils.exceptions.StartupException;
import tk.bluetree242.discordsrvutils.listeners.discordsrv.DiscordSRVListener;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiscordSRVUtils extends JavaPlugin {
    private static DiscordSRVUtils instance;
    public static DiscordSRVUtils get() {
        return instance;
    }
    public final Path embedsDirectory = Paths.get(getDataFolder() + "//embeds");
    private ConfManager<Config> configmanager = ConfManager.create(getDataFolder().toPath(), "config.yml", Config.class);
    private Config config;

    private ConfManager<SQLConfig> sqlconfigmanager = ConfManager.create(getDataFolder().toPath(), "sql.yml", SQLConfig.class);
    private SQLConfig sqlconfig;
    private ExecutorService pool = Executors.newFixedThreadPool(3);
    private DiscordSRVListener dsrvlistener;
    private CommandListener commandListener;
    private void init() {
        instance = this;
        dsrvlistener = new DiscordSRVListener();
        new EmbedManager();
        commandListener = new CommandListener();
        new CommandManager();
    }

    public void onEnable() {
        try {
            init();
            if (!getServer().getPluginManager().isPluginEnabled("DiscordSRV")) {
                getLogger().severe("DiscordSRV is not installed or failed to start. Download DiscordSRV at https://www.spigotmc.org/resources/discordsrv.18494/");
                setEnabled(false);
                return;
            }
            reloadConfigs();
            DiscordSRV.api.subscribe(dsrvlistener);
            if (isReady()) {
                whenReady();
            }
            String storage = getSqlconfig().isEnabled() ? "MySQL" : "Hsqldb";
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "" +
                    "\n[]=====[&2Enabling DiscordSRVUtils&r]=====[]\n" +
                    "| &cInformation:\n&r" +
                    "|   &cName: &rDiscordSRVUtils\n&r" +
                    "|   &cDeveloper: &rBlueTree242\n&r" +
                    "|   &cVersion: &r" + getDescription().getVersion() + "\n&r" +
                    "|   &cStorage: &r" + storage + "\n&r" +
                    "| &cSupport:\n&r" +
                    "|   &cGithub: &rhttps://github.com/BlueTree242/DiscordSRVUtils/issues\n" +
                    "|   &cDiscord: &rhttps://discord.gg/MMMQHA4\n" +
                    "[]================================[]"));
            whenStarted();
        } catch (Throwable ex) {
            throw new StartupException(ex);
        }
    }


    public void onDisable() {
        instance = null;
        DiscordSRV.api.unsubscribe(dsrvlistener);
        getJDA().removeEventListener(commandListener);
        pool.shutdown();
    }

    private void whenStarted() {
        embedsDirectory.toFile().mkdir();
    }


    public void registerListeners() {
        getJDA().addEventListener(commandListener);
    }

    public void registerCommands() {
        CommandManager.get().registerCommand(new TestEmbedCommand());
    }



    public boolean isReady() {
        return DiscordSRV.isReady;
    }

    public void reloadConfigs() throws IOException, InvalidConfigException {
        configmanager.reloadConfig();
        config =configmanager.reloadConfigData();
        sqlconfigmanager.reloadConfig();
        sqlconfig = sqlconfigmanager.reloadConfigData();
    }

    public Config getMainConfig() {
        return config;
    }

    public SQLConfig getSqlconfig() {
        return sqlconfig;
    }

    public void executeAsync(Runnable r) {
        pool.execute(r);
    }

    public void whenReady() {
        registerCommands();
        registerListeners();
        getLogger().info("Plugin is ready to function.");
    }

    public JDA getJDA() {
        return DiscordSRV.getPlugin().getJda();
    }
    public EmbedManager getEmbedManager() {
        return EmbedManager.get();
    }

    public List<Long> getAdminIds() {
        return config.admins();
    }

    public List<User> getAdmins() {
        List<User> admins = new ArrayList<>();
        for (Long lng : getAdminIds()) {
            User usr = getJDA().getUserById(lng);
            if (usr != null) {
                admins.add(usr);
            } else {
                admins.add(getJDA().retrieveUserById(lng).complete());
            }
        }
        return admins;
    }

    public boolean isAdmin(long id) {
        if (getAdminIds().contains(id)) return true;
        return false;
    }

    public String getCommandPrefix() {
        return config.prefix();
    }
}
