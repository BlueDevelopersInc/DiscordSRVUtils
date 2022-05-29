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

package tk.bluetree242.discordsrvutils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class AsyncManager {
    private final DiscordSRVUtils core;
    @Getter
    private ThreadPoolExecutor pool;

    private Thread newDSUThread(Runnable r) {
        //start new thread with name and handler
        Thread thread = new Thread(r);
        thread.setName("DSU-THREAD");
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler((t, e) -> core.getErrorHandler().defaultHandle(e));
        return thread;
    }

    public void start() {
        //initialize pool
        stop();
        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(core.getMainConfig().pool_size(), new ThreadFactory() {
            @Override
            public Thread newThread(@NotNull Runnable r) {

                return newDSUThread(r);
            }
        });
    }

    public void stop() {
        if (pool == null) return;
        pool.shutdown();
        pool = null;
    }

    public boolean isReady() {
        return pool != null && !pool.isShutdown();
    }

    public void executeAsync(Runnable r) {
        if (isReady()) pool.execute(r);
    }


}
