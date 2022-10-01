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

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.json.JSONException;
import org.json.JSONObject;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;
import tk.bluetree242.discordsrvutils.jooq.tables.LevelingTable;
import tk.bluetree242.discordsrvutils.jooq.tables.records.LevelingRecord;
import tk.bluetree242.discordsrvutils.utils.FileWriter;
import tk.bluetree242.discordsrvutils.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LevelingManager {
    public final Long MAP_EXPIRATION_NANOS = Duration.ofSeconds(60L).toNanos();
    public final Map<UUID, Long> antispamMap = new HashMap<>();
    private final DiscordSRVUtils core;
    private boolean adding = false;
    //leveling roles jsonobject, Initialized on startup
    @Getter
    private JSONObject levelingRolesRaw;

    public void reloadLevelingRoles() {
        try {
            File levelingRoles = new File(core.getPlatform().getDataFolder(), core.fileseparator + "leveling-roles.json");
            if (!levelingRoles.exists()) {
                levelingRoles.createNewFile();
                FileWriter writer = new FileWriter(levelingRoles);
                writer.write(new JSONObject().put("_wiki", "https://wiki.discordsrvutils.xyz/leveling-roles/").toString(1));
                writer.close();
                levelingRolesRaw = new JSONObject();
            } else {
                levelingRolesRaw = new JSONObject(Utils.readFile(levelingRoles));
            }
        } catch (FileNotFoundException e) {
            core.getLogger().severe("Error creating leveling-roles.json");
            levelingRolesRaw = new JSONObject();
        } catch (IOException e) {
            core.getLogger().severe("Error creating leveling-roles.json: " + e.getMessage());
        } catch (JSONException e) {
            core.getLogger().severe("Error loading leveling-roles.json: " + e.getMessage());
        }
    }

    public PlayerStats getCachedStats(UUID uuid) {
        return cachedUUIDS.get(uuid);
    }        public PlayerStats getPlayerStats(long discordID) {
        UUID uuid = core.getDiscordSRV().getUuid(discordID + "");
        if (uuid == null) return null;
        DSLContext conn = core.getDatabaseManager().jooq();
        PlayerStats result = getPlayerStats(uuid, conn);
        try {
            conn.configuration().connectionProvider().acquire().close();
        } catch (SQLException throwables) {
            throw new UnCheckedSQLException(throwables);
        }
        return result;
    }public LoadingCache<UUID, PlayerStats> cachedUUIDS = Caffeine.newBuilder()
            .maximumSize(120)
            .expireAfterWrite(Duration.ofMinutes(1))
            .refreshAfterWrite(Duration.ofSeconds(30))
            .build(key -> {
                DiscordSRVUtils core = DiscordSRVUtils.get();
                adding = true;
                PlayerStats stats = getPlayerStats(key, core.getDatabaseManager().jooq());
                adding = false;
                return stats;
            });

    public PlayerStats getCachedStats(long discordID) {
        UUID uuid = core.getDiscordSRV().getUuid(discordID + "");
        if (uuid == null) return null;
        return cachedUUIDS.get(uuid);
    }

    public boolean isLinked(UUID uuid) {
        String discord = core.getDiscordSRV().getDiscordId(uuid);
        return discord != null;
    }

    public PlayerStats getPlayerStats(long discordID, DSLContext conn) {
        UUID uuid = core.getDiscordSRV().getUuid(discordID + "");
        if (uuid == null) return null;
        return getPlayerStats(uuid, conn);
    }



    public PlayerStats getPlayerStats(String name, DSLContext conn) {
        return getPlayerStats(conn, name);
    }

    public PlayerStats getPlayerStats(UUID uuid, DSLContext conn) {
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
    }

    public List<PlayerStats> getLeaderboard(int max, DSLContext conn) {
        List<LevelingRecord> records = conn.selectFrom(LevelingTable.LEVELING).orderBy(LevelingTable.LEVELING.LEVEL.desc()).limit(max).fetch();
        List<PlayerStats> leaderboard = new ArrayList<>();
        int num = 0;
        for (LevelingRecord record : records) {
            num++;
            leaderboard.add(getPlayerStats(record, num));
        }
        return leaderboard;
    }

    public void resetLeveling(DSLContext conn) {
        conn.update(LevelingTable.LEVELING).set(LevelingTable.LEVELING.LEVEL, 0).set(LevelingTable.LEVELING.XP, 0).execute();
    }

    public Role getRoleForLevel(int level) {
        Map<String, Object> map = levelingRolesRaw.toMap();
        List<String> keys = new ArrayList<>(map.keySet());
        keys = keys.stream()
                .filter(num -> {
                    try {
                        return Integer.parseInt(num) <= level;
                    } catch (NumberFormatException ex) {
                        return false; //not a level, maybe the _wiki one?
                    }
                })
                .collect(Collectors.toList());
        if (keys.isEmpty()) return null;
        keys.sort((o1, o2) -> Integer.parseInt(o2) - Integer.parseInt(o1));
        Long id = (Long) map.get(keys.get(0));
        if (id != null) {
            return core.getPlatform().getDiscordSRV().getMainGuild().getRoleById(id);
        }
        return null;
    }

    public List<Role> getRolesToRemove(Integer level) {

        List<Role> roles = new ArrayList<>();
        Map<String, Object> map = levelingRolesRaw.toMap();
        List<Object> values = new ArrayList<>(map.values());
        for (Object value : values) {
            if (!(value instanceof Long)) continue;
            Long id = (Long) value;
            roles.add(core.getPlatform().getDiscordSRV().getMainGuild().getRoleById(id));
        }
        if (level != null)
            roles.remove(getRoleForLevel(level));
        return roles;
    }




}
