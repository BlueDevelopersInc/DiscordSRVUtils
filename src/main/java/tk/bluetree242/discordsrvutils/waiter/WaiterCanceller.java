package tk.bluetree242.discordsrvutils.waiter;

import java.util.Set;
import java.util.TimerTask;

public class WaiterCanceller extends TimerTask {
    @Override
    public void run() {
        try {
            Set<Waiter> waiters = WaiterManager.get().getWaiters();
            for (Waiter waiter : waiters) {
                if (!waiter.isExpired()) {
                    if (waiter.getExpirationTime() <= System.currentTimeMillis()) {
                        waiter.expire();
                    }
                } else WaiterManager.get().getWaiters().remove(waiter);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
