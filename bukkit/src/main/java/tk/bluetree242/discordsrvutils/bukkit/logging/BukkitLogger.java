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

package tk.bluetree242.discordsrvutils.bukkit.logging;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.LegacyAbstractLogger;
import tk.bluetree242.discordsrvutils.bukkit.DiscordSRVUtilsBukkit;

import java.util.logging.LogRecord;

@RequiredArgsConstructor
public class BukkitLogger extends LegacyAbstractLogger {
    private final String name;

    @Override
    public String getName() {
        return name;
    }

    private DiscordSRVUtilsBukkit getMain() {
        return (DiscordSRVUtilsBukkit) Bukkit.getPluginManager().getPlugin("DiscordSRVUtils");
    }

    @Override
    protected String getFullyQualifiedCallerName() {
        return BukkitLogger.class.getName();
    }

    @Override
    protected void handleNormalizedLoggingCall(Level level, Marker marker, String msg, Object[] arguments, Throwable throwable) {
        DiscordSRVUtilsBukkit main = getMain();
        if (main == null) return;
        if (arguments != null) {
            int num = 0;
            for (Object argument : arguments) {
                num++;
                if (num > arguments.length) continue;
                msg = msg.replaceFirst("\\{}", argument.toString());
            }
        }
        LogRecord record = new LogRecord(toJUtilLevel(level), msg);
        record.setLoggerName(main.getLogger().getName());
        record.setParameters(arguments);
        record.setThrown(throwable);
        if (!main.getCore().getMessageFilter().canLog(name, record)) return;
        main.getLogger().log(record);
    }

    private java.util.logging.Level toJUtilLevel(Level level) {
        switch (level) {
            case WARN:
                return java.util.logging.Level.WARNING;
            case ERROR:
                return java.util.logging.Level.SEVERE;
            default:
                return java.util.logging.Level.INFO;
        }
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }
}
