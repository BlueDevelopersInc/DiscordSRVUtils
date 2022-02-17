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
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;
import java.util.function.Consumer;

public class AsyncManager {
    private final DiscordSRVUtils core = DiscordSRVUtils.get();
    @Getter
    private ThreadPoolExecutor pool;

    private Thread newDSUThread(Runnable r) {
        //start new thread with name and handler
        Thread thread = new Thread(r);
        thread.setName("DSU-THREAD");
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler((t, e) -> core.defaultHandle(e));
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

    /**
     * For doing a cf inside another one
     */
    public <U> U handleCFOnAnother(CompletableFuture<U> cf) {
        try {
            return cf.get();
        } catch (ExecutionException | InterruptedException ex) {
            Exception e = ex;
            while (ex instanceof ExecutionException) e = (Exception) ex.getCause();
            throw (RuntimeException) e;
        }
    }

    public <U> void handleCF(CompletableFuture<U> cf, Consumer<U> success, Consumer<Throwable> failure) {
        if (success != null) cf.thenAcceptAsync(success);
        cf.handle((e, x) -> {
            Exception ex = (Exception) x.getCause();
            while (ex instanceof ExecutionException) ex = (Exception) ex.getCause();
            if (failure != null) {
                failure.accept(ex);
            } else core.getErrorHandler().defaultHandle(ex);
            return x;
        });
    }


}
