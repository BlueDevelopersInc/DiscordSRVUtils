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

package dev.bluetree242.discordsrvutils.systems.leveling.listeners.game;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.events.MinecraftLevelupEvent;
import dev.bluetree242.discordsrvutils.jooq.tables.LevelingTable;
import dev.bluetree242.discordsrvutils.platform.events.PlatformChatEvent;
import dev.bluetree242.discordsrvutils.platform.events.PlatformJoinEvent;
import dev.bluetree242.discordsrvutils.platform.listener.PlatformListener;
import dev.bluetree242.discordsrvutils.systems.leveling.MessageType;
import dev.bluetree242.discordsrvutils.systems.leveling.PlayerStats;
import dev.bluetree242.discordsrvutils.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;

@RequiredArgsConstructor
public class GameLevelingListener extends PlatformListener {
    private final DiscordSRVUtils core;

    public void onJoin(PlatformJoinEvent e) {
        core.getAsyncManager().executeAsync(() -> {
            PlayerStats stats = core.getLevelingManager().getPlayerStats(e.getPlayer().getUniqueId());
            DSLContext jooq = core.getDatabaseManager().jooq();
            if (stats == null) {
                jooq.insertInto(LevelingTable.LEVELING)
                        .set(LevelingTable.LEVELING.UUID, e.getPlayer().getUniqueId().toString())
                        .set(LevelingTable.LEVELING.NAME, e.getPlayer().getName())
                        .set(LevelingTable.LEVELING.LEVEL, 0)
                        .set(LevelingTable.LEVELING.XP, 0)
                        .execute();
            } else {
                if (!stats.getName().equals(e.getPlayer().getName())) {
                    jooq.update(LevelingTable.LEVELING)
                            .set(LevelingTable.LEVELING.NAME, e.getPlayer().getName())
                            .where(LevelingTable.LEVELING.UUID.eq(e.getPlayer().getUniqueId().toString()))
                            .execute();
                }
                stats.setName(e.getPlayer().getName());
                core.getLevelingManager().getLevelingRewardsManager().rewardIfOnline(stats);
            }
        });
    }

    public void onChat(PlatformChatEvent e) {
        if (!core.getLevelingConfig().enabled()) return;
        if (e.isCancelled()) return;
        core.getAsyncManager().executeAsync(() -> {
            PlayerStats stats = core.getLevelingManager().getPlayerStats(e.getPlayer().getUniqueId());
            if (stats == null) {
                return;
            }
            if (core.getLevelingConfig().require_link() && core.getDiscordSRV().getDiscordId(e.getPlayer().getUniqueId()) == null) return;
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
            int toAdd = Utils.nextInt(15, 25);
            boolean leveledUp = stats.setXP(stats.getXp() + toAdd, new MinecraftLevelupEvent(stats, e.getPlayer()));
            stats.addMessage(MessageType.MINECRAFT);
            if (leveledUp) {
                e.getPlayer().sendMessage(PlaceholdObjectList.ofArray(core, new PlaceholdObject(core, stats, "stats"), new PlaceholdObject(core, e.getPlayer(), "player")).apply(String.join("\n", core.getLevelingConfig().minecraft_levelup_message()), e.getPlayer()));
                core.getLevelingManager().getLevelingRewardsManager().rewardIfOnline(stats);
            }
        });

    }
}
