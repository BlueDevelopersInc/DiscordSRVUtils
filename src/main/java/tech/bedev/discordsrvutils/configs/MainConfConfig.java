package tech.bedev.discordsrvutils.configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.List;

@ConfHeader("#_____  _                       _  _____ _______      ___    _ _   _ _     \n" +
		"#|  __ \\(_)                     | |/ ____|  __ \\ \\    / / |  | | | (_) |    \n" +
		"#| |  | |_ ___  ___ ___  _ __ __| | (___ | |__) \\ \\  / /| |  | | |_ _| |___ \n" +
		"#| |  | | / __|/ __/ _ \\| '__/ _` |\\___ \\|  _  / \\ \\/ / | |  | | __| | / __|\n" +
		"#| |__| | \\__ \\ (__ (_) | | | (_| |____) | | \\ \\  \\  /  | |__| | |_| | \\__ \\\n" +
		"#|_____/|_|___/\\___\\___/|_|  \\__,_|_____/|_|  \\_\\  \\/    \\____/ \\__|_|_|___/\n" +
		"                                                                            \n" +
		"                ")
public interface MainConfConfig
{

	@ConfKey("update_checker")
	@ConfDefault.DefaultBoolean(true)
	@ConfComments("\n#Should we notify you of new updates?")
	@AnnotationBasedSorter.Order(10)
	boolean isUpdateChecker();


	@ConfKey("recomended_version")
	@ConfDefault.DefaultString("1.16")
	@ConfComments("\n#Version recommended to join with")
	@AnnotationBasedSorter.Order(20)
	String RecommendedVersion();

	@ConfKey("welcomer_channel")
	@ConfDefault.DefaultLong(0)
	@ConfComments("\n#Channel to send welcomer in")
	@AnnotationBasedSorter.Order(30)
	Long WelcomerChannel();

	@ConfKey("welcomer_message")
	@ConfDefault.DefaultStrings({"" +
			"\uD83D\uDD38 **Welcome [User_Name] To The server!**",
			"",
			"",
			"\uD83D\uDD38 **Server ip** | play.example.com",
			"",
			"",
			"\uD83D\uDD38 **Store** | store.example.com"})
	@ConfComments("\n#welcomer_message. We will send this message when a discord user joins\n")
	@AnnotationBasedSorter.Order(40)
	List<String> WelcomerMessage();

	@ConfKey("mc_welcomer_embed_color")
	@ConfDefault.DefaultString("RED")
	@ConfComments("\n#Color for embed to send in Discord when someone joins.")
	@AnnotationBasedSorter.Order(45)
	String WelcomerEmbedColor();

	@ConfKey("welcomer_ignore_bots")
	@ConfDefault.DefaultBoolean(true)
	@ConfComments("\n#Ignore welcomer message for bots?")
	@AnnotationBasedSorter.Order(46)
	boolean isIgnoreBots();

	@ConfKey("mc_welcomer_message")
	@ConfDefault.DefaultString("&b[User_Name] &aJoined the Discord server")
	@ConfComments("\n#Message to send on minecraft when someone joins the server.")
	@AnnotationBasedSorter.Order(50)
	String McWelcomerMessage();

	@ConfKey("join_message_to_online_players")
	@ConfDefault.DefaultBoolean(true)
	@ConfComments("\n#Should we send join message to online players?")
	@AnnotationBasedSorter.Order(60)
	boolean isJoinMessageToOnlinePlayers();

	@ConfKey("essentials_afk_messages")
	@ConfDefault.DefaultBoolean(true)
	@ConfComments("\n#Should we send essentials afk messages to discord?")
	@AnnotationBasedSorter.Order(70)
	boolean isEssentialsAfkMessages();

	@ConfKey("essentials_player_afk_message")
	@ConfDefault.DefaultStrings({"**[Player_Name] is now afk**"})
	@ConfComments("\n#Message to send when someone is essentials afk")
	@AnnotationBasedSorter.Order(80)
	List<String> EssentialsAfkMessage();

	@ConfKey("essentials_player_no_longer_afk_message")
	@ConfDefault.DefaultStrings({"**[Player_Name]** is no longer afk**"})
	@ConfComments("\n#Message to send when someone is no longer essentials afk")
	@AnnotationBasedSorter.Order(90)
	List<String> EssentialsNoLongerAfkMessage();


}
