package tk.bluetree242.discordsrvutils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.OnlineStatus;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.dazzleconf.error.InvalidConfigException;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandListener;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandManager;
import tk.bluetree242.discordsrvutils.commands.bukkit.DiscordSRVUtilsCommand;
import tk.bluetree242.discordsrvutils.commands.bukkit.tabcompleters.DiscordSRVUtilsTabCompleter;
import tk.bluetree242.discordsrvutils.commands.discord.TestMessageCommand;
import tk.bluetree242.discordsrvutils.config.ConfManager;
import tk.bluetree242.discordsrvutils.config.Config;
import tk.bluetree242.discordsrvutils.config.SQLConfig;
import tk.bluetree242.discordsrvutils.listeners.jda.WelcomerAndGoodByeListener;
import tk.bluetree242.discordsrvutils.messages.MessageManager;
import tk.bluetree242.discordsrvutils.exceptions.ConfigurationLoadException;
import tk.bluetree242.discordsrvutils.exceptions.StartupException;
import tk.bluetree242.discordsrvutils.listeners.discordsrv.DiscordSRVListener;
import tk.bluetree242.discordsrvutils.tickets.TicketManager;
import tk.bluetree242.discordsrvutils.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiscordSRVUtils extends JavaPlugin {
    
    private static DiscordSRVUtils instance;
    public static DiscordSRVUtils get() {
        return instance;
    }
    private ConfManager<Config> configmanager = ConfManager.create(getDataFolder().toPath(), "config.yml", Config.class);
    private Config config;
    public final String fileseparator = System.getProperty("file.separator");
    public final Path messagesDirectory = Paths.get(getDataFolder() + fileseparator + "messages");
    private ConfManager<SQLConfig> sqlconfigmanager = ConfManager.create(getDataFolder().toPath(), "sql.yml", SQLConfig.class);
    private SQLConfig sqlconfig;
    public final Map<String, String> defaultmessages = new HashMap<>();
    private ExecutorService pool = Executors.newFixedThreadPool(3);
    private DiscordSRVListener dsrvlistener;
    public Logger logger = getLogger();
    private HikariDataSource sql;
    private List<ListenerAdapter> listeners = new ArrayList<>();
    private void init() {
        instance = this;
        dsrvlistener = new DiscordSRVListener();
        new MessageManager();
        new CommandManager();
        new TicketManager();
        listeners.add(new CommandListener());
        listeners.add(new WelcomerAndGoodByeListener());
        initDefaultMessages();
    }

    public void onEnable() {
        try {
            init();
            if (!getServer().getPluginManager().isPluginEnabled("DiscordSRV")) {
                logger.severe("DiscordSRV is not installed or failed to start. Download DiscordSRV at https://www.spigotmc.org/resources/discordsrv.18494/");
                logger.severe("Disabling...");
                setEnabled(false);
                return;
            }
            try {
                reloadConfigs();
            } catch (ConfigurationLoadException ex) {
                logger.severe(ex.getMessage());
                setEnabled(false);
                return;
            }
            String storage = getSqlconfig().isEnabled() ? "MySQL" : "HsqlDB";
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "" +
                    "\n[]=====[&2Enabling DiscordSRVUtils&r]=====[]\n" +
                    "| &cInformation:\n&r" +
                    "|   &cName: &rDiscordSRVUtils\n&r" +
                    "|   &cDevelopers: &rBlueTree242 & bugo07\n&r" +
                    "|   &cVersion: &r" + getDescription().getVersion() + "\n&r" +
                    "|   &cStorage: &r" + storage + "\n&r" +
                    "| &cSupport:\n&r" +
                    "|   &cGithub: &rhttps://github.com/BlueTree242/DiscordSRVUtils/issues\n" +
                    "|   &cDiscord: &rhttps://discord.gg/MMMQHA4\n" +
                    "[]================================[]"));
            System.setProperty("hsqldb.reconfig_logging", "false");
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            registerBukkitCommands();
            try {
                setupDatabase();
            } catch (SQLException ex) {
                logger.severe("Error could not connect to database: " + ex.getMessage());
                setEnabled(false);
                return;
            }
            DiscordSRV.api.subscribe(dsrvlistener);
            if (isReady()) {
                whenReady();
            }
            whenStarted();
        } catch (Throwable ex) {
            throw new StartupException(ex);
        }
    }

    public void registerBukkitCommands() {
        getCommand("discordsrvutils").setExecutor(new DiscordSRVUtilsCommand());
        getCommand("discordsrvutils").setTabCompleter(new DiscordSRVUtilsTabCompleter());
    }
    
    private void setupDatabase() throws SQLException {
        HikariConfig settings = new HikariConfig();
        String jdbcurl = null;
        String user = null;
        String pass = null;
        if (getSqlconfig().isEnabled()) {
            jdbcurl = "jdbc:mysql://" + // lets continue
                    getSqlconfig().Host() +
                    ":" + getSqlconfig().Port() + "/" + getSqlconfig().DatabaseName() + "?useSSL=false";
            user = sqlconfig.UserName();
            pass = sqlconfig.Password();
        } else {
            logger.info("MySQL is disabled, using hsqldb");
            jdbcurl = "jdbc:hsqldb:file:" + Paths.get(getDataFolder() + fileseparator + "database").resolve("Database") + ";hsqldb.lock_file=false";
            user = "SA";
            pass = "";
        }
        settings.setJdbcUrl(jdbcurl);
        settings.setUsername(user);
        settings.setPassword(pass);
        sql = new HikariDataSource(settings);
        Flyway flyway = Flyway.configure(getClass().getClassLoader())
                .dataSource(sql)
                .baselineOnMigrate(true)
                .locations("classpath:migrations")
                .validateMigrationNaming(true).group(true)
                .load();
        flyway.repair();
        flyway.migrate();
        logger.info("MySQL/HsqlDB Connected & Setup");
    }

    private void initDefaultMessages() {
        JSONObject welcome = new JSONObject();
        JSONObject welcomeembed = new JSONObject();
        welcomeembed.put("color", "cyan");
        welcomeembed.put("description", "\uD83D\uDD38 **Welcome [user.name] To The server!**\n" +
                "\n" +
                "\n" +
                "\uD83D\uDD38 **Server ip** | play.example.com\n" +
                "\n" +
                "\n" +
                "\uD83D\uDD38 **Store** | store.example.com");
        welcomeembed.put("thumbnail", new JSONObject().put("url", "[user.effectiveAvatarUrl]"));
        welcome.put("embed", welcomeembed);
        defaultmessages.put("welcome", welcome.toString());
    }
    
    public void onDisable() {
        instance = null;
        DiscordSRV.api.unsubscribe(dsrvlistener);
        if (isReady()) {
            getJDA().removeEventListener(listeners.toArray(new Object[0]));
        }
        pool.shutdown();
        sql.close();
    }

    private void whenStarted() {
        if (messagesDirectory.toFile().mkdir()) {
            defaultmessages.forEach((key, val) -> {
                try {
                    File file =  new File(messagesDirectory + fileseparator + key + ".json");
                    file.createNewFile();
                    FileWriter writer = new FileWriter(file);
                    writer.write(val);
                    writer.close();
                } catch (FileNotFoundException e) {
                    logger.severe("Error creating default message \"" + key + "\"");
                } catch (IOException e) {
                    logger.severe("Error writing default message \"" + key + "\"");
                }
            });
        }
    }


    public void registerListeners() {
        getJDA().addEventListener(listeners.toArray(new Object[0]));
    }

    public void registerCommands() {
        CommandManager.get().registerCommand(new TestMessageCommand());
    }



    public boolean isReady() {
        return DiscordSRV.isReady;
    }

    public void reloadConfigs() throws IOException, InvalidConfigException {
        configmanager.reloadConfig();
        config =configmanager.reloadConfigData();
        sqlconfigmanager.reloadConfig();
        sqlconfig = sqlconfigmanager.reloadConfigData();
        setSettings();
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

        setSettings();
        registerListeners();
        logger.info("Plugin is ready to function.");
    }

    public void setSettings() {
        if (!isReady()) return;
        OnlineStatus onlineStatus = getMainConfig().onlinestatus().equalsIgnoreCase("DND") ? OnlineStatus.DO_NOT_DISTURB : OnlineStatus.valueOf(getMainConfig().onlinestatus().toUpperCase());
        getJDA().getPresence().setStatus(onlineStatus);
    }

    public JDA getJDA() {
        return DiscordSRV.getPlugin().getJda();
    }
    public MessageManager getEmbedManager() {
        return MessageManager.get();
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

    public void severe(String sv) {
        getLogger().severe(sv);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("discordsrvutils.errornotifications"))
            p.sendMessage(Utils.colors("&7[&eDSU&7] &c" + sv));
        }
    }

    public boolean isAdmin(long id) {
        if (getAdminIds().contains(id)) return true;
        return false;
    }

    public String getCommandPrefix() {
        return config.prefix();
    }

    public Connection getDatabase() throws SQLException{
        return sql.getConnection();
    }
}
