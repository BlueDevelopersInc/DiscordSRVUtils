/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2022 BlueTree242
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

package tk.bluetree242.discordsrvutils;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.OnlineStatus;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageChannel;
import github.scarsz.discordsrv.dependencies.jda.api.requests.GatewayIntent;
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import github.scarsz.discordsrv.dependencies.jda.api.utils.cache.CacheFlag;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import space.arim.dazzleconf.error.InvalidConfigException;
import tk.bluetree242.discordsrvutils.config.*;
import tk.bluetree242.discordsrvutils.database.DatabaseManager;
import tk.bluetree242.discordsrvutils.exceptions.ConfigurationLoadException;
import tk.bluetree242.discordsrvutils.hooks.PluginHookManager;
import tk.bluetree242.discordsrvutils.listeners.bukkit.JoinUpdateChecker;
import tk.bluetree242.discordsrvutils.listeners.discordsrv.DiscordSRVListener;
import tk.bluetree242.discordsrvutils.other.MessageFilter;
import tk.bluetree242.discordsrvutils.platform.PlatformDiscordSRV;
import tk.bluetree242.discordsrvutils.platform.PlatformServer;
import tk.bluetree242.discordsrvutils.platform.PluginPlatform;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandManager;
import tk.bluetree242.discordsrvutils.systems.invitetracking.InviteTrackingManager;
import tk.bluetree242.discordsrvutils.systems.leveling.LevelingManager;
import tk.bluetree242.discordsrvutils.systems.leveling.listeners.game.GameLevelingListener;
import tk.bluetree242.discordsrvutils.systems.messages.MessageManager;
import tk.bluetree242.discordsrvutils.systems.status.StatusManager;
import tk.bluetree242.discordsrvutils.systems.suggestions.SuggestionManager;
import tk.bluetree242.discordsrvutils.systems.tickets.TicketManager;
import tk.bluetree242.discordsrvutils.updatechecker.UpdateChecker;
import tk.bluetree242.discordsrvutils.waiter.WaiterManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DiscordSRVUtils {


    //instance for DiscordSRVUtils.get()
    private static DiscordSRVUtils instance;
    //file separator string
    public final String fileseparator = System.getProperty("file.separator");
    private final MessageFilter messageFilter = new MessageFilter(this);
    private final PluginPlatform main;
    @Getter
    private final MessageManager messageManager = new MessageManager(this);
    @Getter
    private final CommandManager commandManager = new CommandManager(this);
    @Getter
    private final TicketManager ticketManager = new TicketManager(this);
    @Getter
    private final WaiterManager waiterManager = new WaiterManager(this);
    @Getter
    private final LevelingManager levelingManager = new LevelingManager(this);
    @Getter
    private final SuggestionManager suggestionManager = new SuggestionManager(this);
    @Getter
    private final StatusManager statusManager = new StatusManager(this);
    @Getter
    private final PluginHookManager pluginHookManager = new PluginHookManager(this);
    @Getter
    private final AsyncManager asyncManager = new AsyncManager(this);
    @Getter
    private final JdaManager jdaManager = new JdaManager(this);
    @Getter
    private final ErrorHandler errorHandler = new ErrorHandler(this);
    @Getter
    private final UpdateChecker updateChecker = new UpdateChecker(this);
    @Getter
    private final DatabaseManager databaseManager = new DatabaseManager(this);
    //latest error that occurred on our thread pool
    //Plugins we hooked into
    // faster getter for the logger

    @Getter
    private final InviteTrackingManager inviteTrackingManager = new InviteTrackingManager(this);
    @Getter
    public Logger logger;
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
    private SuggestionsConfig suggestionsConfig;
    private ConfManager<SQLConfig> sqlconfigmanager;
    @Getter
    private StatusConfig statusConfig;
    //Our DiscordSRV Listener
    private DiscordSRVListener dsrvlistener;

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
        dsrvlistener = new DiscordSRVListener(this);
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
            DiscordSRV.api.requireIntent(GatewayIntent.GUILD_INVITES);
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
            messageFilter.add();
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
                Class.forName("github.scarsz.discordsrv.api.ApiManager").getDeclaredMethod("addSlashCommandProvider", SlashCommandProvider.class);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                //DiscordSRV is out of date
                severe("Plugin could not enable because DiscordSRV is missing an important feature. This means your DiscordSRV is outdated, please update it for DSU to work");
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


    public void onDisable() {
        if (dsrvlistener != null) DiscordSRV.api.unsubscribe(dsrvlistener);
        messageFilter.remove();
        pluginHookManager.removeHookAll();
        jdaManager.removeListeners();
        if (getJDA() != null) {
            statusManager.unregisterTimer();
            statusManager.editMessage(false);
        }
        asyncManager.stop();
        waiterManager.timer.cancel();
        waiterManager.getWaiters().forEach(w -> w.expire(true));
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
        main.addListener(new GameLevelingListener(this));
        main.addListener(new JoinUpdateChecker(this));
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
        setSettings(false);
    }

    public void whenReady() {
        //do it async, fixing tickets and suggestions can take long time
        asyncManager.executeAsync(() -> {
            registerListeners();
            setSettings(true);
            pluginHookManager.hookAll();
            if (!inviteTrackingManager.cacheInvites())
                errorHandler.severe("Bot does not have the MANAGE_SERVER permission, we cannot make detect inviter when someone joins, please grant the permission.");
            //fix issues with any ticket or panel
            ticketManager.fixTickets();
            //migrate suggestion buttons/reactions if needed
            suggestionManager.migrateSuggestions();
            statusManager.editMessage(true);
            statusManager.registerTimer();
            logger.info("Plugin is ready to function.");
        });

    }

    public void setSettings(boolean first) {
        if (!isReady()) return;
        if (!first) DiscordSRV.api.updateSlashCommands();
        OnlineStatus onlineStatus = getMainConfig().onlinestatus().equalsIgnoreCase("DND") ? OnlineStatus.DO_NOT_DISTURB : OnlineStatus.valueOf(getMainConfig().onlinestatus().toUpperCase());
        getJDA().getPresence().setStatus(onlineStatus);
        levelingManager.cachedUUIDS.invalidateAll();
        if (main.getStatusListener() != null) {
            if (main.getStatusListener().registered) {
                main.getStatusListener().unregister();
            }
            main.getStatusListener().register();
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
        return new JSONObject(new String(IOUtils.toByteArray(getPlatform().getResource("version-config.json"))));
    }

    public void severe(String sv) {
        errorHandler.severe(sv);
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
