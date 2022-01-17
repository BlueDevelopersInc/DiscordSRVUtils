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

package tk.bluetree242.discordsrvutils.bukkit.listeners.afk.cmi;

import com.Zrips.CMI.events.CMIAfkEnterEvent;
import com.Zrips.CMI.events.CMIAfkLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.hooks.PluginHook;

public class CMIHook extends PluginHook {
    private CMIAfkListener listener;
    @Override
    public String getRequiredPlugin() {
        return "CMI";
    }

    @Override
    public void hook() {
        removeHook();
        Bukkit.getPluginManager().registerEvents(listener = new CMIAfkListener(), (Plugin) DiscordSRVUtils.get().getPlatform().getOriginal());
    }

    @Override
    public void removeHook() {
        if (listener == null) return;
        CMIAfkEnterEvent.getHandlerList().unregister((Plugin) DiscordSRVUtils.get().getPlatform().getOriginal());
        CMIAfkLeaveEvent.getHandlerList().unregister((Plugin) DiscordSRVUtils.get().getPlatform().getOriginal());
        listener = null;
    }
}
