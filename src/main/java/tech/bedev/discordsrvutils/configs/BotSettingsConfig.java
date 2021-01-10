package tech.bedev.discordsrvutils.configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

import java.util.List;

public interface BotSettingsConfig
{

	@ConfDefault.DefaultString("!")
	@ConfComments("#Prefix used to execute commands.")
	@AnnotationBasedSorter.Order(10)
	String getBotPrefix();

	@ConfDefault.DefaultString("global")
	@ConfComments("\n #Channel used to send messages. Depends on your DiscordSRV Config")
	@AnnotationBasedSorter.Order(20)
	String getChatChannel();

	@ConfDefault.DefaultBoolean(true)
	@ConfComments("\n #Should we update the status?")
	@AnnotationBasedSorter.Order(30)
	boolean isStatusUpdates();

	@ConfDefault.DefaultInteger(12)
	@ConfComments("\n #Status will update every 12 seconds for example")
	@AnnotationBasedSorter.Order(40)
	int getStatusUpdateInterval();

	@ConfDefault.DefaultStrings({"Playing Minecraft", "Watching online players", "Listening to People chatting"})
	@ConfComments("\n #Status that will update.")
	@AnnotationBasedSorter.Order(50)
	List<String> getStatus();

	@ConfDefault.DefaultString("ONLINE")
	@ConfComments("\n #Status for your bot, DND or ONLINE or IDLE")
	@AnnotationBasedSorter.Order(60)
	String getOnlineState();

	@ConfDefault.DefaultBoolean(false)
	@ConfComments("\n #If you use bungee, enable this in all your servers except lobby (Should use mySQL)")
	@Order(70)
	boolean isBungee();
}
