/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2024 BlueTree242
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

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.bukkit.listeners.afk.essentials.EssentialsAFKListener;
import dev.bluetree242.discordsrvutils.platform.PlatformPlayer;
import dev.bluetree242.discordsrvutils.utils.Utils;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitPlayer extends PlatformPlayer<Player> {
    private final DiscordSRVUtils core;
    @Getter
    private final Player original;

    public BukkitPlayer(DiscordSRVUtils core, Player original) {
        this.original = original;
        this.core = core;
    }

    @Override
    public String getName() {
        return original.getName();
    }

    @Override
    public void sendMessage(String msg) {
        original.sendMessage(Utils.colors(msg));
    }

    @Override
    public void sendMessage(Component component) {
        core.getPlatform().getAudience().player(getUniqueId()).sendMessage(component);
    }


    @Override
    public boolean hasPermission(String node) {
        return original.hasPermission(node);
    }

    @Override
    public UUID getUniqueId() {
        return original.getUniqueId();
    }

    @Override
    public String placeholders(String s) {
        return core.getPlatform().placehold(this, s);
    }

    @Override
    public boolean shouldSendAfk() {
        return EssentialsAFKListener.shouldSend(original);
    }
}
