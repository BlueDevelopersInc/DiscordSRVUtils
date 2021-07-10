package tk.bluetree242.discordsrvutils.utils;

import org.bukkit.ChatColor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static String readFile(String path)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        String fileContents = new String(encoded, StandardCharsets.UTF_8);
        return fileContents;
    }

    public static String colors(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String getDuration(Long ms) {
        String val = "";
        Long remaining = ms;
        Long days = TimeUnit.MILLISECONDS.toDays(ms);
        remaining = remaining - TimeUnit.DAYS.toMillis(days);
        Long hours = TimeUnit.MILLISECONDS.toHours(remaining);
        remaining = remaining - TimeUnit.HOURS.toMillis(hours);
        Long minutes = TimeUnit.MILLISECONDS.toMinutes(remaining);
        remaining = remaining - TimeUnit.MINUTES.toMillis(minutes);
        Long seconds = TimeUnit.MILLISECONDS.toSeconds(remaining);
        if (days != 0) {
            val = days + " Days";
        }
        if (hours != 0) {
            if (val.equals("")) {
                val = hours + " hours";
            } else {
                val = val + ", " + hours + " hours";
            }
        }
        if (minutes != 0) {
            if (val.equals("")) {
                val = minutes + " minutes";
            } else {
                val = val + ", " + minutes + " minutes";
            }
        }
        if (seconds != 0) {
            if (val.equals("")) {
                val = seconds + " seconds";
            } else {
                val = val + ", " + seconds + " seconds";
            }
        }
        if (val.equals("")) {
            return "Less than a second";
        }
        return val;
    }

    public static String trim(String s) {
        if (s.length() >= 100) {
            int len = 0;
            String returnvalue = "";
            for (String l : s.split("")) {
                len++;
                if (len >= 97 && len <= 100 && len <= 100) {
                    returnvalue = returnvalue + ".";
                } else {
                    if (len <= 100)
                        returnvalue = returnvalue + l;
                }
            }
            return returnvalue;
        }
        return s;
    }

    public static String trim(String s, int limit) {
        if (s.length() >= 100) {
            int len = 0;
            String returnvalue = "";
            for (String l : s.split("")) {
                len++;
                if (len >= (limit - 3) && len <= limit && len <= limit) {
                    returnvalue = returnvalue + ".";
                } else {
                    if (len <= limit)
                        returnvalue = returnvalue + l;
                }
            }
            return returnvalue;
        }
        return s;
    }
}
