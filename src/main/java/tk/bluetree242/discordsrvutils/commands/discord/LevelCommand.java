package tk.bluetree242.discordsrvutils.commands.discord;

import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import tk.bluetree242.discordsrvutils.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandCategory;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandType;
import tk.bluetree242.discordsrvutils.leveling.LevelingManager;
import tk.bluetree242.discordsrvutils.leveling.PlayerStats;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;

import java.util.UUID;

public class LevelCommand extends Command {
    public LevelCommand() {
        super("level", CommandType.EVERYWHERE, "Get leveling info about a user or yourself", "[P]level [Player name or user mention]",  null, CommandCategory.LEVELING, "rank");
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        String[] args = e.getArgs();
        PlayerStats target;
        if (args.length <= 1) {
            target = LevelingManager.get().getPlayerStats(e.getAuthor().getIdLong()).get();
            if (target == null) {
                e.replyErr("Your account is not linked with any Minecraft Account. Use `/discordsrv link` in game to link your account").queue();
                return;
            }
        } else {
            if (e.getMessage().getMentionedMembers().isEmpty()) {
                String name = args[1];
                target = LevelingManager.get().getPlayerStats(name).get();
                if (target == null) {
                    e.replyErr("Player never joined before").queue();
                    return;
                }
            } else {
                User user = e.getMessage().getMentionedUsers().get(0);
                target = LevelingManager.get().getPlayerStats(user.getIdLong()).get();
                if (target == null) {
                    e.replyErr(user.getAsTag() + "'s discord account is not linked with minecraft account").queue();
                    return;
                }
            }
        }

        e.replyMessage(core.getLevelingConfig().level_command_message(), PlaceholdObjectList.ofArray(new PlaceholdObject(target, "stats"))).queue();
    }
}
