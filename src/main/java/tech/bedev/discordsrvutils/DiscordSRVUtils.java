package tech.bedev.discordsrvutils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.OnlineStatus;
import github.scarsz.discordsrv.dependencies.jda.api.requests.GatewayIntent;
import github.scarsz.discordsrv.objects.Lag;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.dazzleconf.error.InvalidConfigException;
import tech.bedev.discordsrvutils.commands.*;
import tech.bedev.discordsrvutils.commands.tabcompletion.DiscordSRVUtilsTabCompleter;
import tech.bedev.discordsrvutils.configs.*;
import tech.bedev.discordsrvutils.events.*;
import tech.bedev.discordsrvutils.exceptions.StartupException;
import tech.bedev.discordsrvutils.leaderboard.LeaderboardManager;
import tech.bedev.discordsrvutils.leaderboard.LeaderboardManagerImpl;
import tech.bedev.discordsrvutils.person.Person;
import tech.bedev.discordsrvutils.person.PersonImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

public class DiscordSRVUtils extends JavaPlugin
{
	public static boolean isReady = false;
	public static boolean placeholderAPI;
	public static Timer timer = new Timer();
	public static SQLConfig SQLconfig;
	public static LevelingConfig Levelingconfig;
	public static BotSettingsConfig BotSettingsconfig;
	public static ModerationConfig Moderationconfig;
	public static BansIntegrationConfig BansIntegrationconfig;
	public static MainConfConfig Config;
	public static Timer timer2 = new Timer();
	public static SuggestionsConfig SuggestionsConfig;
	public final Map<UUID, Long> lastChattedAt = new HashMap<>();
	public DiscordSRVEventListener discordListener;
	public JDAEvents jdaListener;
	public HikariDataSource sql;
	public String username;
	public int port;
	public String host;
	public boolean isSQLEnabled;
	public ConfManager<SQLConfig> SQLConfigManager = ConfManager.create(getDataFolder().toPath(), "SQL.yml", SQLConfig.class);
	public ConfManager<LevelingConfig> LevelingConfigManager = ConfManager.create(getDataFolder().toPath(), "Leveling.yml", LevelingConfig.class);
	public ConfManager<BotSettingsConfig> BotSettingsConfigManager = ConfManager.create(getDataFolder().toPath(), "BotSettings.yml", BotSettingsConfig.class);
	public ConfManager<ModerationConfig> ModerationConfigManager = ConfManager.create(getDataFolder().toPath(), "Moderation.yml", ModerationConfig.class);
	public ConfManager<BansIntegrationConfig> BansIntegrationConfigManager = ConfManager.create(getDataFolder().toPath(), "BansIntegration.yml", BansIntegrationConfig.class);
	public ConfManager<MainConfConfig> MainConfManager = ConfManager.create(getDataFolder().toPath(), "config.yml", MainConfConfig.class);
	public Map<Long, Long> tempmute = new HashMap<>();
	public ConfManager<SuggestionsConfig> SuggestionsConfManager = ConfManager.create(getDataFolder().toPath(), "suggestions.yml", SuggestionsConfig.class);
	Path databaseFile;

	public static JDA getJda()
	{
		return DiscordSRV.getPlugin().getJda();
	}

	public static DiscordSRVUtils getMainClass()
	{
		return new DiscordSRVUtils();
	}

	public static Connection getDatabase() throws SQLException
	{
		return new DiscordSRVUtils().getDatabaseFile();
	}

	@Override
	public void onEnable()
	{
		try
		{
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
			SuggestionsConfManager.reloadConfig();
			SuggestionsConfig = SuggestionsConfManager.reloadConfigData();
			if(SQLconfig.isEnabled())
			{
				HikariConfig hikariConf = new HikariConfig();
				hikariConf.setJdbcUrl("jdbc:" + "mysql" + "://" +
						SQLconfig.getHost() +
						":" + SQLconfig.getPort() + "/" + SQLconfig.getDatabaseName());
				hikariConf.setUsername(SQLconfig.getUsername());
				hikariConf.setPassword(SQLconfig.getPassword());
				hikariConf.setMaximumPoolSize(20);
				sql = new HikariDataSource(hikariConf);
				port = SQLconfig.getPort();
				username = SQLconfig.getUsername();
				host = SQLconfig.getHost();
				isSQLEnabled = true;
			}
			else
			{
				port = 3306;
				username = null;
				host = null;
				isSQLEnabled = false;
			}
		}
		catch(IOException | InvalidConfigException e)
		{
			e.printStackTrace();
		}
		try
		{

			if(!this.getDescription().getName().equals("DiscordSRVUtils"))
			{
				setEnabled(false);
				System.out.println("[DiscordSRVUtils] Detected plugin name change.");
				return;
			}
			String storage = "Unknown";
			if(SQLconfig.isEnabled())
			{
				storage = "MySQL";
			}
			else
			{
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
			if(!getServer().getPluginManager().isPluginEnabled("DiscordSRV"))
			{
				getLogger().warning("DiscordSRVUtils could not be enabled. DiscordSRV is not installed or is not enabled.");
				getLogger().warning("We will add support for no discordsrv in the future.");
				setEnabled(false);
				return;
			}
			databaseFile = getDataFolder().toPath().resolve("Database");
			String jdbcUrl = "jdbc:hsqldb:file:" + databaseFile.toAbsolutePath();
			try(Connection conn = getDatabaseFile())
			{
				conn.prepareStatement("CREATE TABLE IF NOT EXISTS discordsrvutils_ticket_allowed_roles (TicketID int, RoleID Bigint)").execute();
				conn.prepareStatement("CREATE TABLE IF NOT EXISTS discordsrvutils_tickets (" +
						"TicketID int, Name Varchar(500), " +
						"MessageId Bigint, " +
						"Opened_Category Bigint, " +
						"Closed_Category Bigint, ChannelID Bigint)").execute();
				conn.prepareStatement("CREATE TABLE IF NOT EXISTS discordsrvutils_Opened_Tickets (UserID Bigint, MessageID Bigint, TicketID Bigint, Channel_id Bigint)").execute();
				conn.prepareStatement("CREATE TABLE IF NOT EXISTS discordsrvutils_Closed_Tickets (UserID Bigint, MessageID Bigint, TicketID Bigint, Channel_id Bigint, Closed_Message Bigint)").execute();
				conn.prepareStatement("CREATE TABLE IF NOT EXISTS discordsrvutils_leveling (userID Bigint, unique_id varchar(36), level int, XP int, DiscordMessages Bigint, MinecraftMessages Bigint)").execute();
				conn.prepareStatement("CREATE TABLE IF NOT EXISTS discordsrvutils_suggestions (Userid Bigint, Channel Bigint, Message Bigint, Suggestion varchar(10000), Number int, isAccepted varchar(50), staffReply varchar(1000), staffReplier Bigint)").execute();
				PreparedStatement p1 = conn.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ? AND COLUMN_NAME = ?");
				p1.setString(1, "discordsrvutils_leveling");
				p1.setString(2, "DiscordMessages");
				ResultSet r1 = p1.executeQuery();
				if(!r1.next())
				{
					conn.prepareStatement("ALTER TABLE discordsrvutils_leveling ADD COLUMN DiscordMessages Bigint").execute();
					conn.prepareStatement("ALTER TABLE discordsrvutils_leveling ADD COLUMN MinecraftMessages Bigint").execute();
				}
				PreparedStatement p2 = conn.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ? AND COLUMN_NAME = ?");
				p2.setString(1, "discordsrvutils_suggestions");
				p2.setString(2, "isAccepted");
				ResultSet r2 = p2.executeQuery();
				if(!r2.next())
				{
					conn.prepareStatement("ALTER TABLE discordsrvutils_suggestions ADD COLUMN isAccepted varchar(50)").execute();
					conn.prepareStatement("ALTER TABLE discordsrvutils_suggestions ADD COLUMN staffReply varchar(1000)").execute();
					conn.prepareStatement("ALTER TABLE discordsrvutils_suggestions ADD COLUMN staffReplier Bigint").execute();
				}
			}
			catch(SQLException exception)
			{
				exception.printStackTrace();
			}
			try(Connection conn = getMemoryConnection())
			{
				conn.prepareStatement("CREATE TABLE suggestions_Awaiting (userid Bigint, Channel Bigint, LastOutput Bigint)").execute();
				conn.prepareStatement("CREATE TABLE status (Status int)").execute();
				conn.prepareStatement("CREATE TABLE tickets_creating (UserID Bigint, Channel_id Bigint, step int, Name Varchar(500), MessageId Bigint, Opened_Category Bigint, Closed_Category Bigint, TicketID int); ").execute();
				conn.prepareStatement("CREATE TABLE discordsrvutils_ticket_allowed_roles (UserID Bigint, Channel_id Bigint, RoleID Bigint)").execute();
				conn.prepareStatement("CREATE TABLE discordsrvutils_Awaiting_Edits (Channel_id Bigint, UserID Bigint, Type int, MessageID Bigint, TicketID int)").execute();
				conn.prepareStatement("CREATE TABLE helpmsges (userid Bigint, Channel Bigint, MessageID Bigint, lastOutput Bigint, Page int)").execute();
				conn.prepareStatement("CREATE TABLE helpmsgesreply (userid Bigint, Channel Bigint, SuggestionID Bigint, step int, Awaiting_isAccepted Bigint, isAccepted varchar(50))").execute();
			}
			catch(SQLException exception)
			{
				exception.printStackTrace();
			}
			if(getServer().getPluginManager().isPluginEnabled("Essentials"))
			{
				getServer().getPluginManager().registerEvents(new EssentialsAfk(this), this);
			}
			if(getServer().getPluginManager().isPluginEnabled("AdvancedBan"))
			{
				getServer().getPluginManager().registerEvents(new AdvancedBanListener(this), this);
			}

			Objects.requireNonNull(getCommand("discordsrvutils")).setExecutor(new DiscordSRVUtilsCommand(this));
			Objects.requireNonNull(getCommand("discordsrvutils")).setTabCompleter(new DiscordSRVUtilsTabCompleter());
			this.discordListener = new DiscordSRVEventListener(this);
			this.jdaListener = new JDAEvents(this);
			Bukkit.getPluginManager().registerEvents(new BukkitEventListener(this), this);

			DiscordSRV.api.subscribe(discordListener);
			getCommand("setlevel").setExecutor(new LevelSetCommand(this));
			getCommand("addlevels").setExecutor(new LevelAddCommand(this));
			getCommand("removelevels").setExecutor(new LevelRemoveCommand(this));
			getCommand("setxp").setExecutor(new XPSetCommand(this));
			getCommand("addxp").setExecutor(new XPAddCommand(this));
			getCommand("removexp").setExecutor(new XPRemoveCommand(this));

			if(DiscordSRV.isReady)
			{
				getJda().addEventListener(jdaListener);
				String status = BotSettingsconfig.getOnlineState();
				if(status != null)
				{
					switch(status.toUpperCase())
					{
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
			if(newVersion.equalsIgnoreCase(getDescription().getVersion()))
			{
				getLogger().info(ChatColor.GREEN + "No new version available. (" + newVersion + ")");
			}
			else
			{
				getLogger().info(ChatColor.GREEN + "A new version is available. Please update ASAP!" + " Your version: " + ChatColor.YELLOW + getDescription().getVersion() + ChatColor.GREEN + " New version: " + ChatColor.YELLOW + newVersion);
			}

			int pluginId = 9456; // <-- Replace with the id of your plugin!
			placeholderAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
			if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
			{
				Bukkit.getScheduler().runTask(this, () ->
				{
					new PlaceholderAPI().register();

				});
			}
			Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Lag(), 100L, 1L);
		}
		catch(Exception ex)
		{
			try
			{
				throw new StartupException();
			}
			catch(StartupException e)
			{
				e.printStackTrace();
			}
		}
		timer2.schedule(new TimeHandler(this), 0, 1000);

	}

	@Override
	public void onLoad()
	{
		if(!this.getDescription().getName().equals("DiscordSRVUtils"))
		{
			setEnabled(false);
		}
		if(getServer().getPluginManager().getPlugin("DiscordSRV") != null)
		{
			DiscordSRV.api.requireIntent(GatewayIntent.GUILD_MESSAGE_REACTIONS);
		}
	}

	public Connection getDatabaseFile() throws SQLException
	{
		if(!isSQLEnabled)
		{
			return DriverManager.getConnection("jdbc:hsqldb:file:" + getDataFolder().toPath().resolve("Database") + ";hsqldb.lock_file=false", "SA", "");
		}

		return sql.getConnection();
	}

	public Connection getMemoryConnection() throws SQLException
	{
		return DriverManager.getConnection("jdbc:hsqldb:mem:MemoryDatabase", "SA", "");
	}

	@Override
	public void onDisable()
	{
		timer.cancel();
	}

	public Person getPersonByUUID(UUID uuid)
	{
		if(uuid == null) return null;
		if(!Bukkit.getOfflinePlayer(uuid).hasPlayedBefore())
		{
			if(Bukkit.getOfflinePlayer(uuid).isOnline())
			{

			}
			else return null;
		}
		String UserID = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(uuid);
		if(UserID == null)
		{
			return new PersonImpl(uuid, null, this);
		}
		return new PersonImpl(uuid, DiscordSRV.getPlugin().getMainGuild().getMemberById(UserID), this);
	}

	public Person getPersonByDiscordID(Long id)
	{
		if(DiscordSRV.getPlugin().getMainGuild().getMemberById(id) == null) return null;
		UUID uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(id.toString());
		if(uuid == null) return new PersonImpl(null, DiscordSRV.getPlugin().getMainGuild().getMemberById(id), this);
		return new PersonImpl(uuid, DiscordSRV.getPlugin().getMainGuild().getMemberById(id), this);
	}

	public LeaderboardManager getLeaderBoardManager()
	{
		return new LeaderboardManagerImpl(this);
	}

}
