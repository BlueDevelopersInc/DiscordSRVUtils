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

package tk.bluetree242.discordsrvutils.bukkit.listeners.punishments.litebans;

import lombok.RequiredArgsConstructor;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.hooks.PluginHook;

@RequiredArgsConstructor
public class LitebansHook extends PluginHook {
    private final DiscordSRVUtils core;
    LitebansPunishmentListener listener;

    @Override
    public String getRequiredPlugin() {
        return "LiteBans";
    }

    @Override
    public void hook() {
        removeHook();
        listener = new LitebansPunishmentListener(core);
    }

    @Override
    public void removeHook() {
        if (listener == null) return;
        listener.unregister();
        listener = null;
    }

    @Override
    public boolean isHooked() {
        return listener != null;
    }
}
