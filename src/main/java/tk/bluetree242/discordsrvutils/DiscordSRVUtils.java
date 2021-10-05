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
import okhttp3.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
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
import tk.bluetree242.discordsrvutils.exceptions.StartupException;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;
import tk.bluetree242.discordsrvutils.leveling.LevelingManager;
import tk.bluetree242.discordsrvutils.leveling.listeners.bukkit.BukkitLevelingListener;
import tk.bluetree242.discordsrvutils.leveling.listeners.jda.DiscordLevelingListener;
import tk.bluetree242.discordsrvutils.listeners.afk.EssentialsAFKListener;
import tk.bluetree242.discordsrvutils.listeners.discordsrv.DiscordSRVListener;
import tk.bluetree242.discordsrvutils.listeners.jda.WelcomerAndGoodByeListener;
import tk.bluetree242.discordsrvutils.listeners.punishments.advancedban.AdvancedBanPunishmentListener;
import tk.bluetree242.discordsrvutils.messages.MessageManager;
import tk.bluetree242.discordsrvutils.tickets.TicketManager;
import tk.bluetree242.discordsrvutils.tickets.listeners.PanelReactListener;
import tk.bluetree242.discordsrvutils.tickets.listeners.TicketCloseListener;
import tk.bluetree242.discordsrvutils.tickets.listeners.TicketDeleteListener;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

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
    private ConfManager<PunishmentsIntegrationConfig> bansIntegrationconfigmanager = ConfManager.create(getDataFolder().toPath(), "PunishmentsIntegration.yml", PunishmentsIntegrationConfig.class);
    private PunishmentsIntegrationConfig bansIntegrationConfig;
    private ConfManager<TicketsConfig> ticketsconfigManager = ConfManager.create(getDataFolder().toPath(), "tickets.yml", TicketsConfig.class);
    private TicketsConfig ticketsConfig;
    private ConfManager<LevelingConfig> levelingconfigManager = ConfManager.create(getDataFolder().toPath(), "leveling.yml", LevelingConfig.class);
    private LevelingConfig levelingConfig;
    public final Map<String, String> defaultmessages = new HashMap<>();
    private ExecutorService pool = Executors.newFixedThreadPool(3, new ThreadFactory() {
        @Override
        public Thread newThread(@NotNull Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("DSU-THREAD");
            thread.setDaemon(true);
            return thread;
        }
    });
    private DiscordSRVListener dsrvlistener;
    public Logger logger = getLogger();
    private HikariDataSource sql;
    private List<ListenerAdapter> listeners = new ArrayList<>();
    public List<Plugin> hookedPlugins = new ArrayList<>();
    private void init() {
        instance = this;
        dsrvlistener = new DiscordSRVListener();
        new MessageManager();
        new CommandManager();
        new TicketManager();
        new WaiterManager();
        new LevelingManager();
        listeners.add(new CommandListener());
        listeners.add(new WelcomerAndGoodByeListener());
        listeners.add(new CreatePanelListener());
        listeners.add(new PaginationListener());
        listeners.add(new TicketDeleteListener());
        listeners.add(new PanelReactListener());
        listeners.add(new TicketCloseListener());
        listeners.add(new EditPanelListener());
        listeners.add(new DiscordLevelingListener());
        initDefaultMessages();

    }

    public void onLoad() {
        init();
        if (getServer().getPluginManager().getPlugin("DiscordSRV") != null) {
            DiscordSRV.api.requireIntent(GatewayIntent.GUILD_MESSAGE_REACTIONS);
        }
    }

    public void onEnable() {
        Bukkit.getScheduler().runTaskAsynchronously(this, ()-> {
            try {
                OkHttpClient client = new OkHttpClient();
                MultipartBody form = new MultipartBody.Builder().setType(MediaType.get("multipart/form-data")).addFormDataPart("version", getDescription().getVersion())
                        .build();

                Request req = new Request.Builder().url("https://discordsrvutils.ml/updatecheck").post(form).build();
                Response response = client.newCall(req).execute();
                JSONObject res = new JSONObject(response.body().string());
                response.close();
                int versions_behind = res.getInt("versions_behind");
                if (res.isNull("message")) {
                    if (versions_behind != 0) {
                        logger.info(ChatColor.GREEN + "Plugin is " + versions_behind + " versions behind. Please Update. Download from " + res.getString("downloadUrl"));
                    } else {
                        logger.info(ChatColor.GREEN + "Plugin is up to date!");
                    }
                } else {
                    logger.info(res.getString("message"));
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
                    "|   &cDiscord: &rhttps://discordsrvutils.ml/support\n" +
                    "[]================================[]"));
            System.setProperty("hsqldb.reconfig_logging", "false");
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
            jdbcurl = "jdbc:hsqldb:file:" + Paths.get(getDataFolder() + fileseparator + "database").resolve("Database") + ";hsqldb.lock_file=false;syntax_mys=true";
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
        panelEmbed.put("description", "React with \uD83C\uDFAB to open a ticket");
        panel.put("embed", panelEmbed);
        defaultmessages.put("panel", panel.toString(1));
        JSONObject ticketOpened = new JSONObject();
        JSONObject ticketOpenedEmbed = new JSONObject();
        ticketOpened.put("content", "[user.asMention] Here is your ticket");
        ticketOpenedEmbed.put("description", String.join("\n", new String[]{
                "Staff will be here shortly",
                "React with \uD83D\uDD12 to close this ticket",
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
    }



    public void onDisable() {
        instance = null;
        if (dsrvlistener != null) DiscordSRV.api.unsubscribe(dsrvlistener);
        if (isReady()) {
            getJDA().removeEventListener(listeners.toArray(new Object[0]));
        }
        pool.shutdown();
        if (WaiterManager.get() != null) WaiterManager.get().timer.cancel();
        if (sql != null)sql.close();
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
        Bukkit.getServer().getPluginManager().registerEvents(new BukkitLevelingListener(), this);
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
    }





    public boolean isReady() {
        return DiscordSRV.isReady;
    }

    public void reloadConfigs() throws IOException, InvalidConfigException {
        configmanager.reloadConfig();
        config =configmanager.reloadConfigData();
        sqlconfigmanager.reloadConfig();
        sqlconfig = sqlconfigmanager.reloadConfigData();
        bansIntegrationconfigmanager.reloadConfig();
        bansIntegrationConfig = bansIntegrationconfigmanager.reloadConfigData();
        ticketsconfigManager.reloadConfig();
        ticketsConfig = ticketsconfigManager.reloadConfigData();
        levelingconfigManager.reloadConfig();
        levelingConfig = levelingconfigManager.reloadConfigData();
        setSettings();
    }

    public Config getMainConfig() {
        return config;
    }

    public SQLConfig getSqlconfig() {
        return sqlconfig;
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
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            hookedPlugins.add(getServer().getPluginManager().getPlugin("PlaceholderAPI"));
        }
        if (getMainConfig().remove_discordsrv_link_listener()) {
            for (Object listener : getJDA().getEventManager().getRegisteredListeners()) {
                if (listener.getClass().getName().equals("github.scarsz.discordsrv.listeners.DiscordAccountLinkListener")) {
                    getJDA().removeEventListener(listener);
                }
            }
        }
        fixTickets();
        logger.info("Plugin is ready to function.");
    }
    public void fixTickets() {
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
        } catch (SQLException e) {
            throw new UnCheckedSQLException(e);
        }
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
        return CompletableFuture.supplyAsync(v);
    }

    public CompletableFuture<Void> completableFutureRun(Runnable r) {
        return CompletableFuture.runAsync(r);
    }

    public RestAction<Message> queueMsg(Message msg, MessageChannel channel) {
        return channel.sendMessage(msg);
    }

    public <U> void handleCF(CompletableFuture<U> cf, Consumer<U> success, Consumer<Throwable> failure) {
        if (success!=null)cf.thenAcceptAsync(success);
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
        ex.printStackTrace();
    }
    public void defaultHandle(Throwable ex) {
        ex.printStackTrace();
        //Do nothing lol
    }


    // Allow the user to set variables inside SpEL Strings and expression turns just nothing
    public String execute(Object o) {
        return "";
    }

}