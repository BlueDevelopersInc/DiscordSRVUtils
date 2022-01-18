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

package tk.bluetree242.discordsrvutils.hooks;

import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.platform.PlatformPluginDescription;

public abstract class PluginHook {
    public abstract String getRequiredPlugin();

    public abstract void hook();

    public abstract void removeHook();

    public final String toString() {
        if (DiscordSRVUtils.get().getPlatform().getServer().isPluginInstalled(getRequiredPlugin())) {
            PlatformPluginDescription info = DiscordSRVUtils.get().getPlatform().getServer().getPluginDescription(getRequiredPlugin());
            return info.getName() + " v" + info.getVersion();
        }
        return getClass().getSimpleName();
    }
}
