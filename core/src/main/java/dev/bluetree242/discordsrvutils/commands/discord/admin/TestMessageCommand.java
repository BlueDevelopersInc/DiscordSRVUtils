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

package dev.bluetree242.discordsrvutils.commands.discord.admin;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.exceptions.MessageNotFoundException;
import dev.bluetree242.discordsrvutils.systems.commandmanagement.Command;
import dev.bluetree242.discordsrvutils.systems.commandmanagement.CommandCategory;
import dev.bluetree242.discordsrvutils.systems.commandmanagement.CommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;
import org.json.JSONException;

public class TestMessageCommand extends Command {
    public TestMessageCommand(DiscordSRVUtils core) {
        super(core, "testmessage", "Test an Embed by it's name", "[P]testmessage <name>", null, CommandCategory.ADMIN,
                new OptionData(OptionType.STRING, "name", "Name of the Embed Message to test", true));
        addAliases("tm");
        setAdminOnly(true);
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        String name = e.getOption("name").getAsString();
        try {
            e.replyMessage("message:" + name).setEphemeral(true).queue();
        } catch (MessageNotFoundException ex) {
            e.replyErr("Embed does not exist").setEphemeral(true).queue();
        } catch (JSONException ex) {
            e.replyErr("Embed is invalid").setEphemeral(true).queue();
        }
    }
}