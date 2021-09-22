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
        super("leaderboard", CommandType.GUILDS, "Get the leaderboard of players by level", "[P]leaderboard", null, CommandCategory.LEVELING);
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        StringJoiner joiner = new StringJoiner("\n");
        for (PlayerStats player : LevelingManager.get().getLeaderboard(10).get()) {
            String prefix = "";
            switch (player.getRank()) {
                case 1:
                    prefix = ":first_place:";
                    break;
                case 2:
                    prefix = ":second_place:";
                    break;
                case 3:
                    prefix = ":third_place:";
            }
            joiner.add("**" +player.getRank() + ".** "  + prefix + player.getName() + " **Level:**" + player.getLevel());
        }
        embed.setTitle("Leaderboard");
        embed.setDescription(joiner.toString());
        embed.setThumbnail(e.getGuild().getIconUrl());
        e.reply(embed.build()).queue();
    }
}
