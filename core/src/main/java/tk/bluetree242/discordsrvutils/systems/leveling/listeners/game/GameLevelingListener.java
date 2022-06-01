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

package tk.bluetree242.discordsrvutils.systems.leveling.listeners.game;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.events.MinecraftLevelupEvent;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;
import tk.bluetree242.discordsrvutils.jooq.tables.LevelingTable;
import tk.bluetree242.discordsrvutils.jooq.tables.records.LevelingRecord;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import tk.bluetree242.discordsrvutils.platform.events.PlatformChatEvent;
import tk.bluetree242.discordsrvutils.platform.events.PlatformJoinEvent;
import tk.bluetree242.discordsrvutils.platform.listener.PlatformListener;
import tk.bluetree242.discordsrvutils.systems.leveling.MessageType;
import tk.bluetree242.discordsrvutils.systems.leveling.PlayerStats;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;

@RequiredArgsConstructor
public class GameLevelingListener extends PlatformListener {
    private final DiscordSRVUtils core;

    public void onJoin(PlatformJoinEvent e) {
        core.getAsyncManager().executeAsync(() -> {
            try (Connection conn = core.getDatabaseManager().getConnection()) {
                DSLContext jooq = core.getDatabaseManager().jooq(conn);
                LevelingRecord record = jooq
                        .selectFrom(LevelingTable.LEVELING)
                        .where(LevelingTable.LEVELING.UUID.eq(e.getPlayer().getUniqueId().toString()))
                        .fetchOne();
                if (record == null) {
                    jooq.insertInto(LevelingTable.LEVELING)
                            .set(LevelingTable.LEVELING.UUID, e.getPlayer().getUniqueId().toString())
                            .set(LevelingTable.LEVELING.NAME, e.getPlayer().getName())
                            .set(LevelingTable.LEVELING.LEVEL, 0)
                            .set(LevelingTable.LEVELING.XP, 0)
                            .execute();
                } else {
                    if (!record.getName().equals(e.getPlayer().getName())) {
                        jooq.update(LevelingTable.LEVELING)
                                .set(LevelingTable.LEVELING.NAME, e.getPlayer().getName())
                                .where(LevelingTable.LEVELING.UUID.eq(e.getPlayer().getUniqueId().toString()))
                                .execute();
                    }
                }
            } catch (SQLException ex) {
                throw new UnCheckedSQLException(ex);
            }
        });
    }

    public void onChat(PlatformChatEvent e) {
        if (!core.getLevelingConfig().enabled()) return;
        if (e.isCancelled()) return;
        core.getAsyncManager().executeAsync(() -> {
            try (Connection conn = core.getDatabaseManager().getConnection()) {
                DSLContext jooq = core.getDatabaseManager().jooq(conn);
                PlayerStats stats = core.getLevelingManager().getPlayerStats(e.getPlayer().getUniqueId(), jooq);
                if (stats == null) {
                    return;
                }
                if (core.getLevelingConfig().antispam_messages()) {
                    Long val = core.getLevelingManager().antispamMap.get(stats.getUuid());
                    if (val == null) {
                        core.getLevelingManager().antispamMap.put(stats.getUuid(), System.nanoTime());
                    } else {
                        if (!(System.nanoTime() - val >= core.getLevelingManager().MAP_EXPIRATION_NANOS)) return;
                        core.getLevelingManager().antispamMap.remove(stats.getUuid());
                        core.getLevelingManager().antispamMap.put(stats.getUuid(), System.nanoTime());
                    }
                }
                int toAdd = new SecureRandom().nextInt(50);
                boolean leveledUp = stats.setXP(stats.getXp() + toAdd, new MinecraftLevelupEvent(stats, e.getPlayer()), jooq);
                stats.addMessage(MessageType.MINECRAFT, jooq);
                if (leveledUp) {
                    e.getPlayer().sendMessage(PlaceholdObjectList.ofArray(core, new PlaceholdObject(core, stats, "stats"), new PlaceholdObject(core, e.getPlayer(), "player")).apply(String.join("\n", core.getLevelingConfig().minecraft_levelup_message()), e.getPlayer()));
                }
            } catch (SQLException ex) {
                throw new UnCheckedSQLException(ex);
            }
        });

    }
}
