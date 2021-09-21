package tk.bluetree242.discordsrvutils.commands.discord;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import tk.bluetree242.discordsrvutils.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandCategory;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandType;
import tk.bluetree242.discordsrvutils.leveling.LevelingManager;
import tk.bluetree242.discordsrvutils.leveling.PlayerStats;

import java.awt.*;
import java.util.StringJoiner;

public class LeaderboardCommand extends Command {
    public LeaderboardCommand() {
        super("leaderboard", CommandType.EVERYWHERE, "Get the leaderboard of players by level", "[P]leaderboard", null, CommandCategory.LEVELING);
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        StringJoiner joiner = new StringJoiner("\n");
        for (PlayerStats player : LevelingManager.get().getLeaderboard(10).get()) {
            joiner.add("**" +player.getRank() + ".** " + player.getName() + " **Level:**" + player.getLevel());
        }
        embed.setTitle("Leaderboard");
        embed.setDescription(joiner.toString());
        e.reply(embed.build()).queue();
    }
}
