package tech.bedev.discordsrvutils.person;

import java.util.UUID;

public interface Person
{
	void addLevels(int levels);

	void removeLevels(int levels);

	void addXP(int xp);

	void removeXP(int xp);

	void clearXP();

	int getLevel();

	void setLevel(int level);

	int getXP();

	void setXP(int xp);

	String getRank();

	boolean isLinked();

	UUID getMinecraftUUID();


	void insertLeveling();

	Long getDiscordMessages();

	Long getMinecraftMessages();

	Long getTotalMessages();

	void addMessages(MessageType msg, int number);

	boolean isBukkitCached();

	void reloadCache();

}
