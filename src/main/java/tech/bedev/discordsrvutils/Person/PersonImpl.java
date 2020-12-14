package tech.bedev.discordsrvutils.Person;



import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import tech.bedev.discordsrvutils.DiscordSRVUtils;

import java.util.UUID;

    public class PersonImpl implements Person {

    private final UUID uuid;
    private final Member DiscordUser;
    private DiscordSRVUtils core;

    public PersonImpl(UUID uuid, Member DiscordUser, DiscordSRVUtils core) {
        this.uuid = uuid;
        this.DiscordUser = DiscordUser;
        this.core = core;
    }
    @Override
    public void setLevel(int level) {


    }

    @Override
    public void addLevels(int levels) {

    }

    @Override
    public void removeLevels(int levels) {

    }

    @Override
    public void clearLevels() {

    }

    @Override
    public void setXP(int xp) {

    }

    @Override
    public void addXP(int xp) {

    }

    @Override
    public void removeXP(int xp) {

    }

    @Override
    public void clearXP() {

    }

    @Override
    public int getLevel() {
        return -1;
    }

    @Override
    public int getXP() {
        return -1;
    }

    @Override
    public UUID getMinecraftUUID() {
        if (this.uuid == null) return null;
        return this.uuid;
    }

    @Override
    public Member getMemberOnMainGuild() {
        if (this.DiscordUser == null) return null;
        return this.DiscordUser;
    }
}
