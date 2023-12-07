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

package dev.bluetree242.discordsrvutils.commands.discord.other;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.embeds.Embed;
import dev.bluetree242.discordsrvutils.systems.commands.discord.Command;
import dev.bluetree242.discordsrvutils.systems.commands.discord.CommandEvent;

import java.util.UUID;

public class UnlinkAccountCommand extends Command {
    public UnlinkAccountCommand(DiscordSRVUtils core) {
        super(core, "unlinkaccount", "Unlink your Discord Account with your in-game account", "[P]unlinkaccount", null);
        addAliases("unlink");
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        UUID uuid = core.getDiscordSRV().getUuid(e.getAuthor().getId());
        if (uuid == null) e.reply(Embed.error("You are not linked.")).queue();
        else {
            core.getDiscordSRV().unlink(uuid);
            e.reply(Embed.success("You have been unlinked.")).queue();
        }
    }
}
