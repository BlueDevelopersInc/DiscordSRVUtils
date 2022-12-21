/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2022 BlueTree242
 * -------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * END
 */

package tk.bluetree242.discordsrvutils.systems.leveling;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import lombok.Setter;
import org.jooq.DSLContext;
import org.jooq.TableField;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.events.LevelupEvent;
import tk.bluetree242.discordsrvutils.jooq.tables.LevelingTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerStats {
    private final DiscordSRVUtils core;
    private final UUID uuid;
    private final int minecraftMessages;
    private final int discordMessages;
    private final int rank;
    @Setter
    private String name;
    private int level;
    private int xp;

    public PlayerStats(DiscordSRVUtils core, UUID uuid, String name, int level, int xp, int minecraftMessages, int discordMessages, int rank) {
        this.core = core;
        this.uuid = uuid;
        this.name = name;
        this.level = level;
        this.xp = xp;
        this.minecraftMessages = minecraftMessages;
        this.discordMessages = discordMessages;
        this.rank = rank;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        DSLContext conn = core.getDatabaseManager().jooq();
        conn.update(LevelingTable.LEVELING).set(LevelingTable.LEVELING.LEVEL, level)
                .where(LevelingTable.LEVELING.UUID.eq(uuid.toString()))
                .execute();
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public boolean setXP(int xp) {
        DSLContext conn = core.getDatabaseManager().jooq();
        return setXP(xp, null);
    }

    public int getTotalXpRequired() {
        return (int) (5 * (Math.pow(level, 2)) + (50 * level) + 100); //mee6's algorithm
    }

    /**
     * @param xp XP to add
     * @return true if player leveled up, false if not
     */
    public boolean setXP(int xp, LevelupEvent event) {
        DSLContext conn = core.getDatabaseManager().jooq();
        if (xp >= getTotalXpRequired()) {
            conn.update(LevelingTable.LEVELING)
                    .set(LevelingTable.LEVELING.LEVEL, level + 1)
                    .set(LevelingTable.LEVELING.XP, 0)
                    .where(LevelingTable.LEVELING.UUID.eq(uuid.toString()))
                    .execute();
            this.level = level + 1;
            this.xp = 0;
            handleRewards();
            if (event != null)
                DiscordSRV.api.callEvent(event);
            return true;
        }
        conn.update(LevelingTable.LEVELING)
                .set(LevelingTable.LEVELING.XP, xp)
                .where(LevelingTable.LEVELING.UUID.eq(uuid.toString())).execute();
        this.xp = xp;
        return false;
    }

    public int getXpPercentage() {
        return (int) (((double) xp) * 100 / (double) getTotalXpRequired());
    }

    private void handleRewards() {
        LevelingManager manager = core.getLevelingManager();
        String id = core.getDiscordSRV().getDiscordId(uuid);
        if (id == null) return;
        Member member = core.getPlatform().getDiscordSRV().getMainGuild().retrieveMemberById(id).complete();
        if (member == null) return;
        Collection actions = new ArrayList<>();
        for (Role role : manager.getLevelingRewardsManager().getRolesToRemove(level)) {
            if (member.getRoles().contains(role))
                actions.add(core.getPlatform().getDiscordSRV().getMainGuild().removeRoleFromMember(member, role).reason("User Leveled Up"));
        }
        List<Role> toAdd = manager.getLevelingRewardsManager().getRolesForLevel(level);
        for (Role role : toAdd) {
            actions.add(core.getPlatform().getDiscordSRV().getMainGuild().addRoleToMember(member, role).reason("Account Linked"));
        }
        if (!actions.isEmpty())
            RestAction.allOf(actions).queue();
    }

    public int getMinecraftMessages() {
        return minecraftMessages;
    }

    public int getDiscordMessages() {
        return discordMessages;
    }

    public void addMessage(MessageType type) {
        DSLContext conn = core.getDatabaseManager().jooq();
        TableField toUpdate = null;
        int value = 0;
        switch (type) {
            case DISCORD:
                toUpdate = LevelingTable.LEVELING.DISCORDMESSAGES;
                value = discordMessages + 1;
                break;
            case MINECRAFT:
                toUpdate = LevelingTable.LEVELING.MINECRAFTMESSAGES;
                value = minecraftMessages + 1;
                break;
        }
        conn.update(LevelingTable.LEVELING)
                .set(toUpdate, value)
                .where(LevelingTable.LEVELING.UUID.eq(uuid.toString()))
                .execute();
    }

    public int getRank() {
        return rank;
    }

    public List<Role> getRoles() {
        return core.getLevelingManager().getLevelingRewardsManager().getRolesForLevel(level);
    }
}
