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

package tk.bluetree242.discordsrvutils.listeners.discordsrv;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.*;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import org.bukkit.Bukkit;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.exceptions.StartupException;
import tk.bluetree242.discordsrvutils.leveling.LevelingManager;

public class DiscordSRVListener {
    private final DiscordSRVUtils core = DiscordSRVUtils.get();
    @Subscribe
    public void onReady(DiscordReadyEvent e) {
        try {
            core.whenReady();
        } catch (Throwable ex) {
            new StartupException(ex).printStackTrace();
            Bukkit.getPluginManager().disablePlugin(core);
        }
    }

    @Subscribe
    public void onLink(AccountLinkedEvent e) {
        if (!core.isReady()) return;
        LevelingManager manager = LevelingManager.get();
        manager.getPlayerStats(e.getUser().getIdLong()).thenAcceptAsync(stats -> {
            int level = stats.getLevel();
            if (stats == null) return;
            String id = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(stats.getUuid());
            if (id == null) return;
            Member member = core.getGuild().retrieveMemberById(id).complete();
            if (member == null) return;
            for (Role role : manager.getRolesToRemove()) {
                if (member.getRoles().contains(role))
                    core.getGuild().removeRoleFromMember(member, role).queue();
            }
            Role toAdd = manager.getRoleForLevel(level);
            if (toAdd != null) {
                core.getGuild().addRoleToMember(member, toAdd).queue();
            }
        });

    }

    @Subscribe
    public void onUnlink(AccountUnlinkedEvent e) {
        if (!core.isReady()) return;
        
        LevelingManager manager = LevelingManager.get();
        core.executeAsync(() -> {
            Member member = core.getGuild().retrieveMemberById(e.getDiscordId()).complete();
            if (member != null) {
                for (Role role : manager.getRolesToRemove()) {
                    if (member.getRoles().contains(role))
                        core.getGuild().removeRoleFromMember(member, role).queue();
                }
            }
        });
    }

    @Subscribe
    public void onDiscordMsg(DiscordGuildMessagePreProcessEvent e) {
        if (e.getMessage().getContentRaw().toLowerCase().startsWith(core.getCommandPrefix().toLowerCase()))
        e.setCancelled(true);
    }
}
