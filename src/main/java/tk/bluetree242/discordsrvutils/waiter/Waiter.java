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