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

package dev.bluetree242.discordsrvutils.waiter;

import lombok.RequiredArgsConstructor;
import dev.bluetree242.discordsrvutils.DiscordSRVUtils;

import java.util.Set;
import java.util.TimerTask;

@RequiredArgsConstructor
public class WaiterCanceller extends TimerTask {
    private final DiscordSRVUtils core;

    @Override
    public void run() {
        try {
            Set<Waiter> waiters = core.getWaiterManager().getWaiters();
            for (Waiter waiter : waiters) {
                if (!waiter.isExpired()) {
                    if (waiter.getExpirationTime() <= System.currentTimeMillis()) {
                        waiter.expire();
                    }
                } else core.getWaiterManager().getWaiters().remove(waiter);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
