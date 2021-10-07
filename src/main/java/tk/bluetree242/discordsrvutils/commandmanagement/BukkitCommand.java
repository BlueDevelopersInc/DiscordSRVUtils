package tk.bluetree242.discordsrvutils.commandmanagement;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.utils.Utils;

public  abstract class BukkitCommand implements CommandExecutor {
    protected  DiscordSRVUtils core = DiscordSRVUtils.get();
    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        DiscordSRVUtils.get().executeAsync(() -> {
            try {
                onRunAsync(sender, command, label, args);
            } catch (Throwable ex) {
                ex.printStackTrace();
                sender.sendMessage(Utils.colors("&cAn internal error occurred while executing this command"));
            }
        });
        return true;
    }

    public abstract void onRunAsync(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws Throwable;


    public String colors(String s) {
        return Utils.colors(s);
    }

}
