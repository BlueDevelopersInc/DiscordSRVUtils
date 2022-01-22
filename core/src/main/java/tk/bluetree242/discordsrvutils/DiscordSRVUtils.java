/*
 *  LICENSE
 *  DiscordSRVUtils
 *  -------------
 *  Copyright (C) 2020 - 2021 BlueTree242
 *  -------------
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program.  If not, see
 *  <http://www.gnu.org/licenses/gpl-3.0.html>.
 *  END
 */

package tk.bluetree242.discordsrvutils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.OnlineStatus;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.dependencies.jda.api.requests.GatewayIntent;
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import github.scarsz.discordsrv.dependencies.jda.api.utils.cache.CacheFlag;
import github.scarsz.discordsrv.dependencies.okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import space.arim.dazzleconf.error.InvalidConfigException;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandListener;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandManager;
import tk.bluetree242.discordsrvutils.config.*;
import tk.bluetree242.discordsrvutils.embeds.Embed;
import tk.bluetree242.discordsrvutils.exceptions.ConfigurationLoadException;
import tk.bluetree242.discordsrvutils.hooks.PluginHookManager;
import tk.bluetree242.discordsrvutils.leveling.LevelingManager;
import tk.bluetree242.discordsrvutils.leveling.listeners.game.GameLevelingListener;
import tk.bluetree242.discordsrvutils.leveling.listeners.jda.DiscordLevelingListener;
import tk.bluetree242.discordsrvutils.listeners.bukkit.JoinUpdateChecker;
import tk.bluetree242.discordsrvutils.listeners.discordsrv.DiscordSRVListener;
import tk.bluetree242.discordsrvutils.listeners.jda.WelcomerAndGoodByeListener;
import tk.bluetree242.discordsrvutils.messages.MessageManager;
import tk.bluetree242.discordsrvutils.platform.*;
import tk.bluetree242.discordsrvutils.status.StatusListener;
import tk.bluetree242.discordsrvutils.status.StatusManager;
import tk.bluetree242.discordsrvutils.suggestions.SuggestionManager;
import tk.bluetree242.discordsrvutils.suggestions.SuggestionVoteMode;
import tk.bluetree242.discordsrvutils.suggestions.listeners.SuggestionListener;
import tk.bluetree242.discordsrvutils.tickets.TicketManager;
import tk.bluetree242.discordsrvutils.tickets.listeners.PanelOpenListener;
import tk.bluetree242.discordsrvutils.tickets.listeners.TicketCloseListener;
import tk.bluetree242.discordsrvutils.tickets.listeners.TicketDeleteListener;
import tk.bluetree242.discordsrvutils.utils.DebugUtil;
import tk.bluetree242.discordsrvutils.utils.FileWriter;
import tk.bluetree242.discordsrvutils.utils.Utils;
import tk.bluetree242.discordsrvutils.waiter.WaiterManager;
import tk.bluetree242.discordsrvutils.waiters.listeners.CreatePanelListener;
import tk.bluetree242.discordsrvutils.waiters.listeners.EditPanelListener;
import tk.bluetree242.discordsrvutils.waiters.listeners.PaginationListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class DiscordSRVUtils {


    //instance for DiscordSRVUtils.get()
    private static DiscordSRVUtils instance;
    //file separator string
    public final String fileseparator = System.getProperty("file.separator");
    //default messages to use
    public final Map<String, String> defaultmessages = new HashMap<>();
    //listeners that should be registered
    public final List<ListenerAdapter> listeners = new ArrayList<>();
    //leveling roles jsonobject, Initialized on startup
    public JSONObject levelingRolesRaw;
    //was the DiscordSRV AccountLink Listener Removed?
    public boolean removedDiscordSRVAccountLinkListener = false;
    //Mode for suggestions voting
    public SuggestionVoteMode voteMode;
    //latest error that occurred on our thread pool
    public String finalError = null;
    //Plugins we hooked into
    //messages folder path
    public Path messagesDirectory;
    // faster getter for the logger
    public Logger logger;
    //Configurations
    private ConfManager<Config> configmanager;
    private ConfManager<PunishmentsIntegrationConfig> bansIntegrationconfigmanager;
    private ConfManager<TicketsConfig> ticketsconfigManager;
    private Config config;
    private ConfManager<LevelingConfig> levelingconfigManager;
    private SQLConfig sqlconfig;
    private ConfManager<SuggestionsConfig> suggestionsConfigManager;
    private PunishmentsIntegrationConfig bansIntegrationConfig;
    private ConfManager<StatusConfig> statusConfigConfManager;
    private TicketsConfig ticketsConfig;
    private LevelingConfig levelingConfig;
    private PluginPlatform main;
    private SuggestionsConfig suggestionsConfig;
    private ConfManager<SQLConfig> sqlconfigmanager;
    private StatusConfig statusConfig;
    //Thread Pool
    private ThreadPoolExecutor pool;
    //Our DiscordSRV Listener
    private DiscordSRVListener dsrvlistener;
    //database connection pool
    private HikariDataSource sql;
    private long lastErrorTime = 0;

    public DiscordSRVUtils(PluginPlatform main) {
        this.main = main;
        initConfigs();
        logger = main.getLogger();
        messagesDirectory = Paths.get(main.getDataFolder() + fileseparator + "messages");
        onLoad();
    }

    public static DiscordSRVUtils get() {
        return instance;
    }

    public static PlatformServer getServer() {
        return getPlatform().getServer();
    }

    public static PluginPlatform getPlatform() {
        return get().main;
    }

    public static PlatformDiscordSRV getDiscordSRV() {
        return getPlatform().getDiscordSRV();
    }

    private final void initConfigs() {
        configmanager = ConfManager.create(main.getDataFolder().toPath(), "config.yml", Config.class);
        sqlconfigmanager = ConfManager.create(main.getDataFolder().toPath(), "sql.yml", SQLConfig.class);
        bansIntegrationconfigmanager = ConfManager.create(main.getDataFolder().toPath(), "PunishmentsIntegration.yml", PunishmentsIntegrationConfig.class);
        ticketsconfigManager = ConfManager.create(main.getDataFolder().toPath(), "tickets.yml", TicketsConfig.class);
        levelingconfigManager = ConfManager.create(main.getDataFolder().toPath(), "leveling.yml", LevelingConfig.class);
        suggestionsConfigManager = ConfManager.create(main.getDataFolder().toPath(), "suggestions.yml", SuggestionsConfig.class);
        statusConfigConfManager = ConfManager.create(main.getDataFolder().toPath(), "status.yml", StatusConfig.class);
    }

    public Thread newDSUThread(Runnable r) {
        //start new thread with name and handler
        Thread thread = new Thread(r);
        thread.setName("DSU-THREAD");
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler((t, e) -> defaultHandle(e));
        return thread;
    }

    public ThreadPoolExecutor getPool() {
        return pool;
    }

    private void init() {
        //set the instance
        instance = this;
        //initialize discordsrv listener
        dsrvlistener = new DiscordSRVListener();
        //Initialize Managers
        //These automatically set themselves so just a simple constructor call
        new MessageManager();
        new CommandManager();
        new TicketManager();
        new WaiterManager();
        new LevelingManager();
        new SuggestionManager();
        new StatusManager();
        new PluginHookManager();
        //put status listener to make StatusListener.get() work
        main.getStatusListener();
        //Add The JDA Listeners to the List
        listeners.add(new CommandListener());
        listeners.add(new WelcomerAndGoodByeListener());
        listeners.add(new CreatePanelListener());
        listeners.add(new PaginationListener());
        listeners.add(new TicketDeleteListener());
        listeners.add(new PanelOpenListener());
        listeners.add(new TicketCloseListener());
        listeners.add(new EditPanelListener());
        listeners.add(new DiscordLevelingListener());
        listeners.add(new SuggestionListener());
        //Init Default Messages
        initDefaultMessages();

    }

    public void onLoad() {
        init();
        //require intents and cacheflags
        if (main.getServer().isPluginInstalled("DiscordSRV")) {
            if (DiscordSRV.isReady) {
                //Oh no, they are using a plugin manager to reload the plugin, give them a warn
                logger.warning("It seems like you are using a Plugin Manager to reload the plugin. This is not a good practice. If you see problems. Please restart");
                return;
            }
            DiscordSRV.api.requireIntent(GatewayIntent.GUILD_MESSAGE_REACTIONS);
            DiscordSRV.api.requireCacheFlag(CacheFlag.EMOTE);
        }
    }

    public void onEnable() {
        updateCheck();
        try {
            if (!main.getServer().isPluginEnabled("DiscordSRV")) {
                logger.severe("DiscordSRV is not installed or failed to start. Download DiscordSRV at https://www.spigotmc.org/resources/discordsrv.18494/");
                logger.severe("Disabling...");
                main.disable();
                return;
            }
            addMessageFilter();
            try {
                //Reload Configurations
                reloadConfigs();
            } catch (ConfigurationLoadException ex) {
                logger.severe(ex.getMessage());
                main.disable();
                return;
            }
            //set storage string to use later
            String storage = getSqlconfig().isEnabled() ? "MySQL" : "HsqlDB";
            //print startup message
            getServer().getConsoleSender().sendMessage("" +
                    "\n[]=====[&2Enabling DiscordSRVUtils&r]=====[]\n" +
                    "| &cInformation:\n&r" +
                    "|   &cName: &rDiscordSRVUtils\n&r" +
                    "|   &cDevelopers: &rBlueTree242\n&r" +
                    "|   &cVersion: &r" + main.getDescription().getVersion() + "\n&r" +
                    (getVersionConfig().getString("buildNumber").equalsIgnoreCase("NONE") ? "" : "|   &cBuild: &r#" + getVersionConfig().getString("buildNumber") + "\n&r") +
                    "|   &cStorage: &r" + storage + "\n&r" +
                    "| &cSupport:\n&r" +
                    "|   &cGithub: &rhttps://github.com/BlueTree242/BlueDevelopersInc/issues\n" +
                    "|   &cDiscord: &rhttps://discordsrvutils.xyz/support\n" +
                    "[]================================[]");
            System.setProperty("hsqldb.reconfig_logging", "false");
            try {
                Class.forName("github.scarsz.discordsrv.dependencies.jda.api.events.interaction.ButtonClickEvent");
            } catch (ClassNotFoundException e) {
                //DiscordSRV is out of date
                severe("Plugin could not enable because DiscordSRV is missing an important feature (buttons). This means your DiscordSRV is out of date please update it for DSU to work");
                main.disable();
                return;
            }
            //initialize pool
            pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.pool_size(), new ThreadFactory() {
                @Override
                public Thread newThread(@NotNull Runnable r) {

                    return newDSUThread(r);
                }
            });

            Class.forName("tk.bluetree242.discordsrvutils.dependencies.hsqldb.jdbc.JDBCDriver");
            //Register our in game commands
            main.registerCommands();
            try {
                setupDatabase();
            } catch (SQLException ex) {
                //Oh no! could not connect or migrate. Plugin may not start
                startupError(ex, "Error could not connect to database: " + ex.getMessage());
            }
            DiscordSRV.api.subscribe(dsrvlistener);
            if (isReady()) {
                //Uhh, Maybe they are using a pluginmanager and this plugin was enabled after discordsrv is ready
                whenReady();
            }
            whenStarted();
        } catch (Throwable ex) {
            //Plugin couldn't start, sadly
            startupError(ex, "Plugin could not start");
        }
    }

    private void startupError(Throwable ex, @NotNull String msg) {
        main.disable();
        logger.warning(msg);
        try {
            //create a debug report, we know commands don't work after plugin is disabled
            logger.severe(DebugUtil.run(Utils.exceptionToStackTrack(ex)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //tell them where to report
        logger.severe("Send this to support at https://discordsrvutils.xyz/support");
        ex.printStackTrace();
    }

    private void setupDatabase() throws SQLException {
        HikariConfig settings = new HikariConfig();
        String jdbcurl = null;
        String user = null;
        String pass = null;
        if (getSqlconfig().isEnabled()) {
            jdbcurl = "jdbc:mysql://" +
                    getSqlconfig().Host() +
                    ":" + getSqlconfig().Port() + "/" + getSqlconfig().DatabaseName() + "?useSSL=false";
            user = sqlconfig.UserName();
            pass = sqlconfig.Password();
        } else {
            logger.info("MySQL is disabled, using hsqldb");
            jdbcurl = "jdbc:hsqldb:file:" + Paths.get(main.getDataFolder() + fileseparator + "database").resolve("Database") + ";hsqldb.lock_file=false;sql.syntax_mys=true";
            user = "SA";
            pass = "";
        }
        settings.setJdbcUrl(jdbcurl);
        settings.setUsername(user);
        settings.setPassword(pass);
        sql = new HikariDataSource(settings);
        if (!getSqlconfig().isEnabled()) {
            try (Connection conn = sql.getConnection()) {
                //This Prevents Errors when syntax of hsqldb and mysql dismatch
                //language=hsqldb
                String query = "SET DATABASE SQL SYNTAX MYS TRUE;";
                conn.prepareStatement(query).execute();
            }
        }
        //Migrate tables, and others.
        Flyway flyway = Flyway.configure(getClass().getClassLoader())
                .dataSource(sql)
                .baselineOnMigrate(true)
                .locations("classpath:migrations")
                .validateMigrationNaming(true).group(true)
                .table("discordsrvutils_schema")
                .load();
        //repair if there is an issue
        flyway.repair();
        flyway.migrate();
        logger.info("MySQL/HsqlDB Connected & Setup");
    }

    private void initDefaultMessages() {
        //prepare a list of all messages
        String[] messages = new String[]{"afk",
                "ban",
                "level",
                "mute",
                "no-longer-afk",
                "panel",
                "suggestion",
                "suggestion-approved",
                "suggestion-denied",
                "suggestion-noted",
                "suggestion-noted-approved",
                "suggestion-noted-denied",
                "ticket-close",
                "ticket-open",
                "ticket-reopen",
                "unban",
                "unmute",
                "welcome",
                "status-online",
                "status-offline"};
        for (String msg : messages) {
            try {
                //add them to the map
                defaultmessages.put(msg, new String(getResource("messages/" + msg + ".json").readAllBytes()));
            } catch (IOException e) {
                logger.severe("Could not load " + msg + ".json");
            }
        }
    }

    public void onDisable() throws ExecutionException, InterruptedException {
        if (dsrvlistener != null) DiscordSRV.api.unsubscribe(dsrvlistener);
        PluginHookManager.get().removehookAll();
        if (getJDA() != null) {
            for (ListenerAdapter listener : listeners) {
                getJDA().removeEventListener(listener);
            }
            StatusManager.get().unregisterTimer();
            StatusManager.get().editMessage(false).get();
        }
        if (pool != null)
            pool.shutdown();
        if (WaiterManager.get() != null) WaiterManager.get().timer.cancel();
        if (sql != null) sql.close();
        instance = null;
    }

    private void whenStarted() {
        main.addHooks();
        if (messagesDirectory.toFile().mkdir()) {
            defaultmessages.forEach((key, val) -> {
                try {
                    File file = new File(messagesDirectory + fileseparator + key + ".json");
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
        try {
            File levelingRoles = new File(main.getDataFolder() + fileseparator + "leveling-roles.json");
            if (!levelingRoles.exists()) {
                levelingRoles.createNewFile();
                FileWriter writer = new FileWriter(levelingRoles);
                writer.write("{\n\n}");
                writer.close();
                levelingRolesRaw = new JSONObject();
            } else {
                levelingRolesRaw = new JSONObject(Utils.readFile(levelingRoles));
            }
        } catch (FileNotFoundException e) {
            logger.severe("Error creating leveling-roles.json");
            levelingRolesRaw = new JSONObject();
        } catch (IOException e) {
            logger.severe("Error creating leveling-roles.json: " + e.getMessage());
        } catch (JSONException e) {
            logger.severe("Error loading leveling-roles.json: " + e.getMessage());
        }
    }

    public void registerListeners() {
        getJDA().addEventListener(listeners.toArray(new Object[0]));
        main.registerListeners();
        main.addListener(new GameLevelingListener());
        main.addListener(new JoinUpdateChecker());
    }

    /**
     * @return true if plugin enabled and discordsrv ready, else false
     */
    public boolean isReady() {
        if (!isEnabled()) return false;
        return DiscordSRV.isReady;
    }

    public boolean isEnabled() {
        return main.isEnabled();
    }

    public void reloadConfigs() throws IOException, InvalidConfigException {
        configmanager.reloadConfig();
        config = configmanager.reloadConfigData();
        sqlconfigmanager.reloadConfig();
        sqlconfig = sqlconfigmanager.reloadConfigData();
        bansIntegrationconfigmanager.reloadConfig();
        bansIntegrationConfig = bansIntegrationconfigmanager.reloadConfigData();
        ticketsconfigManager.reloadConfig();
        ticketsConfig = ticketsconfigManager.reloadConfigData();
        levelingconfigManager.reloadConfig();
        levelingConfig = levelingconfigManager.reloadConfigData();
        suggestionsConfigManager.reloadConfig();
        suggestionsConfig = suggestionsConfigManager.reloadConfigData();
        statusConfigConfManager.reloadConfig();
        statusConfig = statusConfigConfManager.reloadConfigData();
        //make the leveling roles file
        File levelingRoles = new File(main.getDataFolder() + fileseparator + "leveling-roles.json");
        if (!levelingRoles.exists()) {
            levelingRoles.createNewFile();
            FileWriter writer = new FileWriter(levelingRoles);
            writer.write("{\n\n}");
            writer.close();
            levelingRolesRaw = new JSONObject();
        } else {
            levelingRolesRaw = new JSONObject(Utils.readFile(levelingRoles));
        }
        setSettings();
    }

    public void updateCheck(PlatformPlayer p) {
        executeAsync(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject versionConfig = DiscordSRVUtils.get().getVersionConfig();
                MultipartBody form = new MultipartBody.Builder().setType(MediaType.get("multipart/form-data"))
                        .addFormDataPart("version", DiscordSRVUtils.get().getDescription().getVersion())
                        .addFormDataPart("buildNumber", versionConfig.getString("buildNumber"))
                        .addFormDataPart("commit", versionConfig.getString("commit"))
                        .addFormDataPart("buildDate", versionConfig.getString("buildDate"))
                        .addFormDataPart("devUpdatechecker", DiscordSRVUtils.get().getMainConfig().dev_updatechecker() + "")
                        .build();

                Request req = new Request.Builder().url("https://discordsrvutils.xyz/updatecheck").post(form).build();
                Response response = client.newCall(req).execute();
                JSONObject res = new JSONObject(response.body().string());
                response.close();
                int versions_behind = res.getInt("versions_behind");
                if (res.isNull("message")) {
                    if (versions_behind != 0) {
                        p.sendMessage("&7[&eDSU&7] &cPlugin is " + versions_behind + " versions behind. Please Update. Click to Download");
                    }
                } else {
                    p.sendMessage("&7[&eDSU&7] &c" + res.getString("message"));
                }
            } catch (Exception ex) {
                DiscordSRVUtils.get().getLogger().severe("Could not check for updates: " + ex.getMessage());
            }

        });
    }

    public Logger getLogger() {
        return main.getLogger();
    }

    public PlatformPluginDescription getDescription() {
        return main.getDescription();
    }

    public void updateCheck() {
        executeAsync(() -> {
            //do updatechecker
            try {
                if (!isEnabled()) return;
                OkHttpClient client = new OkHttpClient();
                JSONObject versionConfig = getVersionConfig();
                MultipartBody form = new MultipartBody.Builder().setType(MediaType.get("multipart/form-data"))
                        .addFormDataPart("version", getDescription().getVersion())
                        .addFormDataPart("buildNumber", versionConfig.getString("buildNumber"))
                        .addFormDataPart("commit", versionConfig.getString("commit"))
                        .addFormDataPart("buildDate", versionConfig.getString("buildDate"))
                        .build();

                Request req = new Request.Builder().url("https://discordsrvutils.xyz/updatecheck").post(form).build();
                Response response = client.newCall(req).execute();
                JSONObject res = new JSONObject(response.body().string());
                response.close();
                int versions_behind = res.getInt("versions_behind");
                String logger = res.getString("type") != null ? res.getString("type") : "INFO";
                String msg = null;
                if (res.isNull("message")) {
                    if (versions_behind != 0) {
                        if (logger.equalsIgnoreCase("INFO")) {

                        }
                        msg = (Utils.colors("&cPlugin is " + versions_behind + " versions behind. Please Update. Download from " + res.getString("downloadUrl")));
                    } else {
                        msg = (Utils.colors("&aPlugin is up to date!"));
                    }
                } else {
                    //the updatechecker wants its own message
                    String message = res.getString("message");
                    if (message.contains(res.getString("downloadUrl"))) {
                        msg = message;
                    } else {
                        msg = message + " Download from " + res.getString("downloadUrl");
                    }
                }
                switch (logger) {
                    case "INFO":
                        getLogger().info(Utils.colors(msg));
                        break;
                    case "WARNING":
                        getLogger().warning(Utils.colors(msg));
                        break;
                    case "ERROR":
                        getLogger().warning(Utils.colors(msg));
                        break;
                }
            } catch (Exception e) {
                //We could not check for updates.
                logger.severe("Could not check for updates: " + e.getMessage());
            }

        });
    }

    public Config getMainConfig() {
        return config;
    }

    public SQLConfig getSqlconfig() {
        return sqlconfig;
    }

    public SuggestionsConfig getSuggestionsConfig() {
        return suggestionsConfig;
    }

    public PunishmentsIntegrationConfig getBansConfig() {
        return bansIntegrationConfig;
    }

    public TicketsConfig getTicketsConfig() {
        return ticketsConfig;
    }

    public LevelingConfig getLevelingConfig() {
        return levelingConfig;
    }

    public StatusConfig getStatusConfig() {
        return statusConfig;
    }

    public void executeAsync(Runnable r) {
        if (pool == null || pool.isShutdown()) return;
        pool.execute(r);
    }

    private void addMessageFilter() {
        try {
            Class messageFilter = Class.forName("tk.bluetree242.discordsrvutils.other.MessageFilter");
            ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter((Filter) messageFilter.newInstance());
        } catch (Exception e) {
            severe("Failed to add Message Filter");
            e.printStackTrace();
        }
    }

    public void whenReady() {
        //do it async, fixing tickets and suggestions can take long time
        executeAsync(() -> {
            registerListeners();
            setSettings();
            PluginHookManager.get().hookAll();
            //remove the discordsrv LinkAccount listener via reflections
            if (getMainConfig().remove_discordsrv_link_listener()) {
                for (Object listener : getJDA().getEventManager().getRegisteredListeners()) {
                    if (listener.getClass().getName().equals("github.scarsz.discordsrv.listeners.DiscordAccountLinkListener")) {
                        getJDA().removeEventListener(listener);
                        removedDiscordSRVAccountLinkListener = true;
                    }
                }
            }
            //fix issues with any ticket or panel
            TicketManager.get().fixTickets();
            voteMode = SuggestionVoteMode.valueOf(suggestionsConfig.suggestions_vote_mode().toUpperCase()) == null ? SuggestionVoteMode.REACTIONS : SuggestionVoteMode.valueOf(suggestionsConfig.suggestions_vote_mode().toUpperCase());
            //migrate suggestion buttons/reactions if needed
            SuggestionManager.get().migrateSuggestions();
            StatusManager.get().editMessage(true);
            StatusManager.get().registerTimer();
            logger.info("Plugin is ready to function.");
        });

    }

    public JSONObject getVersionConfig() throws IOException {
        return new JSONObject(new String(getResource("version-config.json").readAllBytes()));
    }

    public void setSettings() {
        if (!isReady()) return;
        OnlineStatus onlineStatus = getMainConfig().onlinestatus().equalsIgnoreCase("DND") ? OnlineStatus.DO_NOT_DISTURB : OnlineStatus.valueOf(getMainConfig().onlinestatus().toUpperCase());
        getJDA().getPresence().setStatus(onlineStatus);
        LevelingManager.get().cachedUUIDS.refreshAll(LevelingManager.get().cachedUUIDS.asMap().keySet());
        if (StatusListener.get() != null) {
            if (StatusListener.get().registered) {
                StatusListener.get().unregister();
            }
            StatusListener.get().register();
            StatusManager.get().reloadTimer();
        }
    }

    public JDA getJDA() {
        return getDiscordSRV().getJDA();
    }

    public MessageManager getMessageManager() {
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

    public List<Role> getAdminsRoles() {
        List<Role> roles = new ArrayList<>();
        for (Long lng : getAdminIds()) {
            roles.add(getGuild().getRoleById(lng));
        }
        return roles;
    }

    public void severe(String sv) {
        getLogger().severe(sv);
        for (PlatformPlayer p : getPlatform().getServer().getOnlinePlayers()) {
            if (p.hasPermission("discordsrvutils.errornotifications"))
                //tell admins that something was wrong
                p.sendMessage("&7[&eDSU&7] &c" + sv);
        }
    }

    public boolean isAdmin(long id) {
        if (getAdminIds().contains(id)) return true;
        Member member = getGuild().retrieveMemberById(id).complete();
        if (member != null) {
            for (Role role : member.getRoles()) {
                if (getAdminIds().contains(role.getIdLong())) return true;
            }
        }
        return false;
    }

    public String getCommandPrefix() {
        return config.prefix();
    }

    public Connection getDatabase() throws SQLException {
        return sql.getConnection();
    }

    public TextChannel getChannel(long id, TextChannel channel) {
        if (id == -1) {
            return channel;
        }
        if (id == 0) {
            return getDiscordSRV().getMainChatChannel();
        } else return getJDA().getTextChannelById(id);
    }

    public TextChannel getChannel(long id) {
        return getChannel(id, null);
    }

    public Guild getGuild() {
        return getDiscordSRV().getMainGuild();
    }

    public <U> CompletableFuture<U> completableFuture(Supplier<U> v) {
        return CompletableFuture.supplyAsync(v, pool);
    }

    public CompletableFuture<Void> completableFutureRun(Runnable r) {
        return CompletableFuture.runAsync(r, pool);
    }

    public RestAction<Message> queueMsg(Message msg, MessageChannel channel) {
        return channel.sendMessage(msg);
    }

    public <U> void handleCF(CompletableFuture<U> cf, Consumer<U> success, Consumer<Throwable> failure) {
        if (success != null) cf.thenAcceptAsync(success);
        cf.handle((e, x) -> {
            Exception ex = (Exception) x.getCause();
            while (ex instanceof ExecutionException) ex = (Exception) ex.getCause();
            if (failure != null) {
                failure.accept(ex);
            } else defaultHandle(ex);
            return x;
        });
    }

    /**
     * For doing a cf inside another one
     */
    public <U> U handleCFOnAnother(CompletableFuture<U> cf) {
        try {
            return cf.get();
        } catch (ExecutionException | InterruptedException ex) {
            Exception e = ex;
            while (ex instanceof ExecutionException) e = (Exception) ex.getCause();
            throw (RuntimeException) e;
        }
    }

    public void defaultHandle(Throwable ex, MessageChannel channel) {
        //send message for errors
        channel.sendMessage(Embed.error("An error happened. Check Console for details")).queue();
        logger.severe("The following error have a high chance to be caused by DiscordSRVUtils. Report at https://discordsrvutils.xyz/support and not discordsrv's Discord.");
        ex.printStackTrace();
    }

    public void defaultHandle(Throwable ex) {
        //handle error on thread pool
        if (!config.minimize_errors()) {
            logger.warning("The following error have a high chance to be caused by DiscordSRVUtils. Report at https://discordsrvutils.xyz/support and not discordsrv's Discord.");

            ex.printStackTrace();
            logger.warning("Read the note above the error Please.");
            //don't spam errors
            if ((System.currentTimeMillis() - lastErrorTime) >= 180000)
                for (PlatformPlayer p : getServer().getOnlinePlayers()) {
                    if (p.hasPermission("discordsrvutils.errornotifications")) {
                        //tell admins that something was wrong
                        p.sendMessage("&7[&eDSU&7] Plugin had an error. Check console for details. Support at https://discordsrvutils.xyz/support");
                    }
                }
            lastErrorTime = System.currentTimeMillis();

        } else {
            logger.severe("DiscordSRVUtils had an error. Error minimization enabled.");
        }
        finalError = Utils.exceptionToStackTrack(ex);
    }


    // Allow the user to set variables inside SpEL Strings and expression turns just nothing
    public String execute(Object o) {
        return "";
    }


    //fast getters

    private InputStream getResource(String s) {
        return main.getResource(s);
    }

}
