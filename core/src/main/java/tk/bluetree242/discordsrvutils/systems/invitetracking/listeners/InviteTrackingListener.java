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

package tk.bluetree242.discordsrvutils.systems.invitetracking.listeners;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Invite;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.GuildReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.invite.GuildInviteCreateEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.systems.invitetracking.InviteTrackingManager;

@RequiredArgsConstructor
public class InviteTrackingListener extends ListenerAdapter {

    private final DiscordSRVUtils core;

    public void onGuildReady(GuildReadyEvent e) {
        e.getGuild().retrieveInvites().queue(is ->
                is.forEach(i -> core.getInviteTrackingManager().getCachedInvites().add(new InviteTrackingManager.CachedInvite(i.getCode(), i.getInviter().getIdLong(), e.getGuild().getIdLong(), i.getUses()))));
    }

    public void onGuildInviteCreate(GuildInviteCreateEvent e) {
        Invite i = e.getInvite();
        core.getInviteTrackingManager().getCachedInvites().add(new InviteTrackingManager.CachedInvite(i.getCode(), i.getInviter().getIdLong(), e.getGuild().getIdLong(), i.getUses()));
    }

    public void onGuildInviteDelete(GuildInviteDeleteEvent e) {
        core.getInviteTrackingManager().getCachedInvites().removeIf(i -> i.getCode().equals(e.getCode()));
    }
}
