package tech.bedev.discordsrvutils.Managers;

import me.leoko.advancedban.manager.MessageManager;
import me.leoko.advancedban.manager.TimeManager;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

public class TimeFormatter {

    public String getDuration(Long ms) {
        Long remaining = ms;
        Long days = TimeUnit.MILLISECONDS.toDays(ms);
        remaining = remaining - TimeUnit.DAYS.toMillis(days);
        Long hours = TimeUnit.MILLISECONDS.toHours(remaining);
        remaining = remaining - TimeUnit.HOURS.toMillis(hours);
        Long minutes = TimeUnit.MILLISECONDS.toMinutes(remaining);
        remaining = remaining -TimeUnit.MINUTES.toMillis(minutes);
        Long seconds = TimeUnit.MILLISECONDS.toSeconds(remaining);
        return days + " Days, " + hours + " Hours, " + minutes + " Minutes, " + seconds + " Seconds";
    }
}
