package tech.bedev.discordsrvutils.Managers;

public class  TimerManager {

    public Stopwatch getStopwatch() {
        return new StopWatchImpl();
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }
    public int getCurrentTimeApproximated() {
        return Math.round(System.currentTimeMillis());
    }

    public Long getMilliesAfterSeconds(int Seconds) {
        String Mins = Seconds + "000";
        return System.currentTimeMillis() + Integer.parseInt(Mins);
    }

    public TimeFormatter getTimeFormatter() {
        return new TimeFormatter();
    }
}
