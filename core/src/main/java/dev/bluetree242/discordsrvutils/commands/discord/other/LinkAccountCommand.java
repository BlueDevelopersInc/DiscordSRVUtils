/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2024 BlueTree242
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

package dev.bluetree242.discordsrvutils.commands.discord.other;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.systems.commands.discord.Command;
import dev.bluetree242.discordsrvutils.systems.commands.discord.CommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;

public class LinkAccountCommand extends Command {
    public LinkAccountCommand(DiscordSRVUtils core) {
        super(core, "linkaccount", "Link your Discord Account with InGame Account Using Code", "[P]linkaccount <code>", null,
                new OptionData(OptionType.INTEGER, "code", "LinkAccount Code", true));
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        StringBuilder code = new StringBuilder(String.valueOf((int) e.getOption("code").getAsLong()));
        if (code.length() < 4) {
            int numLeft = 4 - code.length();
            while (numLeft != 0) {
                code.insert(0, "0");
                numLeft--;
            }
        }
        String response = core.getPlatform().getDiscordSRV().proccessMessage(code.toString(), e.getAuthor());
        if (response != null) e.reply(response).setEphemeral(true).queue();
    }
}