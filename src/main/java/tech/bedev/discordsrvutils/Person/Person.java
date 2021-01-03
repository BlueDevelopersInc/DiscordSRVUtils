package tech.bedev.discordsrvutils.Person;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;

import java.util.UUID;

public interface Person {





    void setLevel(int level);
    void addLevels(int levels);
    void removeLevels(int levels);
    void clearLevels();

    void setXP(int xp);
    void addXP(int xp);
    void removeXP(int xp);
    void clearXP();
    void unLink();

    int getLevel();
    int getXP();
    String getRank();

    boolean isLinked();


    UUID getMinecraftUUID();
    Member getMemberOnMainGuild();


    void insertLeveling();

}
