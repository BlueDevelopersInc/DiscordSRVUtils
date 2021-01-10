package tech.bedev.discordsrvutils.leaderboard;

import tech.bedev.discordsrvutils.Person.Person;

import java.util.List;


public interface LeaderBoardManager {

    List<Person> getLeaderBoardFromTo(int from, int to);

    Person getPersonAtPosition(int position);


}
