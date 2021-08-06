package tk.bluetree242.discordsrvutils.waiters;

import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import tk.bluetree242.discordsrvutils.tickets.Panel;
import tk.bluetree242.discordsrvutils.waiter.Waiter;
import tk.bluetree242.discordsrvutils.waiter.WaiterManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreatePanelWaiter extends Waiter {
    private Panel.Builder builder = new Panel.Builder();
    private int step = 1;
    private TextChannel channel;
    private User user;
    public CreatePanelWaiter(TextChannel channel, User user) {
        this.channel = channel;
        this.user = user;
    }



    public static CreatePanelWaiter getWaiter(TextChannel channel, User user) {
        for (Waiter w : WaiterManager.get().getWaiterByName("CreatePanel")) {
            CreatePanelWaiter waiter = (CreatePanelWaiter) w;
            if (waiter.getChannel().getIdLong() == channel.getIdLong()) {
                if (waiter.getUser().getIdLong() == user.getIdLong()) {
                    return waiter;
                }
            }
        }
        return null;
    }

    public User getUser() {
        return user;
    }
    public TextChannel getChannel() {
        return channel;
    }
    public int getStep() {
        return step;
    }
    public Panel.Builder getBuilder() {
        return builder;
    }
    public void setStep(int num) {
        step =num;
    }
    @Override
    public long getExpirationTime() {
        return getStartTime() + 180000;
    }

    @Override
    public String getWaiterName() {
        return "CreatePanel";
    }

    @Override
    public void whenExpired() {
        channel.sendMessage("**Cancelled: Timed Out**").queue();
    }
}
