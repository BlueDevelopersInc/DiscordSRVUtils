package tech.bedev.discordsrvutils.leaderboard;

import tech.bedev.discordsrvutils.Person.Person;

import java.util.Map;

public interface LeaderBoardManager {

    Map<Integer, Person> getLeaderBoardFromTo(int from, int to);

    Person getPersonAtPosition(int position);

    Map<Integer, Person> getPage(int page);

}
