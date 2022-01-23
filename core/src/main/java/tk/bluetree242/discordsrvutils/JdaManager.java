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

package tk.bluetree242.discordsrvutils;

import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import lombok.Getter;
import tk.bluetree242.discordsrvutils.listeners.jda.WelcomerAndGoodByeListener;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandListener;
import tk.bluetree242.discordsrvutils.systems.leveling.listeners.jda.DiscordLevelingListener;
import tk.bluetree242.discordsrvutils.systems.suggestions.listeners.SuggestionListener;
import tk.bluetree242.discordsrvutils.systems.tickets.listeners.PanelOpenListener;
import tk.bluetree242.discordsrvutils.systems.tickets.listeners.TicketCloseListener;
import tk.bluetree242.discordsrvutils.systems.tickets.listeners.TicketDeleteListener;
import tk.bluetree242.discordsrvutils.waiters.listeners.CreatePanelListener;
import tk.bluetree242.discordsrvutils.waiters.listeners.EditPanelListener;
import tk.bluetree242.discordsrvutils.waiters.listeners.PaginationListener;

import java.util.ArrayList;
import java.util.List;

public class JdaManager {
    private final DiscordSRVUtils core = DiscordSRVUtils.get();
    //listeners that should be registered
    @Getter
    private final List<ListenerAdapter> listeners = new ArrayList<>();

    public JdaManager() {
        //Add The JDA Listeners to the List
        listeners.add(new CommandListener());
        listeners.add(new WelcomerAndGoodByeListener());
        listeners.add(new CreatePanelListener());
        listeners.add(new PaginationListener());
        listeners.add(new TicketDeleteListener());
        listeners.add(new PanelOpenListener());
        listeners.add(new TicketCloseListener());
        listeners.add(new EditPanelListener());
        listeners.add(new DiscordLevelingListener());
        listeners.add(new SuggestionListener());
    }

    public JDA getJDA() {
        return DiscordSRVUtils.getPlatform().getDiscordSRV().getJDA();
    }

    public void registerListeners() {
        getJDA().addEventListener(listeners.toArray(new Object[0]));
    }

    public void removeListeners() {
        if (getJDA() != null) {
            for (ListenerAdapter listener : listeners) {
                getJDA().removeEventListener(listener);
            }
        }
    }

    public TextChannel getChannel(long id, TextChannel channel) {
        if (id == -1) {
            return channel;
        }
        if (id == 0) {
            return DiscordSRVUtils.getPlatform().getDiscordSRV().getMainChatChannel();
        } else return getJDA().getTextChannelById(id);
    }

    public List<Long> getAdminIds() {
        return core.getMainConfig().admins();
    }

    public List<User> getAdmins() {
        List<User> admins = new ArrayList<>();
        for (Long lng : getAdminIds()) {
            User usr = getJDA().getUserById(lng);
            if (usr != null) {
                admins.add(usr);
            } else {
                admins.add(getJDA().retrieveUserById(lng).complete());
            }
        }
        return admins;
    }

    public List<Role> getAdminsRoles() {
        List<Role> roles = new ArrayList<>();
        for (Long lng : getAdminIds()) {
            roles.add(core.getGuild().getRoleById(lng));
        }
        return roles;
    }



    public boolean isAdmin(long id) {
        if (getAdminIds().contains(id)) return true;
        Member member = core.getGuild().retrieveMemberById(id).complete();
        if (member != null) {
            for (Role role : member.getRoles()) {
                if (getAdminIds().contains(role.getIdLong())) return true;
            }
        }
        return false;
    }



}
