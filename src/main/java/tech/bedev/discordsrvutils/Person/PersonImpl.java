package tech.bedev.discordsrvutils.Person;


import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import org.bukkit.Bukkit;
import tech.bedev.discordsrvutils.DiscordSRVUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PersonImpl implements Person {

    private final UUID uuid;
    private final Member DiscordUser;
    private DiscordSRVUtils core;
    private int level = -1;
    private int xp = -1;
    private Long minecraftmsges = -1L;
    private Long discordmsges = -1L;

    public PersonImpl(UUID uuid, Member DiscordUser, DiscordSRVUtils core) {
        this.DiscordUser = DiscordUser;
        this.core = core;
        if (uuid != null) {
            UUID uuid2 = Bukkit.getOfflinePlayer(uuid).getUniqueId();
            this.uuid = uuid;
        } else {
            this.uuid = null;
        }
        insertLeveling();
        reloadCache();

    }
    @Override
    public void setLevel(int level) {
            insertLeveling();
            try (Connection conn = core.getDatabaseFile()) {
                if (uuid != null) {
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
                    p1.setString(1, uuid.toString());
                    p1.execute();
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET level=? WHERE unique_id=?");
                        p2.setInt(1, level);
                        p2.setString(2, uuid.toString());
                        p2.execute();
                        this.level = level;
                    }
                } else {
                    if (DiscordUser == null) return;
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE userID=?");
                    p1.setLong(1, DiscordUser.getIdLong());
                    p1.execute();
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET level=? WHERE userID=?");
                        p2.setInt(1, level);
                        p2.setLong(2, DiscordUser.getIdLong());
                        p2.execute();
                        this.level = level;
                    }

                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }

    }

    @Override
    public void addLevels(int levels) {
            insertLeveling();
            try (Connection conn = core.getDatabaseFile()) {
                if (uuid != null) {
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
                    p1.setString(1, uuid.toString());
                    p1.execute();
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET level=? WHERE unique_id=?");
                        p2.setInt(1, r1.getInt("level") + levels);
                        p2.setString(2, uuid.toString());
                        p2.execute();
                        this.level = r1.getInt("level") + levels;
                    }
                } else {
                    if (DiscordUser == null) return;
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE userID=?");
                    p1.setLong(1, DiscordUser.getIdLong());
                    p1.execute();
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET level=? WHERE userID=?");
                        p2.setInt(1, r1.getInt("level") + levels);
                        p2.setLong(2, DiscordUser.getIdLong());
                        p2.execute();
                        this.level = r1.getInt("level") + levels;
                    }

                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
    }

    @Override
    public void removeLevels(int levels) {
            insertLeveling();
            try (Connection conn = core.getDatabaseFile()) {
                if (uuid != null) {
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
                    p1.setString(1, uuid.toString());
                    p1.execute();
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET level=? WHERE unique_id=?");
                        p2.setInt(1, r1.getInt("level") - levels);
                        p2.setString(2, uuid.toString());
                        p2.execute();
                        this.level = r1.getInt("level") - levels;
                    }
                } else {
                    if (DiscordUser == null) return;
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE userID=?");
                    p1.setLong(1, DiscordUser.getIdLong());
                    p1.execute();
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET level=? WHERE userID=?");
                        p2.setInt(1, r1.getInt("level") - levels);
                        p2.setLong(2, DiscordUser.getIdLong());
                        p2.execute();
                        this.level = r1.getInt("level") - levels;
                    }

                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }


    }

    @Override
    public void clearLevels() {
            insertLeveling();
            try (Connection conn = core.getDatabaseFile()) {
                if (uuid != null) {
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
                    p1.setString(1, uuid.toString());
                    p1.execute();
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET level=0 WHERE unique_id=?");
                        p2.setString(1, uuid.toString());
                        p2.execute();
                        this.level = 0;
                    }
                } else {
                    if (DiscordUser == null) return;
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE userID=?");
                    p1.setLong(1, DiscordUser.getIdLong());
                    p1.execute();
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET level=0 WHERE userID=?");
                        p2.setLong(1, DiscordUser.getIdLong());
                        p2.execute();
                        this.level = 0;
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }


    }

    @Override
    public void setXP(int xp) {
            insertLeveling();
            try (Connection conn = core.getDatabaseFile()) {
                if (uuid != null) {
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
                    p1.setString(1, uuid.toString());
                    p1.execute();
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET XP=? WHERE unique_id=?");
                        p2.setInt(1, xp);
                        p2.setString(2, uuid.toString());
                        p2.execute();
                        this.xp = xp;
                    }
                } else {
                    if (DiscordUser == null) return;
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE userID=?");
                    p1.setLong(1, DiscordUser.getIdLong());
                    p1.execute();
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET XP=? WHERE userID=?");
                        p2.setInt(2, xp);
                        p2.setLong(2, DiscordUser.getIdLong());
                        p2.execute();
                        this.xp = xp;
                    }

                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }


    }

    @Override
    public void addXP(int xp) {
            insertLeveling();
            try (Connection conn = core.getDatabaseFile()) {
                if (uuid != null) {
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
                    p1.setString(1, uuid.toString());
                    p1.execute();
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET XP=? WHERE unique_id=?");
                        p2.setInt(1, r1.getInt("XP") + xp);
                        p2.setString(2, uuid.toString());
                        p2.execute();
                        this.xp = r1.getInt("XP") + xp;
                    }
                } else {
                    if (DiscordUser == null) return;
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE userID=?");
                    p1.setLong(1, DiscordUser.getIdLong());
                    p1.execute();
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET XP=? WHERE userID=?");
                        p2.setInt(1, r1.getInt("XP") + xp);
                        p2.setLong(2, DiscordUser.getIdLong());
                        p2.execute();
                        this.xp = r1.getInt("XP") + xp;
                    }

                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }

    }

    @Override
    public void removeXP(int xp) {
            insertLeveling();
            try (Connection conn = core.getDatabaseFile()) {
                if (uuid != null) {
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
                    p1.setString(1, uuid.toString());
                    p1.execute();
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET XP=? WHERE unique_id=?");
                        p2.setInt(1, r1.getInt("XP") - xp);
                        p2.setString(2, uuid.toString());
                        p2.execute();
                        this.xp = r1.getInt("XP") - xp;
                    }
                } else {
                    if (DiscordUser == null) return;
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE userID=?");
                    p1.setLong(1, DiscordUser.getIdLong());
                    p1.execute();
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET XP=? WHERE userID=?");
                        p2.setInt(1, r1.getInt("XP") - xp);
                        p2.setLong(2, DiscordUser.getIdLong());
                        p2.execute();
                        this.xp = r1.getInt("XP") - xp;
                    }

                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }

    }

    @Override
    public void clearXP() {
            insertLeveling();
            try (Connection conn = core.getDatabaseFile()) {
                if (uuid != null) {
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
                    p1.setString(1, uuid.toString());
                    p1.execute();
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET XP=0 WHERE unique_id=?");
                        p2.setString(1, uuid.toString());
                        p2.execute();
                        this.xp = 0;
                    }
                } else {
                    if (DiscordUser == null) return;
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE userID=?");
                    p1.setLong(1, DiscordUser.getIdLong());
                    p1.execute();
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET XP=0 WHERE userID=?");
                        p2.setLong(1, DiscordUser.getIdLong());
                        p2.execute();
                        this.xp = 0;
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }


    }

    @Override
    public void unLink() {
        if (uuid != null) {
            DiscordSRV.getPlugin().getAccountLinkManager().unlink(uuid);
        } else {
            DiscordSRV.getPlugin().getAccountLinkManager().unlink(DiscordUser.getId());
        }
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getXP() {
        return xp;
    }

    @Override
    public String getRank() {
            insertLeveling();
            if (uuid != null) {
                try (Connection conn = core.getDatabaseFile()) {
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling ORDER BY Level DESC");
                    ResultSet r1 = p1.executeQuery();
                    int rank = 0;
                    while (r1.next()) {
                        rank++;
                            if (r1.getString("unique_id").equals(uuid.toString())) {
                                return Integer.toString(rank);
                            }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        return "Unknown";
    }

        @Override
        public boolean isLinked() {
        if (!isBukkitCached()) return false;
            int count = 0;
            if (uuid != null) count++;
            if (DiscordUser != null) count++;
            if (count == 2) return true;
            return false;
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

        @Override
        public void insertLeveling() {
                try (Connection conn = core.getDatabaseFile()) {
                    if (uuid != null) {
                        PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
                        p1.setString(1, uuid.toString());
                        p1.execute();
                        ResultSet r1 = p1.executeQuery();
                        if (!r1.next()) {
                            if (DiscordUser == null) {
                                PreparedStatement p2 = conn.prepareStatement("INSERT INTO discordsrvutils_leveling (unique_id, XP, level) VALUES (?, 0, 0)");
                                p2.setString(1, uuid.toString());
                                p2.execute();
                            } else {
                                PreparedStatement p2 = conn.prepareStatement("INSERT INTO discordsrvutils_leveling (unique_id, XP, level, userID) VALUES (?, 0, 0, ?)");
                                p2.setString(1, uuid.toString());
                                p2.setLong(2, DiscordUser.getIdLong());
                                p2.execute();

                            }
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
        }

    @Override
    public Long getDiscordMessages() {
        return discordmsges;
    }

    @Override
    public Long getMinecraftMessages() {
        return discordmsges;
    }

    @Override
    public Long getTotalMessages() {
        return discordmsges + minecraftmsges;
    }

    @Override
    public void addMessages(MessageType msg, int number) {
        if (uuid == null) return;
        insertLeveling();
        try (Connection conn = core.getDatabaseFile()) {
            switch (msg) {
                case Discord:
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
                    p1.setString(1, uuid.toString());
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET DiscordMessages=? WHERE unique_id=?");
                        p2.setLong(1, r1.getLong("DiscordMessages") + number);
                        p2.setString(2, uuid.toString());
                        p2.execute();
                        discordmsges = r1.getLong("DiscordMessages") + number;
                    }
                    break;
                case Minecraft:
                    PreparedStatement p2 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
                    p2.setString(1, uuid.toString());
                    ResultSet r2 = p2.executeQuery();
                    if (r2.next()) {
                        PreparedStatement p3 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET MinecraftMessages=? WHERE unique_id=?");
                        p3.setLong(1, r2.getLong("MinecraftMessages") + number);
                        p3.setString(2, uuid.toString());
                        p3.execute();
                        minecraftmsges = r2.getLong("MinecraftMessages") + number;
                    }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void removeMessages(MessageType msg, int number) {
        if (uuid == null) return;
        insertLeveling();
        try (Connection conn = core.getDatabaseFile()) {
            switch (msg) {
                case Discord:
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
                    p1.setString(1, uuid.toString());
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET DiscordMessages=? WHERE unique_id=?");
                        p2.setLong(1, r1.getLong("DiscordMessages") - number);
                        p2.setString(2, uuid.toString());
                        p2.execute();
                        discordmsges = r1.getLong("DiscordMessages") - number;
                    }
                    break;
                case Minecraft:
                    PreparedStatement p2 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
                    p2.setString(1, uuid.toString());
                    ResultSet r2 = p2.executeQuery();
                    if (r2.next()) {
                        PreparedStatement p3 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET MinecraftMessages=? WHERE unique_id=?");
                        p3.setLong(1, r2.getLong("MinecraftMessages") - number);
                        p3.setString(2, uuid.toString());
                        p3.execute();
                        minecraftmsges = r2.getLong("DiscordMessages") - number;
                    }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void setMessages(MessageType msg, int number) {
        if (uuid == null) return;
        insertLeveling();
        try (Connection conn = core.getDatabaseFile()) {
            switch (msg) {
                case Discord:
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
                    p1.setString(1, uuid.toString());
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {
                        PreparedStatement p2 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET DiscordMessages=? WHERE unique_id=?");
                        p2.setLong(1, number);
                        p2.setString(2, uuid.toString());
                        p2.execute();
                        discordmsges = Long.parseLong(number + "");
                    }
                    break;
                case Minecraft:
                    PreparedStatement p2 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
                    p2.setString(1, uuid.toString());
                    ResultSet r2 = p2.executeQuery();
                    if (r2.next()) {
                        PreparedStatement p3 = conn.prepareStatement("UPDATE discordsrvutils_leveling SET MinecraftMessages=? WHERE unique_id=?");
                        p3.setLong(1, number);
                        p3.setString(2, uuid.toString());
                        p3.execute();
                        minecraftmsges = Long.parseLong(number + "");
                    }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isBukkitCached() {
        if (uuid == null) return false;
        if (Bukkit.getOfflinePlayer(uuid).getName() == null) return false;
        return true;
    }

    @Override
    public void reloadCache() {
        if (uuid == null) return;
        try (Connection conn = core.getDatabaseFile()) {
            PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling WHERE unique_id=?");
            p1.setString(1, uuid.toString());
            ResultSet r1 = p1.executeQuery();
            if (r1.next()) {
                level = r1.getInt("Level");
                xp = r1.getInt("XP");
                minecraftmsges = r1.getLong("MinecraftMessages");
                discordmsges = r1.getLong("DiscordMessages");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
