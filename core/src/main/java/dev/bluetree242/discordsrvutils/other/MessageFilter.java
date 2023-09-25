/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2023 BlueTree242
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

package dev.bluetree242.discordsrvutils.other;

import lombok.RequiredArgsConstructor;
import dev.bluetree242.discordsrvutils.DiscordSRVUtils;

import java.util.logging.Level;
import java.util.logging.LogRecord;

//fix the big messages that look like spam
@RequiredArgsConstructor
public class MessageFilter {
    private final DiscordSRVUtils core;

    public boolean canLog(String loggerName, LogRecord record) {
        String message = record.getMessage();
        Level level = record.getLevel();
        if (loggerName.startsWith("dev.bluetree242.discordsrvutils.dependencies.hikariCP.hikari")) {
            //Ignorable message
            if (!message.contains("Driver does not support get/set network timeout for connections."))
                log(level, message, "HikariCP");
            return false;
        }
        if (loggerName.startsWith("dev.bluetree242.discordsrvutils.dependencies.flywaydb")) {
            if (message.contains("failed") || message.contains("is up to date. No migration necessary.")) { //Only log when success or an error, other migration, or it look like spam or error
                if (!message.contains("No failed migration detected.")) //This message mean everything fine, why log it
                    log(level, message, "Flyway");
            }
            return false;
        }
        if (loggerName.contains("hsqldb.db") && level == Level.INFO) return false;
        if (loggerName.startsWith("dev.bluetree242.discordsrvutils.dependencies.jooq")) return false;
        return true;
    }

    //1 method so i can easily reformat the messages
    public void log(Level level, String msg, String prefix) {
        if (Level.INFO.equals(level)) {
            core.getLogger().info("[" + prefix + "] " + msg);
        } else if (Level.SEVERE.equals(level)) {
            core.getLogger().severe("[" + prefix + "] " + msg);
        } else {
            core.getLogger().warning("[" + prefix + "] " + msg);
        }
    }


}
