package tech.bedev.discordsrvutils.Person;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;

import java.util.UUID;

public interface Person {


    void addLevels(int levels);

    void removeLevels(int levels);

    void clearLevels();

    void addXP(int xp);

    void removeXP(int xp);

    void clearXP();

    void unLink();

    int getLevel();

    void setLevel(int level);

    int getXP();

    void setXP(int xp);

    String getRank();

    boolean isLinked();


    UUID getMinecraftUUID();

    Member getMemberOnMainGuild();


    void insertLeveling();

    Long getDiscordMessages();

    Long getMinecraftMessages();

    Long getTotalMessages();

    void addMessages(MessageType msg, int number);

    void removeMessages(MessageType msg, int number);

    void setMessages(MessageType msg, int number);

    boolean isBukkitCached();

    void reloadCache();

}
