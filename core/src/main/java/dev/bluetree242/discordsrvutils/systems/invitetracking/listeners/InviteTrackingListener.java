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

package dev.bluetree242.discordsrvutils.systems.invitetracking.listeners;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.systems.invitetracking.InviteTrackingManager;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Invite;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.invite.GuildInviteCreateEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class InviteTrackingListener extends ListenerAdapter {

    private final DiscordSRVUtils core;

    public void onGuildInviteCreate(GuildInviteCreateEvent e) {
        Invite i = e.getInvite();
        if (i.getInviter() == null) return;
        core.getInviteTrackingManager().getCachedInvites().add(new InviteTrackingManager.CachedInvite(i.getCode(), i.getInviter().getIdLong(), e.getGuild().getIdLong(), i.getUses()));
    }

    public void onGuildInviteDelete(@NotNull GuildInviteDeleteEvent e) {
        core.getInviteTrackingManager().getCachedInvites().removeIf(i -> i.getCode().equals(e.getCode()));
    }

    public void onRoleUpdatePermissions(@NotNull RoleUpdatePermissionsEvent e) {
        core.getInviteTrackingManager().cacheInvites();
    }

    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent e) {
        if (e.getUser().getIdLong() == core.getJDA().getSelfUser().getIdLong())
            core.getInviteTrackingManager().cacheInvites();
    }
}
