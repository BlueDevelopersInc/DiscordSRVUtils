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

package tk.bluetree242.discordsrvutils.commands.discord.status;

import github.scarsz.discordsrv.dependencies.jda.api.entities.GuildChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.systems.status.StatusManager;

public class StatusCommand extends Command {
    public StatusCommand() {
        super("status", "Set the status message", "[P]status <ping channel>", null,
                new OptionData(OptionType.CHANNEL, "channel", "Channel to send status message in", true));
        setAdminOnly(true);
    }

    @Override
    public void run(CommandEvent e) throws Exception {

        GuildChannel channel = e.getOption("channel").getAsGuildChannel();
        if (!(channel instanceof TextChannel)) {
            e.replyErr("Sorry this can only be a text channel").queue();
            return;
        }
        e.handleCF(DiscordSRVUtils.get().getStatusManager().newMessage((TextChannel) channel), "Check " + channel.getAsMention(), "Error creating status message");
    }
}
