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

package dev.bluetree242.discordsrvutils.bukkit;

import dev.bluetree242.discordsrvutils.platform.PlatformDiscordSRV;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;

import java.util.UUID;

public class BukkitDiscordSRV extends PlatformDiscordSRV {

    @Override
    public DiscordSRV getDiscordSRV() {
        return DiscordSRV.getPlugin();
    }

    @Override
    public String getDiscordId(UUID uuid) {
        return getDiscordSRV().getAccountLinkManager().getDiscordId(uuid);
    }

    @Override
    public UUID getUuid(String id) {
        return getDiscordSRV().getAccountLinkManager().getUuid(id);
    }

    @Override
    public void unlink(UUID uuid) {
        getDiscordSRV().getAccountLinkManager().unlink(uuid);
    }

    @Override
    public JDA getJDA() {
        return getDiscordSRV().getJda();
    }

    @Override
    public Guild getMainGuild() {
        return getDiscordSRV().getMainGuild();
    }

    @Override
    public TextChannel getMainChatChannel() {
        return getDiscordSRV().getMainTextChannel();
    }

    @Override
    public String proccessMessage(String num, User author) {
        return getDiscordSRV().getAccountLinkManager().process(num, author.getId());
    }
}
