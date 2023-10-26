/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2023 BlueTree242
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

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.jooq.tables.LevelingTable;
import dev.bluetree242.discordsrvutils.jooq.tables.records.LevelingRecord;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Result;

import java.time.Duration;
import java.util.*;

@RequiredArgsConstructor
public class LevelingManager {
    public final Long MAP_EXPIRATION_NANOS = Duration.ofSeconds(60L).toNanos();
    public final Map<UUID, Long> antispamMap = new HashMap<>();
    private final DiscordSRVUtils core;
    @Getter
    private final LevelingRewardsManager levelingRewardsManager;
    private boolean adding = false;

    public PlayerStats getCachedStats(UUID uuid) {
        return cachedUUIDS.get(uuid);
    }

    public PlayerStats getCachedStats(long discordID) {
        UUID uuid = core.getDiscordSRV().getUuid(discordID + "");
        if (uuid == null) return null;
        return cachedUUIDS.get(uuid);
    }

    public boolean isLinked(UUID uuid) {
        String discord = core.getDiscordSRV().getDiscordId(uuid);
        return discord != null;
    }

    public PlayerStats getPlayerStats(long discordID) {
        DSLContext conn = core.getDatabaseManager().jooq();
        UUID uuid = core.getDiscordSRV().getUuid(discordID + "");
        if (uuid == null) return null;
        return getPlayerStats(uuid);
    }

    public PlayerStats getPlayerStats(String name) {
        DSLContext conn = core.getDatabaseManager().jooq();
        return getPlayerStats(conn, name);
    }

    public PlayerStats getPlayerStats(UUID uuid) {
        DSLContext conn = core.getDatabaseManager().jooq();
        List<LevelingRecord> records = conn.selectFrom(LevelingTable.LEVELING).orderBy(LevelingTable.LEVELING.LEVEL.desc()).fetch();
        int num = 0;
        for (LevelingRecord record : records) {
            num++;
            if (record.getUuid().equals(uuid.toString())) {
                return getPlayerStats(record, num);
            }
        }
        return null;
    }

    public PlayerStats getPlayerStats(DSLContext conn, String name) {
        List<LevelingRecord> records = conn.selectFrom(LevelingTable.LEVELING).orderBy(LevelingTable.LEVELING.LEVEL.desc()).fetch();
        int num = 0;
        for (LevelingRecord record : records) {
            num++;
            if (record.getName().equals(name)) {
                return getPlayerStats(record, num);
            }
        }
        return null;
    }

    public PlayerStats getPlayerStats(LevelingRecord r, int rank) {
        PlayerStats stats = new PlayerStats(core,
                UUID.fromString(r.getUuid()),
                r.getName(), r.getLevel(),
                r.getXp(),
                r.getMinecraftmessages() == null ? 0 : r.getMinecraftmessages(),
                r.getDiscordmessages() == null ? 0 : r.getDiscordmessages(),
                rank);
        if (!adding)
            cachedUUIDS.put(stats.getUuid(), stats);
        return stats;
    }    public LoadingCache<UUID, PlayerStats> cachedUUIDS = Caffeine.newBuilder()
            .maximumSize(120)
            .expireAfterWrite(Duration.ofMinutes(1))
            .refreshAfterWrite(Duration.ofSeconds(30))
            .build(key -> {
                DiscordSRVUtils core = DiscordSRVUtils.get();
                adding = true;
                PlayerStats stats = getPlayerStats(key);
                adding = false;
                return stats;
            });

    public List<PlayerStats> getLeaderboard(int max) {
        DSLContext conn = core.getDatabaseManager().jooq();
        List<LevelingRecord> records = conn.selectFrom(LevelingTable.LEVELING).orderBy(LevelingTable.LEVELING.LEVEL.desc()).limit(max).fetch();
        List<PlayerStats> leaderboard = new ArrayList<>();
        int num = 0;
        for (LevelingRecord record : records) {
            num++;
            leaderboard.add(getPlayerStats(record, num));
        }
        return leaderboard;
    }

    public void resetLeveling() {
        core.getDatabaseManager().jooq().update(LevelingTable.LEVELING).set(LevelingTable.LEVELING.LEVEL, 0).set(LevelingTable.LEVELING.XP, 0).execute();
    }

    public void convertToMee6() {
        DSLContext conn = core.getDatabaseManager().jooq();
        Result<LevelingRecord> results = conn.selectFrom(LevelingTable.LEVELING).fetch();

        for (LevelingRecord result : results) {
            if (cachedUUIDS.getIfPresent(UUID.fromString(result.getUuid())) != null)
                cachedUUIDS.invalidate(cachedUUIDS.get(UUID.fromString(result.getUuid())));
            int totalXP = (result.getLevel() * 300) + result.getXp();
            int level = 0;
            Integer xp = null;
            while (xp == null) {
                totalXP = totalXP - getRequiredXP(level);
                if (totalXP > 0) level++;
                else if (totalXP == 0) {
                    xp = 0;
                    level++;
                } else xp = getRequiredXP(level) - Math.abs(totalXP);
            }
            conn.update(LevelingTable.LEVELING)
                    .set(LevelingTable.LEVELING.LEVEL, level)
                    .set(LevelingTable.LEVELING.XP, xp)
                    .where(LevelingTable.LEVELING.UUID.eq(result.getUuid())).execute();
        }
    }

    private int getRequiredXP(int level) {
        return (int) (5 * (Math.pow(level, 2)) + (50 * level) + 100);
    }




}
