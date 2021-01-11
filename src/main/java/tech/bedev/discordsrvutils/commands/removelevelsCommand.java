package tech.bedev.discordsrvutils.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import tech.bedev.discordsrvutils.DiscordSRVUtils;
import tech.bedev.discordsrvutils.Person.Person;

import java.util.UUID;

public class removelevelsCommand implements CommandExecutor {

    private final DiscordSRVUtils core;

    public removelevelsCommand(DiscordSRVUtils core) {

        this.core = core;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!DiscordSRVUtils.isReady) {
            sender.sendMessage(ChatColor.RED + "DiscordSRVUtils/DIscordSRV is still loading...");
            return true;
        }
        if (!(args.length >= 2)) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player> <Levels to remove>");
        } else {
            UUID target = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
            Person person = core.getPersonByUUID(target);
            if (person == null) {
                sender.sendMessage(ChatColor.RED + "Player has never joined  before.");
            } else {
                try {
                    Integer.parseInt(args[1]);
                    person.insertLeveling();
                    person.removeLevels(Integer.parseInt(args[1]));
                    sender.sendMessage(ChatColor.GREEN + args[0] + ChatColor.BLUE + "'s Level is now " + ChatColor.GOLD + person.getLevel());
                } catch (NumberFormatException ex) {
                    sender.sendMessage(ChatColor.RED + "Invalid level set.");
                }
            }
        }
        return true;
    }
}
