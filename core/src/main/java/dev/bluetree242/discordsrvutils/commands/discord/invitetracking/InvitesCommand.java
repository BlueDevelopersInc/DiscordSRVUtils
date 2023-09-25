/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2022 BlueTree242
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

package dev.bluetree242.discordsrvutils.commands.discord.invitetracking;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import dev.bluetree242.discordsrvutils.systems.commandmanagement.Command;
import dev.bluetree242.discordsrvutils.systems.commandmanagement.CommandCategory;
import dev.bluetree242.discordsrvutils.systems.commandmanagement.CommandEvent;
import dev.bluetree242.discordsrvutils.systems.invitetracking.UserInvites;

public class InvitesCommand extends Command {
    public InvitesCommand(DiscordSRVUtils core) {
        super(core, "invites", "Get Amount of invites for a user", "[P]invites [user]", null, CommandCategory.INVITE_TRACKING,
                new OptionData(OptionType.USER, "user_mention", "User to get invites of", false));
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        User user = e.getOption("user_mention") == null ? e.getAuthor() : e.getOption("user_mention").getAsUser();
        UserInvites invites = core.getInviteTrackingManager().getInvites(e.getConnection(), user.getIdLong());
        e.reply(
                core.getMessageManager().getMessage("message:invites",
                        PlaceholdObjectList.ofArray(core, new PlaceholdObject(core, user, "user"), new PlaceholdObject(core, invites, "invites")), null).build()
        ).queue();
    }

    public boolean isEnabled() {
        return core.getMainConfig().track_invites() && super.isEnabled();
    }
}
