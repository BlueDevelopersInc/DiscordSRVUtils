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

import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.embeds.Embed;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandCategory;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.waiters.EditPanelWaiter;

public class EditPanelCommand extends Command {
    public EditPanelCommand() {
        super("editpanel", "Edit a panel", "[P]editpanel <Panel ID>", null, CommandCategory.TICKETS_ADMIN,
                new OptionData(OptionType.STRING, "id", "Panel ID", true));
        addAliases("ep");
        setAdminOnly(true);
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        String id = e.getOption("id").getAsString();
        DiscordSRVUtils.get().getAsyncManager().handleCF(core.getTicketManager().getPanelById(id), panel -> {
            if (panel == null) {
                e.reply(Embed.error("Panel not found, use /panelist for list of panels")).queue();
            } else {
                DiscordSRVUtils.get().getAsyncManager().handleCF(core.getTicketManager().getPanelById(id), s -> {
                    if (panel == null) {
                        e.reply(Embed.error("Panel not found, use /panelist for list of panels")).queue();
                    } else {
                        e.reply("Loading Editor Menu...").setEphemeral(true).queue();
                        e.getChannel().sendMessageEmbeds(EditPanelWaiter.getEmbed()).queue(msg -> {
                            new EditPanelWaiter((TextChannel) e.getChannel(), e.getAuthor(), panel.getEditor(), msg);
                            EditPanelWaiter.addReactions(msg);

                        }, error -> {
                            DiscordSRVUtils.get().getErrorHandler().defaultHandle(error, e.getChannel());
                        });
                    }
                }, error -> {
                    DiscordSRVUtils.get().getErrorHandler().defaultHandle(error, e.getChannel());
                });
            }
        }, error -> {
            DiscordSRVUtils.get().getErrorHandler().defaultHandle(error, e.getChannel());
        });
    }
}
