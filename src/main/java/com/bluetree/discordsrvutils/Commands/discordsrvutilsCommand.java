package com.bluetree.discordsrvutils.Commands;

import com.bluetree.discordsrvutils.DiscordSRVUtils;
import com.bluetree.discordsrvutils.PluginConfiguration;
import com.google.common.base.Charsets;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.OnlineStatus;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

public class discordsrvutilsCommand implements CommandExecutor {
    public static JDA getJda() {
        return DiscordSRV.getPlugin().getJda();
    }
    public static JDA jda;

    int warnings = 0;
    int errors = 0;
    private final DiscordSRVUtils core;

    public discordsrvutilsCommand(DiscordSRVUtils core) {

        this.core = core;
    }


    private FileConfiguration newConfig = null;




    private File configFile = null;
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(args.length >= 1)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bRunning DiscordSRVUtils v.&a" + core.getDescription().getVersion() + " "));


        }
        else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("discordsrvutils.reload")) {
                    sender.sendMessage(ChatColor.GREEN + "Reloading...");
                    YamlConfiguration config = new YamlConfiguration();
                    try {
                        core.saveDefaultConfig();
                        this.configFile = new File(core.getDataFolder(), "config.yml");
                        newConfig = PluginConfiguration.loadConfiguration(configFile);


                        final InputStream defConfigStream = getResource("config.yml");
                        if (defConfigStream == null) {
                            sender.sendMessage("Weird thing is null");
                        }

                        newConfig.setDefaults(PluginConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
                        core.reloadConfig();
                    }
                    catch (InvalidConfigurationException exception) {
                        sender.sendMessage(ChatColor.RED + "Config Broken. Check the error on console.");
                        exception.printStackTrace();
                        return true;
                    }
                    catch (IOException ex) {
                        sender.sendMessage(ChatColor.RED + "Config Broken. Check the error on console.");
                        ex.printStackTrace();
                        return true;

                    }
                    if (core.getConfig().getLong("welcomer_channel") == 000000000000000000) {
                      sender.sendMessage(ChatColor.GOLD + "welcomer_channel is in it's default stat. Please update it.");
                      warnings = warnings + 1;
                    }
                    if (core.getConfig().getStringList("welcomer_message") == null) {
                        sender.sendMessage(ChatColor.RED + "welcomer_message is not set.");
                        errors = errors + 1;
                    }
                    if (core.getConfig().getString("mc_welcomer_message") == null) {
                        sender.sendMessage(ChatColor.RED + "mc_welcomer_message is not set");
                        errors = errors +1;
                    }
                    if (getJda().getGuildChannelById(core.getConfig().getString("welcomer_channel")) == null) {
                        sender.sendMessage(ChatColor.GOLD + "welcomer_channel channel was not found");
                        warnings = warnings + 1;
                    }


                        if (core.getConfig().getString("bot_status").equalsIgnoreCase("DND")) {
                            getJda().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                        }
                        else if (core.getConfig().getString("bot_status").equalsIgnoreCase("IDLE")) {
                            getJda().getPresence().setStatus(OnlineStatus.IDLE);
                        }
                        else if (core.getConfig().getString("bot_status").equalsIgnoreCase("ONLINE")) {
                            getJda().getPresence().setStatus(OnlineStatus.ONLINE);

                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "bot_status is not ONLINE or IDLE or DND");
                            errors = errors+1;
                        }
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bPlugin reloaded with &e" + errors + " &berrors and &e" + warnings + "&b warnings."));
                    warnings = 0;
                    errors = 0;
                }
                else {
                    sender.sendMessage(ChatColor.RED + "You don't have perms to use this command.");
                }

            }
        }
        return true;

    }
    protected final ClassLoader getClassLoader() {
        return classLoader;
    }
    final ClassLoader classLoader = this.getClass().getClassLoader();

    public InputStream getResource(@NotNull String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }
}
