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

package dev.bluetree242.discordsrvutils.bukkit.listeners.afk.cmi;

import com.Zrips.CMI.events.CMIAfkEnterEvent;
import com.Zrips.CMI.events.CMIAfkLeaveEvent;
import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.hooks.PluginHook;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class CMIHook extends PluginHook {
    private final DiscordSRVUtils core;
    private CMIAfkListener listener;

    @Override
    public String getRequiredPlugin() {
        return "CMI";
    }

    @Override
    public void hook() {
        removeHook();
        Bukkit.getPluginManager().registerEvents(listener = new CMIAfkListener(core), (Plugin) core.getPlatform().getOriginal());
    }

    @Override
    public void removeHook() {
        if (listener == null) return;
        CMIAfkEnterEvent.getHandlerList().unregister((Plugin) core.getPlatform().getOriginal());
        CMIAfkLeaveEvent.getHandlerList().unregister((Plugin) core.getPlatform().getOriginal());
        listener = null;
    }

    @Override
    public boolean isHooked() {
        return listener != null;
    }
}
