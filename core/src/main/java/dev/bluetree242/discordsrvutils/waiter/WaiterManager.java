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

package dev.bluetree242.discordsrvutils.waiter;


import dev.bluetree242.discordsrvutils.DiscordSRVUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

public class WaiterManager {

    private static WaiterManager main;
    private final DiscordSRVUtils core;
    private final Set<Waiter> waiters = ConcurrentHashMap.newKeySet();
    public Timer timer = new Timer();

    public WaiterManager(DiscordSRVUtils core) {
        this.core = core;
        timer.schedule(new WaiterCanceller(core), 0, 1000);
    }


    public Set<Waiter> getWaiters() {
        return waiters;
    }

    public Set<Waiter> getWaiterByName(String name) {
        Set<Waiter> ret = new HashSet<>();
        for (Waiter waiter : waiters) {
            if (waiter.getWaiterName().equals(name)) ret.add(waiter);
        }
        return ret;
    }
}
