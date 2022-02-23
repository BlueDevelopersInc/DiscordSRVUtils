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

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.OnlineStatus;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.requests.GatewayIntent;
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import github.scarsz.discordsrv.dependencies.jda.api.utils.cache.CacheFlag;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.json.JSONObject;
import space.arim.dazzleconf.error.InvalidConfigException;
import tk.bluetree242.discordsrvutils.config.*;
import tk.bluetree242.discordsrvutils.exceptions.ConfigurationLoadException;
import tk.bluetree242.discordsrvutils.hooks.PluginHookManager;
import tk.bluetree242.discordsrvutils.listeners.bukkit.JoinUpdateChecker;
import tk.bluetree242.discordsrvutils.listeners.discordsrv.DiscordSRVListener;
import tk.bluetree242.discordsrvutils.platform.*;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandManager;
import tk.bluetree242.discordsrvutils.systems.leveling.LevelingManager;
import tk.bluetree242.discordsrvutils.systems.leveling.listeners.game.GameLevelingListener;
import tk.bluetree242.discordsrvutils.systems.messages.MessageManager;
import tk.bluetree242.discordsrvutils.systems.status.StatusListener;
import tk.bluetree242.discordsrvutils.systems.status.StatusManager;
import tk.bluetree242.discordsrvutils.systems.suggestions.SuggestionManager;
import tk.bluetree242.discordsrvutils.systems.suggestions.SuggestionVoteMode;
import tk.bluetree242.discordsrvutils.systems.tickets.TicketManager;
import tk.bluetree242.discordsrvutils.updatechecker.UpdateChecker;
import tk.bluetree242.discordsrvutils.waiter.WaiterManager;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class DiscordSRVUtils {


    //instance for DiscordSRVUtils.get()
    private static DiscordSRVUtils instance;
    //file separator string
    public final String fileseparator = System.getProperty("file.separator");
    private final PluginPlatform main;
    //Mode for suggestions voting
    public SuggestionVoteMode voteMode;
    //latest error that occurred on our thread pool
    //Plugins we hooked into
    // faster getter for the logger
    @Getter public Logger logger;
    //Configurations
    private ConfManager<Config> configManager;
    private ConfManager<PunishmentsIntegrationConfig> bansIntegrationconfigmanager;
    private ConfManager<TicketsConfig> ticketsconfigManager;
    private Config config;
    private ConfManager<LevelingConfig> levelingconfigManager;
    @Getter
    private SQLConfig sqlconfig;
    private ConfManager<SuggestionsConfig> suggestionsConfigManager;
    @Getter
    private PunishmentsIntegrationConfig bansConfig;
    private ConfManager<StatusConfig> statusConfigConfManager;
    @Getter
    private TicketsConfig ticketsConfig;
    @Getter
    private LevelingConfig levelingConfig;
    //was the DiscordSRV AccountLink Listener Removed?
    @Getter
    private boolean removedDiscordSRVAccountLinkListener = false;
    @Getter
    private SuggestionsConfig suggestionsConfig;
    private ConfManager<SQLConfig> sqlconfigmanager;
    @Getter
    private StatusConfig statusConfig;
    //Our DiscordSRV Listener
    private DiscordSRVListener dsrvlistener;
    @Getter
    private AsyncManager asyncManager = new AsyncManager(this);
    @Getter
    private JdaManager jdaManager = new JdaManager(this);
    @Getter
    private ErrorHandler errorHandler = new ErrorHandler(this);
    @Getter
    private UpdateChecker updateChecker = new UpdateChecker(this);
    @Getter
    private DatabaseManager databaseManager;
    @Getter
    private final MessageManager messageManager = new MessageManager(this);
    @Getter private final CommandManager commandManager = new CommandManager(this);
    @Getter private final TicketManager ticketManager = new TicketManager(this);
    @Getter private final WaiterManager waiterManager = new WaiterManager(this);
    @Getter private final LevelingManager levelingManager = new LevelingManager(this);
    @Getter private final SuggestionManager suggestionManager = new SuggestionManager(this);
    @Getter private final StatusManager statusManager = new StatusManager(this);
    @Getter private final PluginHookManager pluginHookManager = new PluginHookManager(this);
    public DiscordSRVUtils(PluginPlatform main) {
        this.main = main;
        initConfigs();
        logger = main.getLogger();
        onLoad();
    }

    public static DiscordSRVUtils get() {
        return instance;
    }

    public PlatformServer getServer() {
        return getPlatform().getServer();
    }

    public PluginPlatform getPlatform() {
        return main;
    }

    public PlatformDiscordSRV getDiscordSRV() {
        return getPlatform().getDiscordSRV();
    }

    private void init() {
        //set the instance
        instance = this;
        //initialize discordsrv listener
        dsrvlistener = new DiscordSRVListener();
        //Initialize Managers
    }

    private void initConfigs() {
        configManager = ConfManager.create(main.getDataFolder().toPath(), "config.yml", Config.class);
        sqlconfigmanager = ConfManager.create(main.getDataFolder().toPath(), "sql.yml", SQLConfig.class);
        bansIntegrationconfigmanager = ConfManager.create(main.getDataFolder().toPath(), "PunishmentsIntegration.yml", PunishmentsIntegrationConfig.class);
        ticketsconfigManager = ConfManager.create(main.getDataFolder().toPath(), "tickets.yml", TicketsConfig.class);
        levelingconfigManager = ConfManager.create(main.getDataFolder().toPath(), "leveling.yml", LevelingConfig.class);
        suggestionsConfigManager = ConfManager.create(main.getDataFolder().toPath(), "suggestions.yml", SuggestionsConfig.class);
        statusConfigConfManager = ConfManager.create(main.getDataFolder().toPath(), "status.yml", StatusConfig.class);
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
        updateChecker.updateCheck();
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
            try {
                Class.forName("github.scarsz.discordsrv.dependencies.jda.api.events.interaction.ButtonClickEvent");
            } catch (ClassNotFoundException e) {
                //DiscordSRV is out of date
                severe("Plugin could not enable because DiscordSRV is missing an important feature (buttons). This means your DiscordSRV is out of date please update it for DSU to work");
                main.disable();
                return;
            }
            asyncManager.start();
            //Register our in game commands
            main.registerCommands();
            try {
                databaseManager.setupDatabase();
            } catch (SQLException ex) {
                //Oh no! could not connect or migrate. Plugin may not start
                errorHandler.startupError(ex, "Error could not connect to database: " + ex.getMessage());
            }
            DiscordSRV.api.subscribe(dsrvlistener);
            if (isReady()) {
                //Uhh, Maybe they are using a pluginmanager and this plugin was enabled after discordsrv is ready
                whenReady();
            }
            whenStarted();
        } catch (Throwable ex) {
            //Plugin couldn't start, sadly
            errorHandler.startupError(ex, "Plugin could not start");
        }
    }


    public void onDisable() throws ExecutionException, InterruptedException {
        if (dsrvlistener != null) DiscordSRV.api.unsubscribe(dsrvlistener);
        pluginHookManager.removeHookAll();
        jdaManager.removeListeners();
        if (getJDA() != null) {
            statusManager.unregisterTimer();
            statusManager.editMessage(false).get();
        }
        asyncManager.stop();
        if (waiterManager != null) waiterManager.timer.cancel();
        databaseManager.close();
        instance = null;
    }

    private void whenStarted() {
        main.addHooks();
        messageManager.initDefaultMessages();
        messageManager.init();
    }

    public void registerListeners() {
        jdaManager.registerListeners();
        main.registerListeners();
        main.addListener(new GameLevelingListener());
        main.addListener(new JoinUpdateChecker());
    }

    public void reloadConfigs() throws IOException, InvalidConfigException {
        configManager.reloadConfig();
        config = configManager.reloadConfigData();
        sqlconfigmanager.reloadConfig();
        sqlconfig = sqlconfigmanager.reloadConfigData();
        bansIntegrationconfigmanager.reloadConfig();
        bansConfig = bansIntegrationconfigmanager.reloadConfigData();
        ticketsconfigManager.reloadConfig();
        ticketsConfig = ticketsconfigManager.reloadConfigData();
        levelingconfigManager.reloadConfig();
        levelingConfig = levelingconfigManager.reloadConfigData();
        suggestionsConfigManager.reloadConfig();
        suggestionsConfig = suggestionsConfigManager.reloadConfigData();
        statusConfigConfManager.reloadConfig();
        statusConfig = statusConfigConfManager.reloadConfigData();
        levelingManager.reloadLevelingRoles();
        setSettings();
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
        asyncManager.executeAsync(() -> {
            registerListeners();
            setSettings();
            pluginHookManager.hookAll();
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
            ticketManager.fixTickets();
            voteMode = SuggestionVoteMode.valueOf(suggestionsConfig.suggestions_vote_mode().toUpperCase());
            //migrate suggestion buttons/reactions if needed
            suggestionManager.migrateSuggestions();
            statusManager.editMessage(true);
            statusManager.registerTimer();
            logger.info("Plugin is ready to function.");
        });

    }

    public void setSettings() {
        if (!isReady()) return;
        commandManager.addSlashCommands();
        OnlineStatus onlineStatus = getMainConfig().onlinestatus().equalsIgnoreCase("DND") ? OnlineStatus.DO_NOT_DISTURB : OnlineStatus.valueOf(getMainConfig().onlinestatus().toUpperCase());
        getJDA().getPresence().setStatus(onlineStatus);
        levelingManager.cachedUUIDS.refreshAll(levelingManager.cachedUUIDS.asMap().keySet());
        if (StatusListener.get() != null) {
            if (StatusListener.get().registered) {
                StatusListener.get().unregister();
            }
            StatusListener.get().register();
            statusManager.reloadTimer();
        }
    }

    public RestAction<Message> queueMsg(Message msg, MessageChannel channel) {
        return channel.sendMessage(msg);
    }

    public JDA getJDA() {
        return getDiscordSRV().getJDA();
    }


    public JSONObject getVersionConfig() throws IOException {
        return new JSONObject(new String(getPlatform().getResource("version-config.json").readAllBytes()));
    }

    public void severe(String sv) {
        errorHandler.severe(sv);
    }


    public Connection getDatabase() throws SQLException {
        return databaseManager.getConnection();
    }

    public TextChannel getChannel(long id, TextChannel channel) {
        return jdaManager.getChannel(id, channel);
    }

    public TextChannel getChannel(long id) {
        return getChannel(id, null);
    }

    public Guild getGuild() {
        return getDiscordSRV().getMainGuild();
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

    public Config getMainConfig() {
        return config;
    }

    // Allow the user to set variables inside SpEL Strings and expression turns just nothing
    public String execute(Object o) {
        return "";
    }

}
