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

package tk.bluetree242.discordsrvutils.commands.discord.tickets;

import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandCategory;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandType;
import tk.bluetree242.discordsrvutils.embeds.Embed;
import tk.bluetree242.discordsrvutils.systems.tickets.TicketManager;

public class DeletePanelCommand extends Command {

    public DeletePanelCommand() {
        super("deletepanel", CommandType.GUILDS, "Delete a panel", "[P]deletepanel <Panel ID>", null, CommandCategory.TICKETS_ADMIN, "dp");
        setAdminOnly(true);
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        String[] args = e.getArgs();
        if (args.length <= 1) {
            e.reply(Embed.error("Please specify the id of panel, for panel list use " + getCommandPrefix() + "panelist")).queue();
        } else {
            DiscordSRVUtils.get().handleCF(TicketManager.get().getPanelById(args[1]), panel -> {
                if (panel == null) {
                    e.reply(Embed.error("Panel not found, use " + getCommandPrefix() + "panelist for list of panels")).queue();
                } else {
                    DiscordSRVUtils.get().handleCF(panel.delete(), s -> {
                        e.replySuccess("Successfully deleted panel. Note that deleting ticket channels may take a while").queue();
                    }, error -> {
                        DiscordSRVUtils.get().defaultHandle(error, e.getChannel());
                    });
                }
            }, error -> {
                DiscordSRVUtils.get().defaultHandle(error, e.getChannel());
            });
        }

    }
}
