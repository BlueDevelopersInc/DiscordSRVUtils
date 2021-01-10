package tech.bedev.discordsrvutils.leaderboard;

import tech.bedev.discordsrvutils.person.Person;

import java.util.List;


public interface LeaderboardManager
{
	List<Person> getLeaderBoardFromTo(int from, int to);
}
