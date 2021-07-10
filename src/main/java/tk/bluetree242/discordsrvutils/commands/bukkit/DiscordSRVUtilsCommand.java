package tk.bluetree242.discordsrvutils.commands.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import space.arim.dazzleconf.error.ConfigFormatSyntaxException;
import space.arim.dazzleconf.error.InvalidConfigException;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.commandmanagement.BukkitCommand;
import tk.bluetree242.discordsrvutils.exceptions.ConfigurationLoadException;
import tk.bluetree242.discordsrvutils.utils.Utils;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DiscordSRVUtilsCommand extends BukkitCommand {
    public void onRunAsync(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws Throwable{
        if (args.length == 0) {
            sender.sendMessage(colors("&eRunning DiscordSRVUtils v" + core.getDescription().getVersion()));
            sender.sendMessage(colors("&bStatus: " + (core.isReady() ? "&aRunning and functioning" : "&cNot running")));
            return;
        }
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("discordsrvutils.reload")) {
                    sender.sendMessage(colors("&eReloading Configuration"));
                    try {
                        core.reloadConfigs();
                        sender.sendMessage(colors("&aConfiguration sucessfully reloaded"));
                    } catch (ConfigurationLoadException e) {
                        sender.sendMessage(colors("&cCould not reload the " + e.getConfigName() + ". Please check server console"));
                        e.printStackTrace();
                    }
                    return;
                }
            }
        }
        sender.sendMessage(ChatColor.RED + "SubCommand not found");
    }
}
