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

package tk.bluetree242.discordsrvutils.commands.discord.admin;

import org.json.JSONException;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandCategory;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandType;
import tk.bluetree242.discordsrvutils.exceptions.EmbedNotFoundException;

public class TestMessageCommand extends Command {
    public TestMessageCommand() {
        super("testmessage", CommandType.EVERYWHERE, "Test an Embed by it's name", "[P]testmessage <name>", null, CommandCategory.ADMIN, "tm");
        setAdminOnly(true);
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        String[] args = e.getArgs();
        if (!(args.length >= 2)) {
            e.reply(getHelpEmbed()).queue();
        } else {
            String name = args[1];
            try {
                e.replyMessage("message:" + name).queue();
            } catch (EmbedNotFoundException ex) {
                e.replyErr("Embed does not exist").queue();
            } catch (JSONException ex) {
                e.replyErr("Embed is invalid").queue();
            }
        }
    }
}
