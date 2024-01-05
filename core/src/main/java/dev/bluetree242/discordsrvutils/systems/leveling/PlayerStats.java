/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2024 BlueTree242
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

package dev.bluetree242.discordsrvutils.systems.leveling;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.events.LevelupEvent;
import dev.bluetree242.discordsrvutils.jooq.tables.LevelingTable;
import dev.bluetree242.discordsrvutils.utils.Utils;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import lombok.Getter;
import lombok.Setter;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerStats {
    private final DiscordSRVUtils core;
    @Getter
    private final UUID uuid;
    @Getter
    private final int minecraftMessages;
    @Getter
    private final int discordMessages;
    @Getter
    private final int rank;
    @Getter
    @Setter
    private String name;
    @Getter
    private int level;
    @Getter
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

    public static int getTotalXpRequired(int level) {
        return (int) (5 * (Math.pow(level, 2)) + (50 * level) + 100); //mee6's algorithm
    }

    public void setLevel(int level) {
        DSLContext conn = core.getDatabaseManager().jooq();
        conn.update(LevelingTable.LEVELING).set(LevelingTable.LEVELING.LEVEL, level)
                .where(LevelingTable.LEVELING.UUID.eq(uuid.toString()))
                .execute();
        this.level = level;
    }

    public boolean setXP(int xp) {
        return setXP(xp, null);
    }

    public int getTotalXpRequired() {
        return getTotalXpRequired(level);
    }

    /**
     * @param xp XP to add
     * @return true if player leveled up, false if not
     */
    public boolean setXP(int xp, LevelupEvent event) {
        DSLContext conn = core.getDatabaseManager().jooq();
        if (xp >= getTotalXpRequired()) {
            int[] newResults = getNewLevelAndXP(xp);
            int newLevel = newResults[0];
            int newXP = newResults[1];
            conn.update(LevelingTable.LEVELING)
                    .set(LevelingTable.LEVELING.LEVEL, newLevel)
                    .set(LevelingTable.LEVELING.XP, newXP)
                    .where(LevelingTable.LEVELING.UUID.eq(uuid.toString()))
                    .execute();
            this.level = newLevel;
            this.xp = newXP;
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

    private int[] getNewLevelAndXP(int xp) {
        int newLevel = level;
        boolean finished = false;
        int remainingXP = xp;
        while (!finished) {
            int required = getTotalXpRequired(newLevel);
            if (required > remainingXP) {
                finished = true;
            } else {
                remainingXP = remainingXP - required;
                newLevel++;
            }
        }
        return new int[]{newLevel, remainingXP};
    }

    public int getXpPercentage() {
        return (int) (((double) xp) * 100 / (double) getTotalXpRequired());
    }

    private void handleRewards() {
        LevelingManager manager = core.getLevelingManager();
        String id = core.getDiscordSRV().getDiscordId(uuid);
        if (id == null) return;
        Member member = Utils.retrieveMember(core.getDiscordSRV().getMainGuild(), Long.parseLong(id));
        if (member == null) return;
        Collection actions = new ArrayList<>();
        for (Role role : manager.getLevelingRewardsManager().getRolesToRemove(level)) {
            if (member.getRoles().contains(role))
                actions.add(core.getPlatform().getDiscordSRV().getMainGuild().removeRoleFromMember(member, role).reason("User Leveled Up"));
        }
        List<Role> toAdd = manager.getLevelingRewardsManager().getRolesForLevel(level);
        for (Role role : toAdd) {
            actions.add(core.getPlatform().getDiscordSRV().getMainGuild().addRoleToMember(member, role).reason("User Leveled Up"));
        }
        if (!actions.isEmpty())
            RestAction.allOf(actions).queue();
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

    public List<Role> getRoles() {
        return core.getLevelingManager().getLevelingRewardsManager().getRolesForLevel(level);
    }
}
