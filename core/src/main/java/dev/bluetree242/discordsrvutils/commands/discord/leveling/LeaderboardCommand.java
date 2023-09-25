/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2023 BlueTree242
 * -------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * END
 */

package dev.bluetree242.discordsrvutils.commands.discord.leveling;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.systems.commandmanagement.Command;
import dev.bluetree242.discordsrvutils.systems.commandmanagement.CommandCategory;
import dev.bluetree242.discordsrvutils.systems.commandmanagement.CommandEvent;
import dev.bluetree242.discordsrvutils.systems.leveling.PlayerStats;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.StringJoiner;

public class LeaderboardCommand extends Command {
    public LeaderboardCommand(DiscordSRVUtils core) {
        super(core, "leaderboard", "Get the leaderboard of players by level", "[P]leaderboard", null, CommandCategory.LEVELING, "lb");
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        StringJoiner joiner = new StringJoiner("\n");
        for (PlayerStats player : core.getLevelingManager().getLeaderboard(10)) {
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
            joiner.add("**" + player.getRank() + ".** " + prefix + player.getName() + " **Level:**" + player.getLevel());
        }
        embed.setTitle("Leaderboard");
        embed.setDescription(joiner.toString());
        embed.setThumbnail(core.getPlatform().getDiscordSRV().getMainGuild().getIconUrl());
        e.reply(embed.build()).queue();
    }

    public boolean isEnabled() {
        return core.getLevelingConfig().enabled() && super.isEnabled();
    }
}
