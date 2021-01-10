package tech.bedev.discordsrvutils.leaderboard;

import tech.bedev.discordsrvutils.DiscordSRVUtils;
import tech.bedev.discordsrvutils.managers.Stopwatch;
import tech.bedev.discordsrvutils.managers.TimerManager;
import tech.bedev.discordsrvutils.person.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LeaderboardManagerImpl implements LeaderboardManager
{
	public DiscordSRVUtils core;

	public LeaderboardManagerImpl(DiscordSRVUtils core)
	{
		this.core = core;
	}

	@Override
	public List<Person> getLeaderBoardFromTo(int from, int to)
	{
		Stopwatch stopwatch = new TimerManager().getStopwatch();
		List<Person> people = new ArrayList<>();
		try(Connection conn = core.getDatabaseFile())
		{
			PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_leveling ORDER BY Level DESC");
			ResultSet r1 = p1.executeQuery();
			int count = 0;
			while(r1.next())
			{
				count++;
				if(count >= from && count <= to)
				{
					Person p = core.getPersonByUUID(UUID.fromString(r1.getString("unique_id")));
					if(p.isBukkitCached())
					{
						people.add(p);

					}
					else count--;

				}
				else
				{
					System.out.println(stopwatch.getElapsedTime() + "ms");
					return people;
				}
			}
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
		}
		return people;
	}


}
