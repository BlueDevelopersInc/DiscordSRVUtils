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

import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import tk.bluetree242.discordsrvutils.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandType;
import tk.bluetree242.discordsrvutils.status.StatusManager;

public class StatusCommand extends Command {
    public StatusCommand() {
        super("status", CommandType.GUILDS, "Set the status message", "[P]status <ping channel>", null);
        setAdminOnly(true);
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        if (e.getMessage().getMentionedChannels().isEmpty()) {
            e.replyErr("Please mention a channel").queue();
            return;
        } else {
            e.handleCF(StatusManager.get().newMessage(e.getMessage().getMentionedChannels().get(0)), false, "Check " + e.getMessage().getMentionedChannels().get(0).getAsMention(), "Error creating status message");
        }
    }
}
