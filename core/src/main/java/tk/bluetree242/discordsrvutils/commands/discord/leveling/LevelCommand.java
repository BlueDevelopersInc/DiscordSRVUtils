/*
 *  LICENSE
 *  DiscordSRVUtils
 *  -------------
 *  Copyright (C) 2020 - 2021 BlueTree242
 *  -------------
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program.  If not, see
 *  <http://www.gnu.org/licenses/gpl-3.0.html>.
 *  END
 */

package tk.bluetree242.discordsrvutils.commands.discord.leveling;

import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandCategory;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.systems.leveling.LevelingManager;
import tk.bluetree242.discordsrvutils.systems.leveling.PlayerStats;

public class LevelCommand extends Command {
    public LevelCommand() {
        super("level", "Get leveling info about a user or yourself", "[P]level [Player name or user mention]", null, CommandCategory.LEVELING,
                new OptionData(OptionType.USER, "user_mention", "User to get level of, must be linked", false),
                new OptionData(OptionType.STRING, "player_name", "Player Name to get level of", false));
        addAliases("rank");
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        PlayerStats target;

        if (e.getEvent().getOption("user_mention") != null) {
            User user = e.getEvent().getOption("user_mention").getAsUser();
            target = LevelingManager.get().getPlayerStats(user.getIdLong()).get();
            if (target == null) {
                e.replyErr(user.getAsTag() + "'s discord account is not linked with minecraft account");
                return;
            }
        } else if (e.getEvent().getOption("player_name") != null) {
            String name = e.getEvent().getOption("player_name").getAsString();
            target = LevelingManager.get().getPlayerStats(name).get();
            if (target == null) {
                e.replyErr("Player never joined before").queue();
                return;
            }
        } else {
            target = LevelingManager.get().getPlayerStats(e.getAuthor().getIdLong()).get();
            if (target == null) {
                e.replyErr("Your account is not linked with any Minecraft Account. Use `/discordsrv link` in game to link your account").queue();
                return;
            }
        }

        e.replyMessage(core.getLevelingConfig().level_command_message(), PlaceholdObjectList.ofArray(new PlaceholdObject(target, "stats"))).queue();
    }
}
