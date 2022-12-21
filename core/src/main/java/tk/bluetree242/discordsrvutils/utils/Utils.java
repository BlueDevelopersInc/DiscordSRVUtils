/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2022 BlueTree242
 * -------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * END
 */

package tk.bluetree242.discordsrvutils.utils;

import com.vdurmont.emoji.EmojiParser;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Emote;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.exceptions.ErrorResponseException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static String readFile(String path)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    public static String readFile(File file)
            throws IOException {
        byte[] encoded = Files.readAllBytes(file.getAbsoluteFile().toPath());
        return new String(encoded, StandardCharsets.UTF_8);
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
        return s.equalsIgnoreCase("true");
    }

    public static String getDBoolean(Boolean s) {
        if (s) return "true";
        return "false";
    }

    public static String parsePos(int num) {
        if (num == 1) return "1st";
        if (num == 2) return "2nd";
        if (num == 3) return "3rd";
        return num + "th";
    }

    public static String getDuration(Long ms) {
        String val = "";
        Long remaining = ms;
        long days = TimeUnit.MILLISECONDS.toDays(ms);
        remaining = remaining - TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(remaining);
        remaining = remaining - TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(remaining);
        remaining = remaining - TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(remaining);
        if (days != 0) {
            val = days + " Day" + (days <= 1 ? "" : "s");
        }
        if (hours != 0) {
            if (val.equals("")) {
                val = hours + " hour" + (hours <= 1 ? "" : "s");
            } else {
                val = val + ", " + hours + " hour" + (hours <= 1 ? "" : "s");
            }
        }
        if (minutes != 0) {
            if (val.equals("")) {
                val = minutes + " minute" + (minutes <= 1 ? "" : "s");
            } else {
                val = val + ", " + minutes + " minute" + (minutes <= 1 ? "" : "s");
            }
        }
        if (seconds != 0) {
            if (val.equals("")) {
                val = seconds + " second" + (seconds <= 1 ? "" : "s");
            } else {
                val = val + ", " + seconds + " second" + (seconds <= 1 ? "" : "s");
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
                if (len >= 97 && len <= 100) {
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
            StringBuilder returnvalue = new StringBuilder();
            for (String l : s.split("")) {
                len++;
                if (len >= (limit - 3) && len <= limit && len <= limit) {
                    returnvalue.append(".");
                } else {
                    if (len <= limit)
                        returnvalue.append(l);
                }
            }
            return returnvalue.toString();
        }
        return s;
    }

    public static String b64Encode(@NotNull String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    public static String b64Encode(@NotNull byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Contract("_ -> new")
    public static @NotNull
    String b64Decode(String text) {
        return new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
    }


    public static @NotNull
    String parseArgs(String[] args, int start, int end) {
        String argss = "";
        for (int i = start; i < args.length; i++) {
            if (i <= end) {
                argss = argss + args[i] + " ";
            }
        }
        return argss.replaceAll("\\s+$", "");
    }

    public static @NotNull
    String parseArgs(String @NotNull [] args, int start) {
        String argss = "";
        for (int i = start; i < args.length; i++) {
            argss = argss + args[i] + " ";
        }
        return argss.replaceAll("\\s+$", "");
    }

    public static Emoji getEmoji(String val, Emoji def) {
        List<Emote> emotes = DiscordSRVUtils.get().getPlatform().getDiscordSRV().getMainGuild().getEmotesByName(val, true);
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

    public static String exceptionToStackTrack(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static String colors(String s) {
        return DiscordSRVUtils.get().getServer().colors(s);
    }

    public static Member retrieveMember(Guild guild, long id) {
        try {
            return guild.retrieveMemberById(id).complete();
        } catch (ErrorResponseException e) {
            return null;
        }
    }

    public static int nextInt(int min, int max) {
        return new SecureRandom().nextInt(max - min) + min;
    }
}
