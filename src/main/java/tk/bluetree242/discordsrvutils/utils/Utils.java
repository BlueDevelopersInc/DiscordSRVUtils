package tk.bluetree242.discordsrvutils.utils;

import com.vdurmont.emoji.EmojiParser;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Emote;
import org.bukkit.ChatColor;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static String readFile(String path)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        String fileContents = new String(encoded, StandardCharsets.UTF_8);
        return fileContents;
    }

    public static String readFile(File file)
            throws IOException {
        byte[] encoded = Files.readAllBytes(file.getAbsoluteFile().toPath());
        String fileContents = new String(encoded, StandardCharsets.UTF_8);
        return fileContents;
    }

    public static boolean isLong(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean getDBoolean(String s) {
        if (s.equalsIgnoreCase("true")) return true;
        return false;
    }

    public static String parsePos(int num) {
        if  (num == 1) return "1st";
        if (num == 2) return "2nd";
        if (num == 3) return "3rd";
        return num + "th";
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

    public static String b64Encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes());
    }

    public static String b64Decode(String text) {
        return new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
    }


    public static String parseArgs(String[] args, int start, int end) {
        String argss = "";
        for (int i = start; i < args.length; i++) {
            if (i <= end) {
                argss = argss + args[i] + " ";
            }
        }
        return argss.replaceAll("\\s+$", "");
    }

    public static String parseArgs(String[] args, int start) {
        String argss = "";
        for (int i = start; i < args.length; i++) {
            argss = argss + args[i] + " ";
        }
        return argss.replaceAll("\\s+$", "");
    }

    public static Emoji getEmoji(String val, Emoji def) {
        List<Emote> emotes = DiscordSRVUtils.get().getGuild().getEmotesByName(val, true);
        Emote emote;
        if (!emotes.isEmpty()) {
            emote = emotes.get(0);
        } else emote = null;
        if (emote == null) {
                String unicode = EmojiParser.parseToUnicode(":" + val + ":");
            if (unicode.equals(val)) {
                return def;
            } else return new Emoji(unicode);

        } else return new Emoji(emote.getIdLong(), emote.getName(), emote.isAnimated());
    }
}
