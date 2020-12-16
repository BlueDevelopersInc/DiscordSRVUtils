package tech.bedev.discordsrvutils.events;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import tech.bedev.discordsrvutils.DiscordSRVUtils;
import tech.bedev.discordsrvutils.Managers.ConfOptionsManager;
import tech.bedev.discordsrvutils.Managers.Tickets;
import tech.bedev.discordsrvutils.Person.Person;
import tech.bedev.discordsrvutils.UpdateChecker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class BukkitEventListener implements Listener {
    public static final Random RANDOM = new Random();
    private DiscordSRVUtils core;
    private ConfOptionsManager conf;
    public BukkitEventListener(DiscordSRVUtils core) {
        this.core = core;
        this.conf = new ConfOptionsManager(core);
    }
    @EventHandler
    public void onJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        Person person = core.getPersonByUUID(e.getPlayer().getUniqueId());
        person.insertLeveling();
        if (!core.getConfig().getBoolean("update_checker")) return;
        if (e.getPlayer().hasPermission("discordsrvutils.updatechecker")) {
            String newVersion = UpdateChecker.getLatestVersion();
            if (newVersion.equalsIgnoreCase(core.getDescription().getVersion())) {
                core.getLogger().info(net.md_5.bungee.api.ChatColor.GREEN + "No new version available. (" + newVersion + ")");
            } else {
                core.getLogger().info(net.md_5.bungee.api.ChatColor.GREEN + "A new version is available. Please update ASAP!" + " Your version: " + net.md_5.bungee.api.ChatColor.YELLOW + core.getDescription().getVersion() + net.md_5.bungee.api.ChatColor.GREEN + " New version: " + net.md_5.bungee.api.ChatColor.YELLOW + newVersion);
                TextComponent msg = new TextComponent(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', "&aA newer version of DiscordSRVUtils is available.\n&9Your version: &5" + core.getDescription().getVersion() + "\n&9Newer version: &5" + newVersion + "\n&6Click to download."));
                msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/discordsrvutils.85958/updates"));
                msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(net.md_5.bungee.api.ChatColor.GREEN + "" + net.md_5.bungee.api.ChatColor.BOLD + "Click to download").create()));
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        e.getPlayer().spigot().sendMessage(msg);

                    }
                }, 5000);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Bukkit.getScheduler().runTask(core, () -> {
            if (DiscordSRVUtils.Levelingconfig.Leveling_Enabled()) {
                Person person = core.getPersonByUUID(Bukkit.getOfflinePlayer(e.getPlayer().getName()).getUniqueId());
                person.insertLeveling();
                person.addXP(RANDOM.nextInt(25));
                if (person.getXP() >= 300) {
                    person.clearXP();
                    PlayerLevelupEvent ev = new PlayerLevelupEvent(person, e.getPlayer());
                    Bukkit.getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        person.addLevels(1);
                        e.getPlayer().sendMessage(conf.StringToColorCodes(conf.getConfigWithPapi(e.getPlayer().getUniqueId(), String.join("\n", DiscordSRVUtils.Levelingconfig.levelup_minecraft()))).replace("[Level]", person.getLevel() + ""));
                    }
                }
            }
        });
    }


}
