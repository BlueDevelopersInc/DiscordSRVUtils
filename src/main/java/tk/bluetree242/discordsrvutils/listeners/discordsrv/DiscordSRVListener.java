package tk.bluetree242.discordsrvutils.listeners.discordsrv;

import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import org.bukkit.Bukkit;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.exceptions.StartupException;

public class DiscordSRVListener {
    private final DiscordSRVUtils core = DiscordSRVUtils.get();
    @Subscribe
    public void onReady(DiscordReadyEvent e) {
        try {
            core.whenReady();
        } catch (Throwable ex) {
            new StartupException(ex).printStackTrace();
            Bukkit.getPluginManager().disablePlugin(core);
        }
    }
}
