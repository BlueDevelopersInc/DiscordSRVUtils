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

package tk.bluetree242.discordsrvutils.waiter;

import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import tk.bluetree242.discordsrvutils.waiters.CreatePanelWaiter;

public abstract class Waiter {
    private final long startTime;
    private boolean expired = false;



    public Waiter() {
        startTime = System.currentTimeMillis();
        WaiterManager.get().getWaiters().add(this);
    }

    public final long getStartTime() {
        return startTime;
    }

    public abstract long getExpirationTime();

    public abstract String getWaiterName();

    public abstract void whenExpired();

    public final void expire() {
        expired = true;
        WaiterManager.get().getWaiters().remove(this);
        whenExpired();
    }

    public final void expire(boolean callExpired) {
        expired = true;
        WaiterManager.get().getWaiters().remove(this);
        if (callExpired)
            whenExpired();
    }

    public final boolean isExpired() {
        return expired;
    }
}