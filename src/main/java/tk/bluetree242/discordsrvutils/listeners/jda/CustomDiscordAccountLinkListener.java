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

package tk.bluetree242.discordsrvutils.listeners.jda;

import github.scarsz.discordsrv.Debug;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberJoinEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;

import java.util.UUID;

public class CustomDiscordAccountLinkListener extends ListenerAdapter {
    // Original From DiscordAccountLinkListener DiscordSRV Class
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getChannel().getIdLong() != DiscordSRVUtils.get().getMainConfig().linkaccount_channel()) return;
        String response = DiscordSRV.getPlugin().getAccountLinkManager().process(event.getMessage().getContentRaw(), event.getAuthor().getId());
        if (response != null) event.getMessage().reply(response).queue();
    }


    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!DiscordSRVUtils.get().removedDiscordSRVAccountLinkListener) return;
        // add linked role and nickname back to people when they rejoin the server
        UUID uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(event.getUser().getId());
        if (uuid != null) {
            Role roleToAdd = DiscordUtil.resolveRole(DiscordSRV.config().getString("MinecraftDiscordAccountLinkedRoleNameToAddUserTo"));
            if (roleToAdd != null) DiscordUtil.addRoleToMember(event.getMember(), roleToAdd);
            else DiscordSRV.debug(Debug.GROUP_SYNC, "Couldn't add user to null role");

            if (DiscordSRV.config().getBoolean("NicknameSynchronizationEnabled")) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                DiscordSRV.getPlugin().getNicknameUpdater().setNickname(event.getMember(), player);
            }
        }
    }


}
