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

package tk.bluetree242.discordsrvutils.listeners.jda;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Invite;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberJoinEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberRemoveEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import tk.bluetree242.discordsrvutils.systems.invitetracking.InviteTrackingManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
public class WelcomerAndGoodByeListener extends ListenerAdapter {
    private final DiscordSRVUtils core;

    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent e) {
        core.getAsyncManager().executeAsync(() -> {
            if (!e.getUser().isBot()) {
                //get the inviter
                List<Invite> invs = e.getGuild().retrieveInvites().complete();
                InviteTrackingManager.CachedInvite invite = null;
                User inviter = null;
                Iterator<InviteTrackingManager.CachedInvite> invites = core.getInviteTrackingManager().getCachedInvites().iterator();
                while (invites.hasNext()) {
                    InviteTrackingManager.CachedInvite currentInvite = invites.next();
                    for (Invite inv : invs) {
                        if (inv.getCode().equals(currentInvite.getCode()) && inv.getUses() == (currentInvite.getUses() + 1)) {
                            invite = currentInvite;
                            currentInvite.setUses(currentInvite.getUses() + 1);
                            inviter = inv.getInviter();
                        }
                    }
                }
                //store in db
                if (core.getMainConfig().track_invites() && invite != null) {
                    try (Connection conn = core.getDatabaseManager().getConnection()) {
                        core.getInviteTrackingManager().addInvite(core.getDatabaseManager().jooq(conn), e.getUser().getIdLong(), invite.getUserId(), invite.getGuildId());
                    } catch (SQLException ex) {
                        core.getErrorHandler().defaultHandle(ex);
                    }
                }
                //welcomer
                if (core.getMainConfig().welcomer_enabled()) {
                    MessageChannel channel = core.getMainConfig().welcomer_dm_user() ? e.getUser().openPrivateChannel().complete() : core.getPlatform().getDiscordSRV().getMainGuild().getTextChannelById(core.getMainConfig().welcomer_channel());
                    if (channel == null) {
                        core.severe("No Text Channel was found with ID " + core.getMainConfig().welcomer_channel() + ". Join Message was not sent for " + e.getUser().getAsTag());
                    } else {
                        PlaceholdObjectList holders = new PlaceholdObjectList(core);
                        holders.add(new PlaceholdObject(core, e.getUser(), "user"));
                        holders.add(new PlaceholdObject(core, core.getPlatform().getDiscordSRV().getMainGuild(), "guild"));
                        holders.add(new PlaceholdObject(core, e.getMember(), "member"));
                        if (inviter != null) holders.add(new PlaceholdObject(core, inviter, "inviter"));
                        channel.sendMessage(core.getMessageManager().getMessage(core.getMainConfig().welcomer_message(), holders, null).build()).queue();
                    }
                    if (core.getMainConfig().welcomer_role() != 0) {
                        Role role = core.getPlatform().getDiscordSRV().getMainGuild().getRoleById(core.getMainConfig().welcomer_role());
                        if (role == null) {
                            core.severe("Welcomer Role not found... User did not receive any roles");
                        } else {
                            core.getPlatform().getDiscordSRV().getMainGuild().addRoleToMember(e.getMember(), role).queue();
                        }
                    }
                }
            }
        });
    }

    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent e) {
        core.getAsyncManager().executeAsync(() -> {
            if (!e.getUser().isBot()) {
                try (Connection conn = core.getDatabaseManager().getConnection()) {
                    core.getInviteTrackingManager().leftServer(core.getDatabaseManager().jooq(conn), e.getUser().getIdLong());
                } catch (SQLException ex) {
                    core.getErrorHandler().defaultHandle(ex);
                }
                if (core.getMainConfig().goodbye_enabled()) {
                    MessageChannel channel = core.getJdaManager().getChannel(core.getMainConfig().goodbye_channel());
                    if (channel == null) {
                        core.severe("No Text Channel was found with ID " + core.getMainConfig().goodbye_channel() + ". Leave Message was not sent for " + e.getUser().getAsTag());
                        return;
                    }
                    PlaceholdObjectList holders = new PlaceholdObjectList(core);
                    holders.add(new PlaceholdObject(core, e.getUser(), "user"));
                    holders.add(new PlaceholdObject(core, core.getPlatform().getDiscordSRV().getMainGuild(), "guild"));
                    holders.add(new PlaceholdObject(core, e.getMember(), "member"));
                    channel.sendMessage(core.getMessageManager().getMessage(core.getMainConfig().goodbye_message(), holders, null).build()).queue();
                }
            }
        });
    }
}
