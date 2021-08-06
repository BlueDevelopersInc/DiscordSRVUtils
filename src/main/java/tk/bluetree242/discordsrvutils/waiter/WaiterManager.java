package tk.bluetree242.discordsrvutils.waiter;


import tk.bluetree242.discordsrvutils.DiscordSRVUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

public class WaiterManager {

    private static WaiterManager main;
    private final DiscordSRVUtils core = DiscordSRVUtils.get();
    private final Set<Waiter> waiters = ConcurrentHashMap.newKeySet();
    public Timer timer = new Timer();

    public WaiterManager() {
        main = this;
        timer.schedule(new WaiterCanceller(), 0, 1000);
    }

    public static WaiterManager get() {
        return main;
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
