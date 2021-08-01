package tk.bluetree242.discordsrvutils.listeners.afk;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.messages.MessageManager;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;

public class EssentialsAFKListener implements Listener {

    private DiscordSRVUtils core = DiscordSRVUtils.get();
    @EventHandler
    public void onAfk(AfkStatusChangeEvent e) {
        core.executeAsync(() -> {
            boolean afk = e.getAffected().isAfk();
            Player player = e.getAffected().getBase();
            if (core.getMainConfig().afk_message_enabled()) {
                PlaceholdObjectList holders = new PlaceholdObjectList();
                holders.add(new PlaceholdObject(player, "player"));
                TextChannel channel = core.getChannel(core.getMainConfig().afk_channel());
                if (channel == null) {
                    core.severe("No Channel was found with ID " + core.getMainConfig().afk_channel() + ". Afk/NoLonger message was not sent for " + player.getName());
                    return;
                }
                Message msg;
                if (afk) {
                    msg = MessageManager.get().getMessage(core.getMainConfig().afk_message(), holders, player).build();
                } else {
                    msg = MessageManager.get().getMessage(core.getMainConfig().no_longer_afk_message(), holders, player).build();
                }
                channel.sendMessage(msg).queue();
            }
        });
    }
}
