package tech.bedev.discordsrvutils.Managers;

public interface Stopwatch {

    void start();

    void stop();

    long getElapsedTime();

    long getElapsedTimeSecs();
}
