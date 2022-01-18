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

package tk.bluetree242.discordsrvutils.platform;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import tk.bluetree242.discordsrvutils.platform.command.CommandUser;

import java.util.List;
import java.util.UUID;

public abstract class PlatformServer {

    public abstract boolean isPluginEnabled(String name);

    public abstract boolean isPluginInstalled(String name);

    public abstract PlatformPluginDescription getPluginDescription(String name);

    public abstract List<PlatformPlayer> getOnlinePlayers();

    public abstract CommandUser getConsoleSender();

    public abstract String colors(String s);

    public abstract DiscordSRV getDiscordSRV();

    public abstract String getDiscordId(UUID uuid);

    public abstract UUID getUuid(String id);

    public abstract JDA getJDA();

    public abstract Guild getMainGuild();

    public abstract TextChannel getMainChatChannel();
}
