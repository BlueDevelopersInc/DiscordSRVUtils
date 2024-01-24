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

package dev.bluetree242.discordsrvutils;

import dev.bluetree242.discordsrvutils.config.*;
import dev.bluetree242.discordsrvutils.database.DatabaseManager;
import dev.bluetree242.discordsrvutils.exceptions.ConfigurationLoadException;
import dev.bluetree242.discordsrvutils.exceptions.InvalidResponseException;
import dev.bluetree242.discordsrvutils.hooks.PluginHookManager;
import dev.bluetree242.discordsrvutils.listeners.discordsrv.DiscordSRVListener;
import dev.bluetree242.discordsrvutils.listeners.game.JoinUpdateChecker;
import dev.bluetree242.discordsrvutils.other.MessageFilter;
import dev.bluetree242.discordsrvutils.platform.PlatformDiscordSRV;
import dev.bluetree242.discordsrvutils.platform.PlatformServer;
import dev.bluetree242.discordsrvutils.platform.PluginPlatform;
import dev.bluetree242.discordsrvutils.systems.commands.discord.CommandManager;
import dev.bluetree242.discordsrvutils.systems.invitetracking.InviteTrackingManager;
import dev.bluetree242.discordsrvutils.systems.leveling.LevelingManager;
import dev.bluetree242.discordsrvutils.systems.leveling.LevelingRewardsManager;
import dev.bluetree242.discordsrvutils.systems.leveling.listeners.game.GameLevelingListener;
import dev.bluetree242.discordsrvutils.systems.messages.MessageManager;
import dev.bluetree242.discordsrvutils.systems.status.StatusManager;
import dev.bluetree242.discordsrvutils.systems.suggestions.SuggestionManager;
import dev.bluetree242.discordsrvutils.systems.tickets.TicketManager;
import dev.bluetree242.discordsrvutils.updatechecker.UpdateChecker;
import dev.bluetree242.discordsrvutils.waiter.WaiterManager;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.OnlineStatus;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageChannel;
import github.scarsz.discordsrv.dependencies.jda.api.requests.GatewayIntent;
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import github.scarsz.discordsrv.dependencies.jda.api.utils.cache.CacheFlag;
import lombok.Getter;
import space.arim.dazzleconf.error.InvalidConfigException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DiscordSRVUtils {
    // Instance for DiscordSRVUtils.get()
    private static DiscordSRVUtils instance;
    @Getter
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
    private final LevelingManager levelingManager = new LevelingManager(this, new LevelingRewardsManager(this));
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

    @Getter
    private final InviteTrackingManager inviteTrackingManager = new InviteTrackingManager(this);
    @Getter
    public Logger logger;
    // Configurations
    private ConfManager<Config> configManager;
    private ConfManager<PunishmentsIntegrationConfig> bansIntegrationConfigManager;
    private ConfManager<TicketsConfig> ticketsConfigManager;
    private Config config;
    private ConfManager<LevelingConfig> levelingConfigManager;
    @Getter
    private SQLConfig sqlconfig;
    private ConfManager<SuggestionsConfig> suggestionsConfigManager;
    @Getter
    private PunishmentsIntegrationConfig bansConfig;
    private ConfManager<StatusConfig> statusConfigManager;
    @Getter
    private TicketsConfig ticketsConfig;
    @Getter
    private LevelingConfig levelingConfig;
    // Was the DiscordSRV AccountLink Listener Removed?
    @Getter
    private SuggestionsConfig suggestionsConfig;
    private ConfManager<SQLConfig> sqlConfigManager;
    @Getter
    private StatusConfig statusConfig;
    // Our DiscordSRV Listener
    private DiscordSRVListener dsrvListener;

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
        // Set the instance
        instance = this;
        // Initialize discordsrv listener
        dsrvListener = new DiscordSRVListener(this);
        // Initialize Managers
    }

    private void initConfigs() {
        configManager = ConfManager.create(main.getDataFolder().toPath(), "config.yml", Config.class);
        sqlConfigManager = ConfManager.create(main.getDataFolder().toPath(), "sql.yml", SQLConfig.class);
        bansIntegrationConfigManager = ConfManager.create(main.getDataFolder().toPath(), "PunishmentsIntegration.yml", PunishmentsIntegrationConfig.class);
        ticketsConfigManager = ConfManager.create(main.getDataFolder().toPath(), "tickets.yml", TicketsConfig.class);
        levelingConfigManager = ConfManager.create(main.getDataFolder().toPath(), "leveling.yml", LevelingConfig.class);
        suggestionsConfigManager = ConfManager.create(main.getDataFolder().toPath(), "suggestions.yml", SuggestionsConfig.class);
        statusConfigManager = ConfManager.create(main.getDataFolder().toPath(), "status.yml", StatusConfig.class);
    }

    public void onLoad() {
        init();
        // Require intents and cacheflags
        if (main.getServer().isPluginInstalled("DiscordSRV")) {
            if (DiscordSRV.isReady) {
                // Oh no, they are using a plugin manager to reload the plugin, give them a warning
                logger.warning("It seems like you are using a plugin manager to reload the plugin. This is not a good practice. If you see problems, Please restart");
                return;
            }
            DiscordSRV.api.requireIntent(GatewayIntent.GUILD_MESSAGE_REACTIONS);
            DiscordSRV.api.requireIntent(GatewayIntent.GUILD_INVITES);
            DiscordSRV.api.requireCacheFlag(CacheFlag.EMOTE);
        }
    }

    public void onEnable() {
        try {
            if (!main.getServer().isPluginEnabled("DiscordSRV")) {
                logger.severe("DiscordSRV is not installed or failed to start. Download DiscordSRV at https://www.spigotmc.org/resources/discordsrv.18494/");
                logger.severe("Disabling...");
                main.disable();
                return;
            }
            try {
                // Reload Configurations
                reloadConfigs(true);
            } catch (ConfigurationLoadException ex) {
                logger.severe(ex.getMessage());
                main.disable();
                return;
            }
            // Set storage string to use later
            String storage = getSqlconfig().isEnabled() ? "MySQL" : "HsqlDB";
            // Print startup message
            getServer().getConsoleSender().sendMessage("\n[]=====[&2Enabling DiscordSRVUtils&r]=====[]\n" +
                    "| &cInformation:\n&r" +
                    "|   &cName: &rDiscordSRVUtils\n&r" +
                    "|   &cDevelopers: &rBlueTree242\n&r" +
                    "|   &cVersion: &r" + main.getDescription().getVersion() + "\n&r" +
                    (VersionInfo.BUILD_NUMBER.equalsIgnoreCase("NONE") ? "" : "|   &cBuild: &r#" + VersionInfo.BUILD_NUMBER + "\n&r") +
                    "|   &cStorage: &r" + storage + "\n&r" +
                    "| &cSupport:\n&r" +
                    "|   &cGithub: &rhttps://github.com/BlueTree242/BlueDevelopersInc/issues\n" +
                    "|   &cWiki: &rhttps://wiki.discordsrvutils.xyz\n" +
                    "|   &cDiscord: &rhttps://discordsrvutils.xyz/support\n" +
                    "[]====================================[]");
            asyncManager.start();
            // Register our in game commands
            main.registerCommands();
            try {
                databaseManager.setupDatabase();
            } catch (SQLException ex) {
                // Oh, no! could not connect or migrate. Plugin may not start
                errorHandler.startupError(ex, "Error could not connect to database: " + ex.getMessage());
                getPlatform().disable();
                return;
            }
            DiscordSRV.api.subscribe(dsrvListener);
            if (isReady()) {
                // Uhh, Maybe they are using a plugin manager and this plugin was enabled after discordsrv is ready
                whenReady();
            }
            whenStarted();
            getServer().runAsync(() -> {
                try {
                    UpdateChecker.UpdateCheckResult result = getUpdateChecker().updateCheck(true);
                } catch (InvalidResponseException e) {
                    getLogger().severe(e.getFriendlyMessage());
                }
            });
        } catch (Throwable ex) {
            // Plugin couldn't start, sadly
            errorHandler.startupError(ex, "Plugin could not start");
        }
    }


    public void onDisable() {
        if (dsrvListener != null) DiscordSRV.api.unsubscribe(dsrvListener);
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
        reloadConfigs(false);
    }

    public void reloadConfigs(boolean first) throws IOException, InvalidConfigException {
        configManager.reloadConfig();
        config = configManager.reloadConfigData();
        sqlConfigManager.reloadConfig();
        sqlconfig = sqlConfigManager.reloadConfigData();
        bansIntegrationConfigManager.reloadConfig();
        bansConfig = bansIntegrationConfigManager.reloadConfigData();
        ticketsConfigManager.reloadConfig();
        ticketsConfig = ticketsConfigManager.reloadConfigData();
        levelingConfigManager.reloadConfig();
        levelingConfig = levelingConfigManager.reloadConfigData();
        suggestionsConfigManager.reloadConfig();
        suggestionsConfig = suggestionsConfigManager.reloadConfigData();
        statusConfigManager.reloadConfig();
        statusConfig = statusConfigManager.reloadConfigData();
        levelingManager.getLevelingRewardsManager().reloadLevelingRewards();
        levelingManager.getLevelingRewardsManager().reloadRewardCache();
        setSettings(first);
    }

    public void whenReady() {
        // Do it async, fixing tickets and suggestions can take long time
        asyncManager.executeAsync(() -> {
            registerListeners();
            setSettings(true);
            pluginHookManager.hookAll();
            if (!inviteTrackingManager.cacheInvites())
                errorHandler.severe("Bot does not have the MANAGE_SERVER permission, we cannot make detect inviter when someone joins, please grant the permission.");
            // Fix issues with any ticket or panel
            ticketManager.updateTickets();
            // Migrate suggestion buttons/reactions if needed
            suggestionManager.migrateSuggestions();
            statusManager.registerTimer();
            logger.info("Plugin is ready to function.");
        });

    }

    public void setSettings(boolean first) {
        if (!isReady()) return;
        if (!first) DiscordSRV.api.updateSlashCommands();
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
