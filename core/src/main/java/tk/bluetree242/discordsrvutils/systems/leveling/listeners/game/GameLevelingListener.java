/*
 *  LICENSE
 *  DiscordSRVUtils
 *  -------------
 *  Copyright (C) 2020 - 2021 BlueTree242
 *  -------------
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program.  If not, see
 *  <http://www.gnu.org/licenses/gpl-3.0.html>.
 *  END
 */

package tk.bluetree242.discordsrvutils.systems.leveling.listeners.game;

import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.events.MinecraftLevelupEvent;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import tk.bluetree242.discordsrvutils.platform.events.PlatformChatEvent;
import tk.bluetree242.discordsrvutils.platform.events.PlatformJoinEvent;
import tk.bluetree242.discordsrvutils.platform.listener.PlatformListener;
import tk.bluetree242.discordsrvutils.systems.leveling.LevelingManager;
import tk.bluetree242.discordsrvutils.systems.leveling.MessageType;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GameLevelingListener extends PlatformListener {
    private DiscordSRVUtils core = DiscordSRVUtils.get();

    public void onJoin(PlatformJoinEvent e) {
        core.executeAsync(() -> {
            try (Connection conn = core.getDatabase()) {
                PreparedStatement p1 = conn.prepareStatement("SELECT * FROM leveling WHERE UUID=?");
                p1.setString(1, e.getPlayer().getUniqueId().toString());
                ResultSet r1 = p1.executeQuery();
                if (!r1.next()) {
                    PreparedStatement p2 = conn.prepareStatement("INSERT INTO leveling (UUID, Name, Level, XP) VALUES (?, ?, ?, ?)");
                    p2.setString(1, e.getPlayer().getUniqueId().toString());
                    p2.setString(2, e.getPlayer().getName());
                    p2.setInt(3, 0);
                    p2.setInt(4, 0);
                    p2.execute();
                } else {
                    if (!r1.getString("name").equals(e.getPlayer().getName())) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE leveling SET Name=? WHERE UUID=?");
                        p2.setString(1, e.getPlayer().getName());
                        p2.setString(2, e.getPlayer().getUniqueId().toString());
                        p2.execute();
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
        core.handleCF(LevelingManager.get().getPlayerStats(e.getPlayer().getUniqueId()), stats -> {
            if (stats == null) {
                return;
            }
            if (core.getLevelingConfig().antispam_messages()) {
                Long val = LevelingManager.get().antispamMap.get(stats.getUuid());
                if (val == null) {
                    LevelingManager.get().antispamMap.put(stats.getUuid(), System.nanoTime());
                } else {
                    if (!(System.nanoTime() - val >= LevelingManager.get().MAP_EXPIRATION_NANOS)) return;
                    LevelingManager.get().antispamMap.remove(stats.getUuid());
                    LevelingManager.get().antispamMap.put(stats.getUuid(), System.nanoTime());
                }
            }
            int toAdd = new SecureRandom().nextInt(50);
            boolean leveledUp = core.handleCFOnAnother(stats.setXP(stats.getXp() + toAdd, new MinecraftLevelupEvent(stats, e.getPlayer())));
            core.handleCFOnAnother(stats.addMessage(MessageType.MINECRAFT));
            if (leveledUp) {
                e.getPlayer().sendMessage(PlaceholdObjectList.ofArray(new PlaceholdObject(stats, "stats"), new PlaceholdObject(e.getPlayer(), "player")).apply(String.join("\n", core.getLevelingConfig().minecraft_levelup_message()), e.getPlayer()));
            }
        }, null);
    }
}
