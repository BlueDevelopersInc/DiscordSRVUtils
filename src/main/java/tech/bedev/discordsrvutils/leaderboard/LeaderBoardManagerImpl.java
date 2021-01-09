package tech.bedev.discordsrvutils.leaderboard;

import org.bukkit.Bukkit;
import tech.bedev.discordsrvutils.DiscordSRVUtils;
import tech.bedev.discordsrvutils.Managers.Stopwatch;
import tech.bedev.discordsrvutils.Managers.TimerManager;
import tech.bedev.discordsrvutils.Person.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class LeaderBoardManagerImpl implements LeaderBoardManager{
    public DiscordSRVUtils core;
    public LeaderBoardManagerImpl(DiscordSRVUtils core) {
        this.core = core;
    }
    @Override
    public List<Person> getLeaderBoardFromTo(int from, int to) {
        Stopwatch swtch = new TimerManager().getStopwatch();
        List<Person> mem = new ArrayList<>();
        try (Connection conn = core.getDatabaseFile()) {
            PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling ORDER BY Level DESC");
            ResultSet r1 = p1.executeQuery();
            int count = 0;
            while (r1.next()) {
                count++;
                if (count >= from && count <= to) {
                    Person p = core.getPersonByUUID(UUID.fromString(r1.getString("unique_id")));
                    if (p.isBukkitCached()) {
                        mem.add(p);

                    } else count--;

                } else { System.out.println(swtch.getElapsedTime() + "ms"); return mem;}
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return mem;
    }

    @Override
    public Person getPersonAtPosition(int position) {
        try (Connection conn = core.getDatabaseFile()) {
            int count = 0;
            PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling");
            ResultSet r1 = p1.executeQuery();
            while (r1.next()) {
                count++;
                if (count == position) return core.getPersonByUUID(Bukkit.getOfflinePlayer(r1.getString("unique_id")).getUniqueId());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }


}
