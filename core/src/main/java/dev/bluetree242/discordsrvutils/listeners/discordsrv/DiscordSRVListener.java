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

package dev.bluetree242.discordsrvutils.listeners.discordsrv;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.exceptions.StartupException;
import dev.bluetree242.discordsrvutils.systems.leveling.LevelingManager;
import dev.bluetree242.discordsrvutils.systems.leveling.PlayerStats;
import dev.bluetree242.discordsrvutils.utils.Utils;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AccountLinkedEvent;
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class DiscordSRVListener {
    private final DiscordSRVUtils core;

    @Subscribe(priority = ListenerPriority.MONITOR)
    public void onReady(DiscordReadyEvent e) {
        try {
            core.whenReady();
        } catch (Throwable ex) {
            new StartupException(ex).printStackTrace();
            core.getPlatform().disable();
        }
    }

    @Subscribe
    public void onLink(AccountLinkedEvent e) {
        if (!core.isReady()) return;
        core.getAsyncManager().executeAsync(() -> {
            LevelingManager manager = core.getLevelingManager();
            PlayerStats stats = manager.getPlayerStats(e.getUser().getIdLong());
            if (stats == null) return;
            int level = stats.getLevel();
            String id = e.getUser().getId();
            if (id == null) return;
            Member member = Utils.retrieveMember(core.getDiscordSRV().getMainGuild(), e.getUser().getIdLong());
            if (member == null) return;
            Collection actions = new ArrayList<>();
            for (Role role : manager.getLevelingRewardsManager().getRolesToRemove(stats.getLevel())) {
                if (member.getRoles().contains(role))
                    actions.add(core.getPlatform().getDiscordSRV().getMainGuild().removeRoleFromMember(member, role).reason("User should not have this role"));
            }
            List<Role> toAdd = manager.getLevelingRewardsManager().getRolesForLevel(level);
            for (Role role : toAdd) {
                actions.add(core.getPlatform().getDiscordSRV().getMainGuild().addRoleToMember(member, role).reason("Account Linked"));
            }
            if (!actions.isEmpty()) RestAction.allOf(actions).queue();
        });
    }

    @Subscribe
    public void onUnlink(AccountUnlinkedEvent e) {
        if (!core.isReady()) return;

        LevelingManager manager = core.getLevelingManager();
        core.getAsyncManager().executeAsync(() -> {
            Member member = Utils.retrieveMember(core.getDiscordSRV().getMainGuild(), e.getDiscordUser().getIdLong());
            if (member != null) {
                for (Role role : manager.getLevelingRewardsManager().getRolesToRemove(null)) {
                    if (member.getRoles().contains(role))
                        core.getPlatform().getDiscordSRV().getMainGuild().removeRoleFromMember(member, role).reason("Account Unlinked").queue();
                }
            }
        });
    }
}
