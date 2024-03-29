/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2024 BlueTree242
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

package dev.bluetree242.discordsrvutils.bukkit.status;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.systems.status.StatusListener;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class BukkitStatusListener extends StatusListener implements Listener, EventExecutor {
    private final DiscordSRVUtils core;

    public BukkitStatusListener(DiscordSRVUtils core) {
        super(core);
        this.core = core;
    }

    @Override
    public void register() {
        for (String event : core.getStatusConfig().update_events()) {
            try {
                Class eventClass = Class.forName(event);
                Bukkit.getPluginManager().registerEvent(eventClass, this, EventPriority.MONITOR, this, (Plugin) core.getPlatform().getOriginal(), false);
            } catch (ClassNotFoundException e) {
                core.severe("Event " + event + " not found");
            }
        }
        registered = true;
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) {
        if (event instanceof Cancellable) {
            if (((Cancellable) event).isCancelled()) return;
        }
        core.getAsyncManager().executeAsync(() -> {
            try {
                core.getStatusManager().editMessage(true);
            } catch (Throwable ex) {
                core.getLogger().severe("Failed to update status message.");
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void unregister() {
        HandlerList.unregisterAll(this);
        registered = false;
    }
}
