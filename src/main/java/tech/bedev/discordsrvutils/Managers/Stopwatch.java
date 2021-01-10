package tech.bedev.discordsrvutils.managers;

public interface Stopwatch {

    void start();
    void stop();
    long getElapsedTime();
    long getElapsedTimeSecs();
}
