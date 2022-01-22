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

package tk.bluetree242.discordsrvutils.leveling;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.events.LevelupEvent;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerStats {
    private final DiscordSRVUtils core = DiscordSRVUtils.get();
    private final UUID uuid;
    private final String name;
    private final int minecraftMessages;
    private final int discordMessages;
    private final int rank;
    private int level;
    private int xp;

    public PlayerStats(UUID uuid, String name, int level, int xp, int minecraftMessages, int discordMessages, int rank) {
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

    public int getXp() {
        return xp;
    }

    public CompletableFuture<Void> setLevel(int level) {
        return core.completableFutureRun(() -> {
            try (Connection conn = core.getDatabase()) {
                PreparedStatement p1 = conn.prepareStatement("UPDATE leveling SET Level=? WHERE UUID=?");
                p1.setInt(1, level);
                p1.setString(2, uuid.toString());
                p1.execute();
                this.level = level;
            } catch (SQLException e) {
                throw new UnCheckedSQLException(e);
            }
        });
    }

    public CompletableFuture<Boolean> setXP(int xp) {
        return setXP(xp, null);
    }

    /**
     * @param xp XP to add
     * @return true if player leveled up, false if not
     */
    public CompletableFuture<Boolean> setXP(int xp, LevelupEvent event) {
        if (event == null) {
            event = new LevelupEvent(this, uuid);
        }
        LevelupEvent finalEvent = event;
        return core.completableFuture(() -> {
            try (Connection conn = core.getDatabase()) {
                if (xp >= 300) {
                    PreparedStatement p1 = conn.prepareStatement("UPDATE leveling SET XP=0, Level=? WHERE UUID=?");
                    p1.setInt(1, level + 1);
                    p1.setString(2, uuid.toString());
                    p1.execute();
                    this.level = level + 1;
                    this.xp = 0;
                    String id = DiscordSRVUtils.getDiscordSRV().getDiscordId(uuid);
                    if (id == null) return true;
                    LevelingManager manager = LevelingManager.get();
                    Member member = core.getGuild().retrieveMemberById(id).complete();
                    if (member == null) return true;
                    Collection actions = new ArrayList<>();
                    for (Role role : manager.getRolesToRemove(level)) {
                        if (member.getRoles().contains(role))
                            actions.add(core.getGuild().removeRoleFromMember(member, role).reason("User Leveled Up"));
                    }
                    Role toAdd = manager.getRoleForLevel(level);
                    if (toAdd != null) {
                        actions.add(core.getGuild().addRoleToMember(member, toAdd).reason("User Leveled Up"));
                    }
                    if (!actions.isEmpty())
                        RestAction.allOf(actions).queue();
                    DiscordSRV.api.callEvent(finalEvent);
                    return true;
                }
                PreparedStatement p1 = conn.prepareStatement("UPDATE leveling SET XP=? WHERE UUID=?");
                p1.setInt(1, xp);
                p1.setString(2, uuid.toString());
                p1.execute();
                this.xp = xp;
                return false;

            } catch (SQLException ex) {
                throw new UnCheckedSQLException(ex);
            }
        });
    }

    public int getMinecraftMessages() {
        return minecraftMessages;
    }

    public int getDiscordMessages() {
        return discordMessages;
    }

    public CompletableFuture<Void> addMessage(MessageType type) {
        return core.completableFutureRun(() -> {
            try (Connection conn = core.getDatabase()) {
                PreparedStatement p1 = null;
                switch (type) {
                    case DISCORD:
                        p1 = conn.prepareStatement("UPDATE leveling SET DiscordMessages=? WHERE UUID=?");
                        p1.setInt(1, discordMessages + 1);
                        break;
                    case MINECRAFT:
                        p1 = conn.prepareStatement("UPDATE leveling SET MinecraftMessages=? WHERE UUID=?");
                        p1.setInt(1, minecraftMessages + 1);
                        break;
                }
                p1.setString(2, uuid.toString());
                p1.execute();
            } catch (SQLException e) {
                throw new UnCheckedSQLException(e);
            }
        });
    }

    public int getRank() {
        return rank;
    }
}
