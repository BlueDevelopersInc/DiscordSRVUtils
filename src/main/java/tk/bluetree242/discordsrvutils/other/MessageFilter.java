/*
 *  LICENSE
 *  DiscordSRVUtils
 *  -------------
 *  Copyright (C) 2020 - 2021 BlueTree242
 *  -------------
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program.  If not, see
 *  <http://www.gnu.org/licenses/gpl-3.0.html>.
 *  END
 */

package tk.bluetree242.discordsrvutils.other;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;

//fix the big messages that look like spam
public class MessageFilter implements Filter {
    private final DiscordSRVUtils core = DiscordSRVUtils.get();

    @Override
    public Result getOnMismatch() {
        return Result.NEUTRAL;
    }

    @Override
    public Result getOnMatch() {
        return Result.NEUTRAL;
    }

    public Result handle(String loggerName, Level level, String message, Throwable throwable) {
        if (!core.isEnabled()) return Result.NEUTRAL;
        if (loggerName.startsWith("tk.bluetree242.discordsrvutils.dependencies.hikariCP.hikari")) {
            log(level, message, "HikariCP");
            return Result.DENY;
        }
        if (loggerName.startsWith("tk.bluetree242.discordsrvutils.dependencies.flywaydb")) {
            if (message.contains("failed") || message.contains("is up to date. No migration necessary.")) {
                if (!message.contains("No failed migration detected."))
                    log(level, message, "Flyway");
            }
            return Result.DENY;
        }
        if (loggerName.startsWith("hsqldb.db.HSQLDB7C892AA07F.ENGINE")) {
            log(level, message, "Hsqldb");
            return Result.DENY;
        }
        return Result.NEUTRAL;
    }

    //Redirect to 1 method so i don't recode
    @Override
    public Result filter(LogEvent logEvent) {
        return handle(
                logEvent.getLoggerName(),
                logEvent.getLevel(),
                logEvent.getMessage()
                        .getFormattedMessage(),
                logEvent.getThrown());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object... parameters) {
        return handle(
                logger.getName(),
                level,
                message,
                null);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object message, Throwable throwable) {
        return handle(
                logger.getName(),
                level,
                message.toString(),
                throwable);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message message, Throwable throwable) {
        return handle(
                logger.getName(),
                level,
                message.getFormattedMessage(),
                throwable);
    }

    //1 method so i can easily reformat the messages
    public void log(Level level, String msg, String prefix) {
        switch (level.name()) {
            case "INFO":
                core.getLogger().info("[" + prefix + "] " + msg);
                break;
            case "WARN":
                core.getLogger().warning("[" + prefix + "] " + msg);
                break;
            case "ERROR":
                core.getLogger().severe("[" + prefix + "] " + msg);
                break;
            default:
                core.getLogger().info("[" + prefix + "] " + msg);
        }
    }

}
