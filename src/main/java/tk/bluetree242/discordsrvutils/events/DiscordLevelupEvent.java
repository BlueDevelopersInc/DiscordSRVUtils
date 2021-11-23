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

package tk.bluetree242.discordsrvutils.events;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import org.bukkit.Bukkit;
import tk.bluetree242.discordsrvutils.leveling.PlayerStats;

public class DiscordLevelupEvent extends LevelupEvent {
    private PlayerStats stats;
    private TextChannel channel;
    private User user;

    public DiscordLevelupEvent(PlayerStats stats, TextChannel channel, User user) {
        super(stats, Bukkit.getOfflinePlayer(DiscordSRV.getPlugin().getAccountLinkManager().getUuid(user.getId())));
        this.stats = stats;
        this.channel = channel;
        this.user = user;
    }

    public PlayerStats getStats() {
        return stats;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public User getUser() {
        return user;
    }
}
