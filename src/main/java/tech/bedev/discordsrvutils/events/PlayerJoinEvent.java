package tech.bedev.discordsrvutils.events;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tech.bedev.discordsrvutils.DiscordSRVUtils;
import tech.bedev.discordsrvutils.UpdateChecker;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerJoinEvent implements Listener {
    private DiscordSRVUtils core;
    public PlayerJoinEvent(DiscordSRVUtils core) {
        this.core = core;
    }
    @EventHandler
    public void onJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        if (!core.getConfig().getBoolean("update_checker")) return;
        if (e.getPlayer().hasPermission("discordsrvutils.updatechecker")) {
            new UpdateChecker(core).getVersion(version -> {
                if (core.getDescription().getVersion().equalsIgnoreCase(version.replace("_", " "))) {
                    core.getLogger().info(ChatColor.GREEN + "No new version available. (" + version.replace("_", " ") + ")");
                } else {
                    Timer yourtimer = new Timer(true);
                    yourtimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            TextComponent msg = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&aA newer version of DiscordSRVUtils is available.\n&9Your version: &5" + core.getDescription().getVersion() + "\n&9Newer version: &5" + version + "\n&6Click to download."));
                            msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/discordsrvutils.85958/updates"));
                            msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "" + ChatColor.BOLD + "Click to download").create()));
                            e.getPlayer().spigot().sendMessage(msg);
                        }

                    }, 10000);
                    core.getLogger().info(ChatColor.GREEN + "A new version is available. Please update ASAP!" + " Your version: " + ChatColor.YELLOW + core.getDescription().getVersion() + ChatColor.GREEN + " New version: " + ChatColor.YELLOW + version.replace("_", " "));

                }
            });
        }
    }
}
