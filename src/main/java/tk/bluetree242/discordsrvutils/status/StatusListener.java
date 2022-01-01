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

package tk.bluetree242.discordsrvutils.status;

import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;

public class StatusListener implements Listener, EventExecutor {
    private static StatusListener main;
    public boolean registered = false;
    private DiscordSRVUtils core = DiscordSRVUtils.get();

    public StatusListener() {

        main = this;
    }

    public static StatusListener get() {
        return main;
    }

    public void register() {
        for (String event : core.getStatusConfig().update_events()) {
            try {
                Class eventClass = Class.forName(event);
                Bukkit.getPluginManager().registerEvent(eventClass, this, EventPriority.MONITOR, this, core.getBukkitMain(), false);
            } catch (ClassNotFoundException e) {
                core.severe("Event " + event + " Not Found");
            }
        }
        registered = true;
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {
        if (event instanceof Cancellable) {
            if (((Cancellable) event).isCancelled()) return;
        }
        Bukkit.getServer().getScheduler().runTaskLater(core.getBukkitMain(), () -> {
            StatusManager.get().editMessage(true);
        }, 1);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
        registered = false;
    }
}
