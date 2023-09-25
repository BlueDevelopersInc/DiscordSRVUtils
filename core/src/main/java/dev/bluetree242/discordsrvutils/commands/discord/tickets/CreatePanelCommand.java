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
import dev.bluetree242.discordsrvutils.waiters.CreatePanelWaiter;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;

import java.awt.*;

public class CreatePanelCommand extends Command {
    public CreatePanelCommand(DiscordSRVUtils core) {
        super(core, "createpanel", "Create a ticket panel", "[P]createpanel", null, CommandCategory.TICKETS_ADMIN, "cp");
        setAdminOnly(true);
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        if (CreatePanelWaiter.getWaiter((TextChannel) e.getChannel(), e.getMember().getUser()) != null) {
            e.getChannel().sendMessage(Embed.error("You are already creating one")).queue();
            return;
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.ORANGE);
        embed.setDescription("**Step 1: Please Send the name of the panel**");
        e.reply(embed.build()).queue();
        new CreatePanelWaiter(core, (TextChannel) e.getChannel(), e.getMember().getUser());
    }
}
