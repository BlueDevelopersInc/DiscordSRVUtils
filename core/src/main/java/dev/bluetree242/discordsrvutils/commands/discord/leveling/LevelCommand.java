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
import dev.bluetree242.discordsrvutils.systems.leveling.PlayerStats;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import dev.bluetree242.discordsrvutils.systems.commandmanagement.Command;
import dev.bluetree242.discordsrvutils.systems.commandmanagement.CommandCategory;
import dev.bluetree242.discordsrvutils.systems.commandmanagement.CommandEvent;

public class LevelCommand extends Command {
    public LevelCommand(DiscordSRVUtils core) {
        super(core, "level", "Get leveling info about a user or yourself", "[P]level [Player name or user mention]", null, CommandCategory.LEVELING,
                new OptionData(OptionType.USER, "user_mention", "User to get level of, must be linked", false),
                new OptionData(OptionType.STRING, "player_name", "Player Name to get level of", false));
        addAliases("rank");
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        PlayerStats target;

        if (e.getOption("user_mention") != null) {
            User user = e.getOption("user_mention").getAsUser();
            target = core.getLevelingManager().getPlayerStats(user.getIdLong());
            if (target == null) {
                e.replyErr(new PlaceholdObject(core, user, "user").apply(core.getLevelingConfig().level_command_other_not_linked()));
                return;
            }
        } else if (e.getOption("player_name") != null) {
            String name = e.getOption("player_name").getAsString();
            target = core.getLevelingManager().getPlayerStats(name);
            if (target == null) {
                e.replyErr(core.getLevelingConfig().level_command_invalid_player()).queue();
                return;
            }
        } else {
            target = core.getLevelingManager().getPlayerStats(e.getAuthor().getIdLong());
            if (target == null) {
                e.replyErr(core.getLevelingConfig().level_command_not_linked()).queue();
                return;
            }
        }

        e.replyMessage(core.getLevelingConfig().level_command_message(), PlaceholdObjectList.ofArray(core, new PlaceholdObject(core, target, "stats")), core.getServer().getOfflinePlayer(target.getUuid())).queue();
    }

    public boolean isEnabled() {
        return core.getLevelingConfig().enabled() && super.isEnabled();
    }
}
