package com.bluetree.discordsrvutils;

import com.bluetree.discordsrvutils.Commands.discordsrvutilsCommand;
import com.bluetree.discordsrvutils.Events.EssentialsAfk;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.OnlineStatus;
import github.scarsz.discordsrv.dependencies.jda.api.requests.GatewayIntent;
import net.md_5.bungee.api.ChatColor;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class DiscordSRVUtils extends JavaPlugin {
    public DiscordSRVEventListener DISCORDSRVEVENTLISTENER;
    public JDALISTENER JDALISTENER;

    public static JDA getJda() {
        return DiscordSRV.getPlugin().getJda();
    }


    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.reloadConfig();
        if (!getServer().getPluginManager().isPluginEnabled("DiscordSRV")) {
            getLogger().warning("DiscordSRVUtils could not be enabled. DiscordSRV is not installed or is not enabled.");
            getLogger().warning("We will add support for no discordsrv in the future.");
            setEnabled(false);
            return;

        }
        if (getServer().getPluginManager().isPluginEnabled("Essentials")) {
            getServer().getPluginManager().registerEvents(new EssentialsAfk(this), this);
        }
        getCommand("discordsrvutils").setExecutor(new discordsrvutilsCommand(this));
        this.DISCORDSRVEVENTLISTENER = new DiscordSRVEventListener(this);
        this.JDALISTENER = new JDALISTENER(this);
        DiscordSRV.api.subscribe(DISCORDSRVEVENTLISTENER);
        if (DiscordSRV.isReady) {
            getJda().addEventListener(JDALISTENER);
            getLogger().warning("Please restart to enable all features.");
            if (getConfig().getString("bot_status") == null) {

            }
            else {
                if (getConfig().getString("bot_status").equalsIgnoreCase("DND")) {
                    getJda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                }
                else if (getConfig().getString("bot_status").equalsIgnoreCase("IDLE")) {
                    getJda().getPresence().setStatus(OnlineStatus.IDLE);
                }
                else if (getConfig().getString("bot_status").equalsIgnoreCase("ONLINE")) {
                    getJda().getPresence().setStatus(OnlineStatus.ONLINE);
                }

            }
        }
        if (getConfig().getLong("welcomer_channel") == 000000000000000000) {
            getLogger().warning("Welcomer messages channel not spectified");
        }
        new Updatechecker(this).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version.replace("_", " "))) {
                getLogger().info(ChatColor.GREEN + "No new version available. (" + version.replace("_", " ") + ")");
            } else {
                getLogger().info(ChatColor.GREEN + "A new version is available. Please download as fast as possible!" + " Your version: " + ChatColor.YELLOW + this.getDescription().getVersion() + ChatColor.GREEN + " New version: " + ChatColor.YELLOW + version.replace("_", " "));
            }
        });
        int pluginId = 9456; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);



    }
    @Override
    public void onDisable() {
        if (!getServer().getPluginManager().isPluginEnabled("DiscordSRV")) return;

            DiscordSRV.api.unsubscribe(DISCORDSRVEVENTLISTENER);
    }
    @Override
    public void onLoad() {
        if (!getServer().getPluginManager().isPluginEnabled("DiscordSRV")) return;
        DiscordSRV.api.requireIntent(GatewayIntent.GUILD_MESSAGE_REACTIONS);
    }

    public static void setupCommands() {

    }
}
