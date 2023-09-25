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

package dev.bluetree242.discordsrvutils.commands.discord.tickets;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.embeds.Embed;
import dev.bluetree242.discordsrvutils.systems.commandmanagement.Command;
import dev.bluetree242.discordsrvutils.systems.commandmanagement.CommandCategory;
import dev.bluetree242.discordsrvutils.systems.commandmanagement.CommandEvent;
import dev.bluetree242.discordsrvutils.systems.tickets.Panel;
import dev.bluetree242.discordsrvutils.waiters.EditPanelWaiter;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;

public class EditPanelCommand extends Command {
    public EditPanelCommand(DiscordSRVUtils core) {
        super(core, "editpanel", "Edit a panel", "[P]editpanel <Panel ID>", null, CommandCategory.TICKETS_ADMIN,
                new OptionData(OptionType.STRING, "id", "Panel ID", true));
        addAliases("ep");
        setAdminOnly(true);
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        String id = e.getOption("id").getAsString();
        Panel panel = core.getTicketManager().getPanelById(id);
        if (panel == null) {
            e.reply(Embed.error("Panel not found, use /panelist for list of panels")).queue();
        } else {
            e.reply(EditPanelWaiter.getEmbed(true)).addActionRows(EditPanelWaiter.getActionRows()).queue(i -> new EditPanelWaiter((TextChannel) e.getChannel(), e.getAuthor(), panel.getEditor(), i.getInteraction()));
        }
    }
}
