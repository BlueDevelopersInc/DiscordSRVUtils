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
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import github.scarsz.discordsrv.dependencies.jda.api.requests.GatewayIntent;
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import github.scarsz.discordsrv.dependencies.jda.api.utils.cache.CacheFlag;
import github.scarsz.discordsrv.dependencies.okhttp3.*;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import space.arim.dazzleconf.error.InvalidConfigException;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandListener;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandManager;
import tk.bluetree242.discordsrvutils.commands.bukkit.DiscordSRVUtilsCommand;
import tk.bluetree242.discordsrvutils.commands.bukkit.tabcompleters.DiscordSRVUtilsTabCompleter;
import tk.bluetree242.discordsrvutils.commands.discord.*;
import tk.bluetree242.discordsrvutils.config.*;
import tk.bluetree242.discordsrvutils.embeds.Embed;
import tk.bluetree242.discordsrvutils.exceptions.ConfigurationLoadException;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;
import tk.bluetree242.discordsrvutils.leveling.LevelingManager;
import tk.bluetree242.discordsrvutils.leveling.listeners.bukkit.BukkitLevelingListener;
import tk.bluetree242.discordsrvutils.leveling.listeners.jda.DiscordLevelingListener;
import tk.bluetree242.discordsrvutils.listeners.afk.EssentialsAFKListener;
import tk.bluetree242.discordsrvutils.listeners.bukkit.JoinUpdateChecker;
import tk.bluetree242.discordsrvutils.listeners.discordsrv.DiscordSRVListener;
import tk.bluetree242.discordsrvutils.listeners.jda.CustomDiscordAccountLinkListener;
import tk.bluetree242.discordsrvutils.listeners.jda.WelcomerAndGoodByeListener;
import tk.bluetree242.discordsrvutils.listeners.punishments.advancedban.AdvancedBanPunishmentListener;
import tk.bluetree242.discordsrvutils.listeners.punishments.libertybans.LibertybansListener;
import tk.bluetree242.discordsrvutils.listeners.punishments.litebans.LitebansPunishmentListener;
import tk.bluetree242.discordsrvutils.messages.MessageManager;
import tk.bluetree242.discordsrvutils.suggestions.Suggestion;
import tk.bluetree242.discordsrvutils.suggestions.SuggestionManager;
import tk.bluetree242.discordsrvutils.suggestions.listeners.SuggestionVoteListener;
import tk.bluetree242.discordsrvutils.tickets.Panel;
import tk.bluetree242.discordsrvutils.tickets.TicketManager;
import tk.bluetree242.discordsrvutils.tickets.listeners.PanelReactListener;
import tk.bluetree242.discordsrvutils.tickets.listeners.TicketCloseListener;
import tk.bluetree242.discordsrvutils.tickets.listeners.TicketDeleteListener;
import tk.bluetree242.discordsrvutils.utils.DebugUtil;
import tk.bluetree242.discordsrvutils.utils.FileWriter;
import tk.bluetree242.discordsrvutils.utils.SuggestionVoteMode;
import tk.bluetree242.discordsrvutils.utils.Utils;
import tk.bluetree242.discordsrvutils.waiter.WaiterManager;
import tk.bluetree242.discordsrvutils.waiters.listeners.CreatePanelListener;
import tk.bluetree242.discordsrvutils.waiters.listeners.EditPanelListener;
import tk.bluetree242.discordsrvutils.waiters.listeners.PaginationListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class DiscordSRVUtils extends JavaPlugin {

    private static DiscordSRVUtils instance;
    public final String fileseparator = System.getProperty("file.separator");
    public final Path messagesDirectory = Paths.get(getDataFolder() + fileseparator + "messages");
    public final Map<String, String> defaultmessages = new HashMap<>();
    public JSONObject levelingRolesRaw;
    public boolean removedDiscordSRVAccountLinkListener = false;
    public SuggestionVoteMode voteMode;
    public String finalError = null;
    public Logger logger = getLogger();
    public List<Plugin> hookedPlugins = new ArrayList<>();
    private ConfManager<Config> configmanager = ConfManager.create(getDataFolder().toPath(), "config.yml", Config.class);
    private Config config;
    private ConfManager<SQLConfig> sqlconfigmanager = ConfManager.create(getDataFolder().toPath(), "sql.yml", SQLConfig.class);
    private SQLConfig sqlconfig;
    private ConfManager<PunishmentsIntegrationConfig> bansIntegrationconfigmanager = ConfManager.create(getDataFolder().toPath(), "PunishmentsIntegration.yml", PunishmentsIntegrationConfig.class);
    private PunishmentsIntegrationConfig bansIntegrationConfig;
    private ConfManager<TicketsConfig> ticketsconfigManager = ConfManager.create(getDataFolder().toPath(), "tickets.yml", TicketsConfig.class);
    private TicketsConfig ticketsConfig;
    private ConfManager<LevelingConfig> levelingconfigManager = ConfManager.create(getDataFolder().toPath(), "leveling.yml", LevelingConfig.class);
    private LevelingConfig levelingConfig;
    private ConfManager<SuggestionsConfig> suggestionsConfigManager = ConfManager.create(getDataFolder().toPath(), "suggestions.yml", SuggestionsConfig.class);
    private SuggestionsConfig suggestionsConfig;
    private ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(3, new ThreadFactory() {
        @Override
        public Thread newThread(@NotNull Runnable r) {

            return newDSUThread(r);
        }
    });
    private DiscordSRVListener dsrvlistener;
    private HikariDataSource sql;
    private List<ListenerAdapter> listeners = new ArrayList<>();

    public static DiscordSRVUtils get() {
        return instance;
    }

    public Thread newDSUThread(Runnable r) {
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
        instance = this;
        dsrvlistener = new DiscordSRVListener();
        new MessageManager();
        new CommandManager();
        new TicketManager();
        new WaiterManager();
        new LevelingManager();
        new SuggestionManager();
        listeners.add(new CommandListener());
        listeners.add(new WelcomerAndGoodByeListener());
        listeners.add(new CreatePanelListener());
        listeners.add(new PaginationListener());
        listeners.add(new TicketDeleteListener());
        listeners.add(new PanelReactListener());
        listeners.add(new TicketCloseListener());
        listeners.add(new EditPanelListener());
        listeners.add(new DiscordLevelingListener());
        listeners.add(new SuggestionVoteListener());
        listeners.add(new CustomDiscordAccountLinkListener());
        initDefaultMessages();

    }

    public void onLoad() {
        init();
        if (getServer().getPluginManager().getPlugin("DiscordSRV") != null) {
            DiscordSRV.api.requireIntent(GatewayIntent.GUILD_MESSAGE_REACTIONS);
            DiscordSRV.api.requireCacheFlag(CacheFlag.EMOTE);
        }
    }

    public void onEnable() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                if (!isEnabled()) return;
                OkHttpClient client = new OkHttpClient();
                MultipartBody form = new MultipartBody.Builder().setType(MediaType.get("multipart/form-data")).addFormDataPart("version", getDescription().getVersion())
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
                        msg = (ChatColor.GREEN + "Plugin is " + versions_behind + " versions behind. Please Update. Download from " + res.getString("downloadUrl"));
                    } else {
                        msg = (ChatColor.GREEN + "Plugin is up to date!");
                    }
                } else {
                    msg = (res.getString("message"));
                }
                switch (logger) {
                    case "INFO":
                        getLogger().info(msg);
                        break;
                    case "WARNING":
                        getLogger().warning(msg);
                        break;
                    case "ERROR":
                        getLogger().warning(msg);
                        break;
                }
            } catch (Exception e) {
                logger.severe("Could not check for updates: " + e.getMessage());
            }

        });
        try {
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
                    "|   &cDevelopers: &rBlueTree242\n&r" +
                    "|   &cVersion: &r" + getDescription().getVersion() + "\n&r" +
                    "|   &cStorage: &r" + storage + "\n&r" +
                    "| &cSupport:\n&r" +
                    "|   &cGithub: &rhttps://github.com/BlueTree242/DiscordSRVUtils/issues\n" +
                    "|   &cDiscord: &rhttps://discordsrvutils.xyz/support\n" +
                    "[]================================[]"));
            System.setProperty("hsqldb.reconfig_logging", "false");
            try {
                Class.forName("github.scarsz.discordsrv.dependencies.jda.api.events.interaction.ButtonClickEvent");
            } catch (ClassNotFoundException e) {
                severe("Plugin could not enable because DiscordSRV is missing an important feature (buttons). This means your DiscordSRV is out of date please update it for DSU to work");
                setEnabled(false);
                return;
            }

            Class.forName("tk.bluetree242.discordsrvutils.dependencies.hsqldb.jdbc.JDBCDriver");
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
            Metrics metrics = new Metrics(this, 9456);
            metrics.addCustomChart(new AdvancedPie("features", () -> {
                Map<String, Integer> valueMap = new HashMap<>();
                /*
                if (!TicketManager.get().getPanels().get().isEmpty())
                valueMap.put("Tickets", 1);

                 */
                if (getLevelingConfig().enabled()) valueMap.put("Leveling", 1);
                if (getSuggestionsConfig().enabled()) valueMap.put("Suggestions", 1);
                if (getMainConfig().welcomer_enabled()) valueMap.put("Welcomer", 1);
                if (getBansConfig().isSendPunishmentmsgesToDiscord() && isAnyPunishmentsPluginInstalled())
                    valueMap.put("Punishment Messages", 1);
                if (getServer().getPluginManager().isPluginEnabled("Essentials") && getMainConfig().afk_message_enabled())
                    valueMap.put("AFK Messages", 1);
                return valueMap;
            }));
            metrics.addCustomChart(new SimplePie("discordsrv_versions", () -> DiscordSRV.getPlugin().getDescription().getVersion()));
            metrics.addCustomChart(new SimplePie("admins", () -> getAdminIds().size() + ""));
        } catch (Throwable ex) {
            setEnabled(false);
            logger.warning("DSU could not start.");
            try {
                logger.severe(DebugUtil.run(Utils.exceptionToStackTrack(ex)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            logger.severe("Send this to support at https://discordsrvutils.xyz/support");
            ex.printStackTrace();
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
            jdbcurl = "jdbc:mysql://" +
                    getSqlconfig().Host() +
                    ":" + getSqlconfig().Port() + "/" + getSqlconfig().DatabaseName() + "?useSSL=false";
            user = sqlconfig.UserName();
            pass = sqlconfig.Password();
        } else {
            logger.info("MySQL is disabled, using hsqldb");
            jdbcurl = "jdbc:hsqldb:file:" + Paths.get(getDataFolder() + fileseparator + "database").resolve("Database") + ";hsqldb.lock_file=false;sql.syntax_mys=true";
            user = "SA";
            pass = "";
        }
        settings.setJdbcUrl(jdbcurl);
        settings.setUsername(user);
        settings.setPassword(pass);
        sql = new HikariDataSource(settings);
        if (!getSqlconfig().isEnabled()) {
            try (Connection conn = sql.getConnection()) {
                //language=hsqldb
                String query = "SET DATABASE SQL SYNTAX MYS TRUE;";
                conn.prepareStatement(query).execute();
            }
        }
        Flyway flyway = Flyway.configure(getClass().getClassLoader())
                .dataSource(sql)
                .baselineOnMigrate(true)
                .locations("classpath:migrations")
                .validateMigrationNaming(true).group(true)
                .table("discordsrvutils_schema")
                .load();
        flyway.repair();
        flyway.migrate();
        logger.info("MySQL/HsqlDB Connected & Setup");
    }

    private void initDefaultMessages() {
        //welcome message
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
        defaultmessages.put("welcome", welcome.toString(1));
        //afk message
        JSONObject afk = new JSONObject();
        JSONObject afkembed = new JSONObject();
        afkembed.put("color", "green");
        afkembed.put("author", new JSONObject().put("name", "[player.name] is now afk").put("icon_url", "https://minotar.net/avatar/[player.name]"));
        afk.put("embed", afkembed);
        defaultmessages.put("afk", afk.toString(1));
        afk = new JSONObject();
        afkembed = new JSONObject();
        afkembed.put("color", "green");
        afkembed.put("author", new JSONObject().put("name", "[player.name] is no longer afk").put("icon_url", "https://minotar.net/avatar/[player.name]"));
        afk.put("embed", afkembed);
        defaultmessages.put("no-longer-afk", afk.toString(1));
        JSONObject punishmentMessage = new JSONObject();
        JSONObject punishmentEmbed = new JSONObject();
        punishmentEmbed.put("color", "red");
        punishmentEmbed.put("author", new JSONObject().put("name", "[punishment.name] was banned by [punishment.operator] For [punishment.reason]").put("icon_url", "https://minotar.net/avatar/[punishment.name]"));
        punishmentEmbed.put("footer", new JSONObject().put("text", "[punishment.duration]"));
        punishmentMessage.put("embed", punishmentEmbed);
        defaultmessages.put("ban", punishmentMessage.toString(1));
        punishmentMessage = new JSONObject();
        punishmentEmbed = new JSONObject();
        punishmentEmbed.put("color", "red");
        punishmentEmbed.put("author", new JSONObject().put("name", "[punishment.name] was unbanned by [punishment.operator]").put("icon_url", "https://minotar.net/avatar/[punishment.name]"));
        punishmentMessage.put("embed", punishmentEmbed);
        defaultmessages.put("unban", punishmentMessage.toString(1));
        punishmentMessage = new JSONObject();
        punishmentEmbed = new JSONObject();
        punishmentEmbed.put("color", "red");
        punishmentEmbed.put("author", new JSONObject().put("name", "[punishment.name] was muted by [punishment.operator] For [punishment.reason]").put("icon_url", "https://minotar.net/avatar/[punishment.name]"));
        punishmentEmbed.put("footer", new JSONObject().put("text", "[punishment.duration]"));
        punishmentMessage.put("embed", punishmentEmbed);
        defaultmessages.put("mute", punishmentMessage.toString(1));
        punishmentMessage = new JSONObject();
        punishmentEmbed = new JSONObject();
        punishmentEmbed.put("color", "red");
        punishmentEmbed.put("author", new JSONObject().put("name", "[punishment.name] was unmuted by [punishment.operator]").put("icon_url", "https://minotar.net/avatar/[punishment.name]"));
        punishmentMessage.put("embed", punishmentEmbed);
        defaultmessages.put("unmute", punishmentMessage.toString(1));
        JSONObject panel = new JSONObject();
        JSONObject panelEmbed = new JSONObject();
        panelEmbed.put("color", "cyan");
        panelEmbed.put("title", "[panel.name]");
        panelEmbed.put("description", "Click on The Button to open a ticket");
        panel.put("embed", panelEmbed);
        defaultmessages.put("panel", panel.toString(1));
        JSONObject ticketOpened = new JSONObject();
        JSONObject ticketOpenedEmbed = new JSONObject();
        ticketOpened.put("content", "[user.asMention] Here is your ticket");
        ticketOpenedEmbed.put("description", String.join("\n", new String[]{
                "Staff will be here shortly",
                "Click `Close Ticket` to close this ticket",
                "**Panel Name: **[panel.name]"
        }));
        ticketOpenedEmbed.put("color", "green");
        ticketOpened.put("embed", ticketOpenedEmbed);
        defaultmessages.put("ticket-open", ticketOpened.toString(1));
        JSONObject ticketClosed = new JSONObject();
        JSONObject ticketClosedEmbed = new JSONObject();
        ticketClosedEmbed.put("description", "Ticket Closed by [user.asMention]");
        ticketClosedEmbed.put("color", "red");
        ticketClosed.put("embed", ticketClosedEmbed);
        defaultmessages.put("ticket-close", ticketClosed.toString(1));
        ticketOpened = new JSONObject();
        ticketOpenedEmbed = new JSONObject();
        ticketOpenedEmbed.put("description", "Ticket reopened by [user.asMention]");
        ticketOpenedEmbed.put("color", "green");
        ticketOpened.put("embed", ticketOpenedEmbed);
        defaultmessages.put("ticket-reopen", ticketOpened.toString(1));
        JSONObject level = new JSONObject();
        JSONObject levelEmbed = new JSONObject();
        levelEmbed.put("color", "cyan");
        levelEmbed.put("title", "Level for [stats.name]");
        levelEmbed.put("description", String.join("\n", new String[]{
                        "**Level:** [stats.level]",
                        "**XP:** [stats.xp]",
                        "**Rank:**: #[stats.rank]"
                }
        ));
        levelEmbed.put("thumbnail", new JSONObject().put("url", "https://minotar.net/avatar/[stats.name]"));
        level.put("embed", levelEmbed);
        defaultmessages.put("level", level.toString(1));
        JSONObject suggestion = new JSONObject();
        JSONObject suggestionEmbed = new JSONObject();
        suggestionEmbed.put("color", "orange");
        JSONArray suggestionFields = new JSONArray();
        suggestionFields.put(new JSONObject().put("name", "Submitter").put("value", "[submitter.asMention]"));
        suggestionFields.put(new JSONObject().put("name", "Suggestion").put("value", "[suggestion.text]"));
        suggestionEmbed.put("fields", suggestionFields);
        suggestionEmbed.put("thumbnail", new JSONObject().put("url", "[submitter.effectiveAvatarUrl]"));
        suggestionEmbed.put("title", "Suggestion Number: [suggestion.number]");
        suggestion.put("embed", suggestionEmbed);
        defaultmessages.put("suggestion", suggestion.toString(1));
        suggestionEmbed = new JSONObject();
        suggestion = new JSONObject();
        suggestionFields = new JSONArray();
        suggestionEmbed.put("color", "orange");
        suggestionFields.put(new JSONObject().put("name", "Submitter").put("value", "[submitter.asMention]"));
        suggestionFields.put(new JSONObject().put("name", "Suggestion").put("value", "[suggestion.text]"));
        suggestionFields.put(new JSONObject().put("name", "Staff Note").put("value", "[note.text]"));
        suggestionFields.put(new JSONObject().put("name", "Staff Member").put("value", "[staff.asMention]"));
        suggestionEmbed.put("fields", suggestionFields);
        suggestionEmbed.put("thumbnail", new JSONObject().put("url", "[submitter.effectiveAvatarUrl]"));
        suggestionEmbed.put("title", "Suggestion Number: [suggestion.number]");
        suggestion.put("embed", suggestionEmbed);
        defaultmessages.put("suggestion-noted", suggestion.toString(1));
        suggestionEmbed = new JSONObject();
        suggestion = new JSONObject();
        suggestionFields = new JSONArray();
        suggestionEmbed.put("color", "green");
        suggestionFields.put(new JSONObject().put("name", "Vote Results").put("value", ":white_check_mark: [suggestion.yesCount]\n:x: [suggestion.noCount]"));
        suggestionFields.put(new JSONObject().put("name", "Submitter").put("value", "[submitter.asMention]"));
        suggestionFields.put(new JSONObject().put("name", "Suggestion").put("value", "[suggestion.text]"));
        suggestionFields.put(new JSONObject().put("name", "Staff Note").put("value", "[note.text]"));
        suggestionFields.put(new JSONObject().put("name", "Staff Member").put("value", "[staff.asMention]"));
        suggestionFields.put(new JSONObject().put("name", "Approved By").put("value", "[approver.asMention]"));
        suggestionEmbed.put("fields", suggestionFields);
        suggestionEmbed.put("thumbnail", new JSONObject().put("url", "[submitter.effectiveAvatarUrl]"));
        suggestionEmbed.put("title", "Suggestion Number: [suggestion.number]");
        suggestion.put("embed", suggestionEmbed);
        defaultmessages.put("suggestion-noted-approved", suggestion.toString(1));
        suggestionEmbed = new JSONObject();
        suggestion = new JSONObject();
        suggestionFields = new JSONArray();
        suggestionEmbed.put("color", "green");
        suggestionFields.put(new JSONObject().put("name", "Vote Results").put("value", ":white_check_mark: [suggestion.yesCount]\n:x: [suggestion.noCount]"));
        suggestionFields.put(new JSONObject().put("name", "Submitter").put("value", "[submitter.asMention]"));
        suggestionFields.put(new JSONObject().put("name", "Suggestion").put("value", "[suggestion.text]"));
        suggestionFields.put(new JSONObject().put("name", "Approved By").put("value", "[approver.asMention]"));
        suggestionEmbed.put("fields", suggestionFields);
        suggestionEmbed.put("thumbnail", new JSONObject().put("url", "[submitter.effectiveAvatarUrl]"));
        suggestionEmbed.put("title", "Suggestion Number: [suggestion.number]");
        suggestion.put("embed", suggestionEmbed);
        defaultmessages.put("suggestion-approved", suggestion.toString(1));
        suggestionEmbed = new JSONObject();
        suggestion = new JSONObject();
        suggestionFields = new JSONArray();
        suggestionEmbed.put("color", "red");
        suggestionFields.put(new JSONObject().put("name", "Vote Results").put("value", ":white_check_mark: [suggestion.yesCount]\n:x: [suggestion.noCount]"));
        suggestionFields.put(new JSONObject().put("name", "Submitter").put("value", "[submitter.asMention]"));
        suggestionFields.put(new JSONObject().put("name", "Suggestion").put("value", "[suggestion.text]"));
        suggestionFields.put(new JSONObject().put("name", "Denied By").put("value", "[approver.asMention]"));
        suggestionEmbed.put("fields", suggestionFields);
        suggestionEmbed.put("thumbnail", new JSONObject().put("url", "[submitter.effectiveAvatarUrl]"));
        suggestionEmbed.put("title", "Suggestion Number: [suggestion.number]");
        suggestion.put("embed", suggestionEmbed);
        defaultmessages.put("suggestion-denied", suggestion.toString(1));
        suggestionEmbed = new JSONObject();
        suggestion = new JSONObject();
        suggestionFields = new JSONArray();
        suggestionEmbed.put("color", "red");
        suggestionFields.put(new JSONObject().put("name", "Vote Results").put("value", ":white_check_mark: [suggestion.yesCount]\n:x: [suggestion.noCount]"));
        suggestionFields.put(new JSONObject().put("name", "Submitter").put("value", "[submitter.asMention]"));
        suggestionFields.put(new JSONObject().put("name", "Suggestion").put("value", "[suggestion.text]"));
        suggestionFields.put(new JSONObject().put("name", "Staff Note").put("value", "[note.text]"));
        suggestionFields.put(new JSONObject().put("name", "Staff Member").put("value", "[staff.asMention]"));
        suggestionFields.put(new JSONObject().put("name", "Denied By").put("value", "[approver.asMention]"));
        suggestionEmbed.put("fields", suggestionFields);
        suggestionEmbed.put("thumbnail", new JSONObject().put("url", "[submitter.effectiveAvatarUrl]"));
        suggestionEmbed.put("title", "Suggestion Number: [suggestion.number]");
        suggestion.put("embed", suggestionEmbed);
        defaultmessages.put("suggestion-noted-denied", suggestion.toString(1));
    }


    public void onDisable() {
        if (dsrvlistener != null) DiscordSRV.api.unsubscribe(dsrvlistener);
        if (isReady()) {
            getJDA().removeEventListener(listeners.toArray(new Object[0]));
        }
        pool.shutdown();
        if (WaiterManager.get() != null) WaiterManager.get().timer.cancel();
        if (sql != null) sql.close();
    }

    private void whenStarted() {
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
            File levelingRoles = new File(getDataFolder() + fileseparator + "leveling-roles.json");
            if (!levelingRoles.exists()) {
                levelingRoles.createNewFile();
                FileWriter writer = new FileWriter(levelingRoles);
                writer.write("{}");
                writer.close();
                levelingRolesRaw = new JSONObject();
            } else {
                levelingRolesRaw = new JSONObject(Utils.readFile(levelingRoles));
            }
        } catch (FileNotFoundException e) {
            logger.severe("Error creating leveling-roles.json");
        } catch (IOException e) {
            logger.severe("Error creating leveling-roles.json");
        }

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PAPIExpansion().register();
        }


    }


    public void registerListeners() {
        getJDA().addEventListener(listeners.toArray(new Object[0]));
        Bukkit.getServer().getPluginManager().registerEvents(new BukkitLevelingListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JoinUpdateChecker(), this);
    }

    public void registerCommands() {
        CommandManager.get().registerCommand(new TestMessageCommand());
        CommandManager.get().registerCommand(new HelpCommand());
        CommandManager.get().registerCommand(new CreatePanelCommand());
        CommandManager.get().registerCommand(new PanelListCommand());
        CommandManager.get().registerCommand(new DeletePanelCommand());
        CommandManager.get().registerCommand(new EditPanelCommand());
        CommandManager.get().registerCommand(new CloseCommand());
        CommandManager.get().registerCommand(new ReopenCommand());
        CommandManager.get().registerCommand(new LevelCommand());
        CommandManager.get().registerCommand(new LeaderboardCommand());
        CommandManager.get().registerCommand(new SuggestCommand());
        CommandManager.get().registerCommand(new SuggestionNoteCommand());
        CommandManager.get().registerCommand(new ApproveSuggestionCommand());
        CommandManager.get().registerCommand(new DenySuggestionCommand());
    }


    public boolean isReady() {
        return DiscordSRV.isReady;
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
        File levelingRoles = new File(getDataFolder() + fileseparator + "leveling-roles.json");
        if (!levelingRoles.exists()) {
            levelingRoles.createNewFile();
            FileWriter writer = new FileWriter(levelingRoles);
            writer.write("{}");
            writer.close();
            levelingRolesRaw = new JSONObject();
        } else {
            levelingRolesRaw = new JSONObject(Utils.readFile(levelingRoles));
        }
        setSettings();
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

    public void executeAsync(Runnable r) {
        pool.execute(r);
    }

    public void whenReady() {
        executeAsync(() -> {
            registerCommands();
            setSettings();
            registerListeners();
            if (getServer().getPluginManager().isPluginEnabled("Essentials")) {
                getServer().getPluginManager().registerEvents(new EssentialsAFKListener(), this);
                hookedPlugins.add(getServer().getPluginManager().getPlugin("Essentials"));
            }
            if (getServer().getPluginManager().isPluginEnabled("AdvancedBan")) {
                getServer().getPluginManager().registerEvents(new AdvancedBanPunishmentListener(), this);
                hookedPlugins.add(getServer().getPluginManager().getPlugin("AdvancedBan"));
            }
            if (getServer().getPluginManager().isPluginEnabled("Litebans")) {
                new LitebansPunishmentListener();
                hookedPlugins.add(getServer().getPluginManager().getPlugin("Litebans"));
            }
            if (getServer().getPluginManager().isPluginEnabled("LibertyBans")) {
                new LibertybansListener();
                hookedPlugins.add(getServer().getPluginManager().getPlugin("LibertyBans"));
            }
            if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                hookedPlugins.add(getServer().getPluginManager().getPlugin("PlaceholderAPI"));
            }
            if (getMainConfig().remove_discordsrv_link_listener()) {
                for (Object listener : getJDA().getEventManager().getRegisteredListeners()) {
                    if (listener.getClass().getName().equals("github.scarsz.discordsrv.listeners.DiscordAccountLinkListener")) {
                        getJDA().removeEventListener(listener);
                        removedDiscordSRVAccountLinkListener = true;
                    }
                }
            }
            fixTickets();
            voteMode = SuggestionVoteMode.valueOf(suggestionsConfig.suggestions_vote_mode().toUpperCase()) == null ? SuggestionVoteMode.REACTIONS : SuggestionVoteMode.valueOf(suggestionsConfig.suggestions_vote_mode().toUpperCase());
            doSuggestions();
            logger.info("Plugin is ready to function.");
        });

    }

    private void fixTickets() {
        try (Connection conn = getDatabase()) {
            PreparedStatement p1 = conn.prepareStatement("SELECT * FROM tickets");
            ResultSet r1 = p1.executeQuery();
            while (r1.next()) {
                TextChannel channel = getGuild().getTextChannelById(r1.getLong("Channel"));
                if (channel == null) {
                    PreparedStatement p = conn.prepareStatement("DELETE FROM tickets WHERE Channel=?");
                    p.setLong(1, r1.getLong("Channel"));
                    p.execute();
                }
            }
            p1 = conn.prepareStatement("SELECT * FROM ticket_panels");
            r1 = p1.executeQuery();
            while (r1.next()) {
                Panel panel = TicketManager.get().getPanel(r1);
                Message msg = getGuild().getTextChannelById(panel.getChannelId()).retrieveMessageById(panel.getMessageId()).complete();
                if (msg.getButtons().isEmpty()) {
                    msg.clearReactions().queue();
                    msg.editMessage(msg).setActionRow(Button.secondary("open_ticket", Emoji.fromUnicode("\uD83C\uDFAB")).withLabel("Open Ticket")).queue();
                }
            }
        } catch (SQLException e) {
            throw new UnCheckedSQLException(e);
        }
    }

    private void doSuggestions() {
        String warnmsg = "Suggestions are being migrated to the new Suggestions Mode. Users may not vote for suggestions during this time";
        boolean sent = false;
        try (Connection conn = getDatabase()) {
            PreparedStatement p1 = conn.prepareStatement("SELECT * FROM suggestions");
            ResultSet r1 = p1.executeQuery();
            while (r1.next()) {
                Suggestion suggestion = SuggestionManager.get().getSuggestion(r1);
                Message msg = suggestion.getMessage();
                if (msg != null) {
                    if (msg.getButtons().isEmpty()) {
                        if (voteMode == SuggestionVoteMode.REACTIONS) {
                        } else {
                            if (!sent) {
                                logger.info(warnmsg);
                                sent = true;
                                SuggestionManager.get().loading = true;
                            }
                            msg.clearReactions().queue();
                            msg.editMessage(suggestion.getCurrentMsg()).setActionRow(
                                    Button.success("yes", SuggestionManager.getYesEmoji().toJDAEmoji()),
                                    Button.danger("no", SuggestionManager.getNoEmoji().toJDAEmoji()),
                                    Button.secondary("reset", Emoji.fromUnicode("⬜"))).queue();
                        }
                    } else {
                        if (voteMode == SuggestionVoteMode.REACTIONS) {
                            if (!sent) {
                                SuggestionManager.get().loading = true;
                                logger.info(warnmsg);
                                sent = true;
                            }
                            msg.addReaction(SuggestionManager.getYesEmoji().getNameInReaction()).queue();
                            msg.addReaction(SuggestionManager.getNoEmoji().getNameInReaction()).queue();
                            msg.editMessage(msg).setActionRows(Collections.EMPTY_LIST).queue();
                        }
                    }
                }
            }
            if (sent) {
                logger.info("Suggestions Migration has finished.");
            }
            SuggestionManager.get().loading = false;
        } catch (SQLException e) {
            throw new UnCheckedSQLException(e);
        }
    }


    public void setSettings() {
        if (!isReady()) return;
        OnlineStatus onlineStatus = getMainConfig().onlinestatus().equalsIgnoreCase("DND") ? OnlineStatus.DO_NOT_DISTURB : OnlineStatus.valueOf(getMainConfig().onlinestatus().toUpperCase());
        getJDA().getPresence().setStatus(onlineStatus);
        LevelingManager.get().cachedUUIDS.refreshAll(LevelingManager.get().cachedUUIDS.asMap().keySet());
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

    public Connection getDatabase() throws SQLException {
        return sql.getConnection();
    }

    public TextChannel getChannel(long id, TextChannel channel) {
        if (id == -1) {
            if (channel != null) return channel;
            return null;
        }
        if (id == 0) {
            return DiscordSRV.getPlugin().getMainTextChannel();
        } else return getJDA().getTextChannelById(id);
    }

    public TextChannel getChannel(long id) {
        return getChannel(id, null);
    }

    public Guild getGuild() {
        return DiscordSRV.getPlugin().getMainGuild();
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
        channel.sendMessage(Embed.error("An error happened. Check Console for details")).queue();
        logger.severe("The following error have a high chance to be caused by DiscordSRVUtils. Report at https://discordsrvutils.xyz/support and not discordsrv's Discord.");
        ex.printStackTrace();
    }

    public void defaultHandle(Throwable ex) {
        if (!config.minimize_errors()) {
            logger.severe("The following error have a high chance to be caused by DiscordSRVUtils. Report at https://discordsrvutils.xyz/support and not discordsrv's Discord.");
            ex.printStackTrace();
        } else {
            logger.severe("DiscordSRVUtils had an error. Error minimization enabled.");
        }
        finalError = Utils.exceptionToStackTrack(ex);
    }

    private boolean isAnyPunishmentsPluginInstalled() {
        if (getServer().getPluginManager().isPluginEnabled("AdvancedBan")) return true;
        if (getServer().getPluginManager().isPluginEnabled("Litebans")) return true;
        if (getServer().getPluginManager().isPluginEnabled("Libertybans")) return true;
        return false;
    }


    // Allow the user to set variables inside SpEL Strings and expression turns just nothing
    public String execute(Object o) {
        return "";
    }

}