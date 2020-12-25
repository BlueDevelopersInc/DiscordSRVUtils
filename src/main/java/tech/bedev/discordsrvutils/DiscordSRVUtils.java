package tech.bedev.discordsrvutils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.OnlineStatus;
import github.scarsz.discordsrv.dependencies.jda.api.requests.GatewayIntent;
import github.scarsz.discordsrv.objects.Lag;
import net.md_5.bungee.api.ChatColor;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.dazzleconf.error.InvalidConfigException;
import sun.jvm.hotspot.debugger.cdbg.LineNumberVisitor;
import tech.bedev.discordsrvutils.Configs.*;
import tech.bedev.discordsrvutils.Exceptions.StartupException;
import tech.bedev.discordsrvutils.Managers.TimerManager;
import tech.bedev.discordsrvutils.Person.Person;
import tech.bedev.discordsrvutils.Person.PersonImpl;
import tech.bedev.discordsrvutils.commands.*;
import tech.bedev.discordsrvutils.commands.tabCompleters.DiscordSRVUtilsTabCompleter;
import tech.bedev.discordsrvutils.events.*;


import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DiscordSRVUtils extends JavaPlugin {

    public static boolean isReady = false;
    public static boolean PAPI;
    Path databaseFile;
    String jdbcUrl;
    public DiscordSRVEventListener discordListener;
    public JDAEvents JDALISTENER;
    public HikariDataSource sql;
    public String username;
    public String password;
    public int port;
    public String host;
    public boolean SQLEnabled;


    public static JDA getJda() {
        return DiscordSRV.getPlugin().getJda();
    }
    public static Timer timer = new Timer();
    public static SQLConfig SQLconfig;
    public ConfManager<SQLConfig> SQLConfigManager = ConfManager.create(getDataFolder().toPath(),"SQL.yml", SQLConfig.class);
    public static LevelingConfig Levelingconfig;
    public ConfManager<LevelingConfig> LevelingConfigManager = ConfManager.create(getDataFolder().toPath(),"Leveling.yml", LevelingConfig.class);
    public static BotSettingsConfig BotSettingsconfig;
    public ConfManager<BotSettingsConfig> BotSettingsConfigManager = ConfManager.create(getDataFolder().toPath(),"BotSettings.yml", BotSettingsConfig.class);
    public static ModerationConfig Moderationconfig;
    public ConfManager<ModerationConfig> ModerationConfigManager = ConfManager.create(getDataFolder().toPath(),"Moderation.yml", ModerationConfig.class);
    public static BansIntegrationConfig BansIntegrationconfig;
    public ConfManager<BansIntegrationConfig> BansIntegrationConfigManager = ConfManager.create(getDataFolder().toPath(),"BansIntegration.yml", BansIntegrationConfig.class);
    public static MainConfConfig Config;
    public ConfManager<MainConfConfig> MainConfManager = ConfManager.create(getDataFolder().toPath(),"config.yml", MainConfConfig.class);
    public static Timer timer2 = new Timer();
    public Map<Long, Long> tempmute = new HashMap<>();
    public static DiscordSRVUtils getMainClass() {
        return new DiscordSRVUtils();
    }
    public Long parseStringToMillies(String s) {
        String slc = s.toLowerCase();
        if (slc.endsWith("s")) {
            String v = slc.replace("s", "");
            try {
                Integer.parseInt(v);
                String v2 = v + "000";
                return Long.parseLong(v2);
            } catch (NumberFormatException ex) {
                return Long.parseLong("-1");
            }
        } else if (slc.endsWith("m")) {
            String v = slc.replace("m", "");
            try {
                Integer.parseInt(v);
                return Integer.parseInt(v) * 60000L;
            } catch (NumberFormatException ex) {
                return Long.parseLong("-1");
            }

        } else if (slc.endsWith("h")) {
            String v = slc.replace("h", "");
            try {
                Integer.parseInt(v);
                return Integer.parseInt(v) * 3600000L;
            } catch (NumberFormatException ex) {
                return Long.parseLong("-1");
            }

        } else if (slc.endsWith("d")) {
            String v = slc.replace("d", "");
            try {
                Integer.parseInt(v);
                return Integer.parseInt(v) * 86400000L;
            } catch (NumberFormatException ex) {
                return Long.parseLong("-1");
            }

        }
        return Long.parseLong("-1");
    }


    @Override
    public void onEnable() {
        TimerManager time = new TimerManager();
        String duration = time.getTimeFormatter().getDuration(parseStringToMillies("1d"));
        System.out.println(duration);


        try {
            SQLConfigManager.reloadConfig();
            LevelingConfigManager.reloadConfig();
            Levelingconfig = LevelingConfigManager.reloadConfigData();
            SQLconfig = SQLConfigManager.reloadConfigData();
            BotSettingsConfigManager.reloadConfig();
            BotSettingsconfig = BotSettingsConfigManager.reloadConfigData();
            ModerationConfigManager.reloadConfig();
            Moderationconfig = ModerationConfigManager.reloadConfigData();
            BansIntegrationConfigManager.reloadConfig();
            BansIntegrationconfig = BansIntegrationConfigManager.reloadConfigData();
            MainConfManager.reloadConfig();
            Config = MainConfManager.reloadConfigData();
            if (SQLconfig.isEnabled()) {
                HikariConfig hikariConf = new HikariConfig();
                hikariConf.setJdbcUrl("jdbc:" + "mysql" + "://" +
                        SQLconfig.Host() +
                        ":" + SQLconfig.Port() + "/" + SQLconfig.DatabaseName());
                hikariConf.setUsername(SQLconfig.UserName());
                hikariConf.setPassword(SQLconfig.Password());
                hikariConf.setMaximumPoolSize(20);
                sql = new HikariDataSource(hikariConf);
                port = SQLconfig.Port();
                username = SQLconfig.UserName();
                host = SQLconfig.Host();
                SQLEnabled = true;
            } else {
                port = 3306;
                username = null;
                host = null;
                SQLEnabled = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigException e) {
            e.printStackTrace();
        }
        try {

            if (!this.getDescription().getName().equals("DiscordSRVUtils")) {
                setEnabled(false);
                System.out.println("[DiscordSRVUtils] Detected plugin name change.");
                return;
            }
            String storage = "Unknown";
            if (SQLconfig.isEnabled()) {
                storage = "MySQL";
            } else {
                storage = "HSQLDB (local)";
            }
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "" +
                    "\n[]=====[&2Enabling DiscordSRVUtils&r]=====[]\n" +
                    "| &cInformation:\n&r" +
                    "|   &cName: &rDiscordSRVUtils\n&r" +
                    "|   &cDeveloper: &rBlue Tree\n&r" +
                    "|   &cVersion: &r" + getDescription().getVersion() + "\n&r" +
                    "|   &cStorage: &r" + storage + "\n&r" +
                    "| &cSupport:\n&r" +
                    "|   &cGithub: &rhttps://github.com/BlueTree242/DiscordSRVUtils/issues\n" +
                    "|   &cDiscord: &rhttps://discord.gg/MMMQHA4\n" +
                    "[]================================[]"));
            System.setProperty("hsqldb.reconfig_logging", "false");
            try {
                Class.forName("org.hsqldb.jdbc.JDBCDriver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();

            }
            if (!getServer().getPluginManager().isPluginEnabled("DiscordSRV")) {
                getLogger().warning("DiscordSRVUtils could not be enabled. DiscordSRV is not installed or is not enabled.");
                getLogger().warning("We will add support for no discordsrv in the future.");
                setEnabled(false);
                return;
            }
            databaseFile = getDataFolder().toPath().resolve("Database");
            String jdbcUrl = "jdbc:hsqldb:file:" + databaseFile.toAbsolutePath();
            try (Connection conn = getDatabaseFile()) {
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS discordsrvutils_ticket_allowed_roles (TicketID int, RoleID Bigint)").execute();
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS discordsrvutils_tickets (" +
                        "TicketID int, Name Varchar(500), " +
                        "MessageId Bigint, " +
                        "Opened_Category Bigint, " +
                        "Closed_Category Bigint, ChannelID Bigint)").execute();
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS discordsrvutils_Opened_Tickets (UserID Bigint, MessageID Bigint, TicketID Bigint, Channel_id Bigint)").execute();
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS discordsrvutils_Closed_Tickets (UserID Bigint, MessageID Bigint, TicketID Bigint, Channel_id Bigint, Closed_Message Bigint)").execute();
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS discordsrvutils_leveling (userID Bigint, unique_id varchar(36), level int, XP int)").execute();
            } catch (SQLException exception) {
                exception.printStackTrace();

            }
            try (Connection conn = getMemoryConnection()) {

                conn.prepareStatement("CREATE TABLE status (Status int)").execute();
                conn.prepareStatement("CREATE TABLE tickets_creating (UserID Bigint, Channel_id Bigint, step int, Name Varchar(500), MessageId Bigint, Opened_Category Bigint, Closed_Category Bigint, TicketID int); ").execute();
                conn.prepareStatement("CREATE TABLE discordsrvutils_ticket_allowed_roles (UserID Bigint, Channel_id Bigint, RoleID Bigint)").execute();
                conn.prepareStatement("CREATE TABLE discordsrvutils_Awaiting_Edits (Channel_id Bigint, UserID Bigint, Type int, MessageID Bigint, TicketID int)").execute();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            if (getServer().getPluginManager().isPluginEnabled("Essentials")) {
                getServer().getPluginManager().registerEvents(new EssentialsAfk(this), this);
            }
            if (getServer().getPluginManager().isPluginEnabled("AdvancedBan")) {
                getServer().getPluginManager().registerEvents(new AdvancedBanListener(this), this);
            }

            Objects.requireNonNull(getCommand("discordsrvutils")).setExecutor(new DiscordSRVUtilsCommand(this));
            Objects.requireNonNull(getCommand("discordsrvutils")).setTabCompleter(new DiscordSRVUtilsTabCompleter());
            this.discordListener = new DiscordSRVEventListener(this);
            this.JDALISTENER = new JDAEvents(this);
            Bukkit.getPluginManager().registerEvents(new BukkitEventListener(this), this);

            DiscordSRV.api.subscribe(discordListener);
            getCommand("setlevel").setExecutor(new setlevelCommand(this));
            getCommand("addlevels").setExecutor(new addlevelsCommand(this));
            getCommand("removelevels").setExecutor(new removelevelsCommand(this));
            getCommand("setxp").setExecutor(new setxpCommand(this));
            getCommand("addxp").setExecutor(new addxpCommand(this));
            getCommand("removexp").setExecutor(new removeXPCommand(this));

            if (DiscordSRV.isReady) {
                getJda().addEventListener(JDALISTENER);
                String status = BotSettingsconfig.status();
                if (status != null) {
                    switch (status.toUpperCase()) {
                        case "DND":
                            getJda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                            break;
                        case "IDLE":
                            getJda().getPresence().setStatus(OnlineStatus.IDLE);
                            break;
                        case "ONLINE":
                            getJda().getPresence().setStatus(OnlineStatus.ONLINE);
                            break;
                    }
                }
            }
            String newVersion = UpdateChecker.getLatestVersion();
            if (newVersion.equalsIgnoreCase(getDescription().getVersion())) {
                getLogger().info(net.md_5.bungee.api.ChatColor.GREEN + "No new version available. (" + newVersion + ")");
            } else {
                getLogger().info(net.md_5.bungee.api.ChatColor.GREEN + "A new version is available. Please update ASAP!" + " Your version: " + net.md_5.bungee.api.ChatColor.YELLOW + getDescription().getVersion() + net.md_5.bungee.api.ChatColor.GREEN + " New version: " + net.md_5.bungee.api.ChatColor.YELLOW + newVersion);
            }

            int pluginId = 9456; // <-- Replace with the id of your plugin!
            Metrics metrics = new Metrics(this, pluginId);
            PAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                Bukkit.getScheduler().runTask(this, () -> {
                    new PlaceholderAPI().register();

                });
            }
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Lag(), 100L, 1L);
        } catch (Exception ex) {
            try {
                throw new StartupException();
            } catch (StartupException e) {
                e.printStackTrace();
            }
        }
        timer2.schedule(new TimeHandler(this), 0, 1000);

    }
    @Override
    public void onLoad() {
        if (!this.getDescription().getName().equals("DiscordSRVUtils")) {
            setEnabled(false);
        }
        if (getServer().getPluginManager().getPlugin("DiscordSRV") != null) {
            DiscordSRV.api.requireIntent(GatewayIntent.GUILD_MESSAGE_REACTIONS);
        }
    }
    public static Connection getDatabase() throws SQLException{
        return new DiscordSRVUtils().getDatabaseFile();
    }
    public Connection getDatabaseFile() throws SQLException {
        if (!SQLEnabled) {
            return DriverManager.getConnection("jdbc:hsqldb:file:" + getDataFolder().toPath().resolve("Database") + ";hsqldb.lock_file=false", "SA", "");
        }

        return sql.getConnection();
    }
    public Connection getMemoryConnection() throws SQLException{
        return DriverManager.getConnection("jdbc:hsqldb:mem:MemoryDatabase", "SA", "");
    }
    @Override
    public void onDisable() {
        timer.cancel();
    }

    public Person getPersonByUUID(UUID uuid) {
        if (!Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
            if (Bukkit.getOfflinePlayer(uuid).isOnline()) {

            } else return null;
        }
        String UserID = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(uuid);
        if (UserID == null) {
            return new PersonImpl(uuid, null, this);
        }
        return new PersonImpl(uuid, DiscordSRV.getPlugin().getMainGuild().getMemberById(UserID), this);
    }
    public Person getPersonByDiscordID(Long id) {
        if (DiscordSRV.getPlugin().getMainGuild().getMemberById(id) == null) return null;
        UUID uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(id.toString());
        if (uuid == null) return new PersonImpl(null, DiscordSRV.getPlugin().getMainGuild().getMemberById(id), this);
        return new PersonImpl(uuid, DiscordSRV.getPlugin().getMainGuild().getMemberById(id), this);
    }
}
