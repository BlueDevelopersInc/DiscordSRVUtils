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

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;

import java.lang.reflect.Field;

//fix the big messages that look like spam
@RequiredArgsConstructor
public class MessageFilter implements Filter {
    private final DiscordSRVUtils core;

    @Override
    public Result getOnMismatch() {
        return Result.NEUTRAL;
    }

    @Override
    public Result getOnMatch() {
        return Result.NEUTRAL;
    }

    public Result handle(String loggerName, Level level, String message) {
        if (!core.isEnabled()) return Result.NEUTRAL;
        if (loggerName.startsWith("tk.bluetree242.discordsrvutils.dependencies.hikariCP.hikari")) {
            //Ignorable message
            if (!message.contains("Driver does not support get/set network timeout for connections."))
                log(level, message, "HikariCP");
            return Result.DENY;
        }
        if (loggerName.startsWith("tk.bluetree242.discordsrvutils.dependencies.flywaydb")) {
            if (message.contains("failed") || message.contains("is up to date. No migration necessary.")) { //Only log when success or an error, other migration, or it look like spam or error
                if (!message.contains("No failed migration detected.")) //This message mean everything fine, why log it
                    log(level, message, "Flyway");
            }
            return Result.DENY;
        }
        if (loggerName.contains("tk.bluetree242.discordsrvutils.dependencies.hsqldb")) {
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
                        .getFormattedMessage());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object... parameters) {
        return handle(
                logger.getName(),
                level,
                message
        );
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object message, Throwable throwable) {
        return handle(
                logger.getName(),
                level,
                message.toString()
        );
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message message, Throwable throwable) {
        return handle(
                logger.getName(),
                level,
                message.getFormattedMessage()
        );
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

    public void add() {
        try {
            ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(this);
        } catch (Exception e) {
            core.logger.severe("Failed to add Message Filter");
            e.printStackTrace();
        }
    }

    public void remove() {
        try {
            org.apache.logging.log4j.core.Logger logger = ((org.apache.logging.log4j.core.Logger) org.apache.logging.log4j.LogManager.getRootLogger());

            Field configField = null;
            Class<?> targetClass = logger.getClass();

            while (targetClass != null) {
                try {
                    configField = targetClass.getDeclaredField("config");
                    break;
                } catch (NoSuchFieldException ignored) {}

                try {
                    configField = targetClass.getDeclaredField("privateConfig");
                    break;
                } catch (NoSuchFieldException ignored) {}

                targetClass = targetClass.getSuperclass();
            }

            if (configField != null) {
                if (!configField.isAccessible()) configField.setAccessible(true);

                Object config = configField.get(logger);
                Field configField2 = config.getClass().getDeclaredField("config");
                if (!configField2.isAccessible()) configField2.setAccessible(true);

                Object config2 = configField2.get(config);
                if (config2 instanceof org.apache.logging.log4j.core.filter.Filterable) {
                    ((org.apache.logging.log4j.core.filter.Filterable) config2).removeFilter(this);
                }
            }
        } catch (Throwable t) {
            core.logger.severe("Failed to remove Message Filter");
        }
    }

}
