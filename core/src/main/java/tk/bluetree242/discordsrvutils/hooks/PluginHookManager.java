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

import java.util.ArrayList;
import java.util.List;

public class PluginHookManager {

    private static PluginHookManager main;
    private DiscordSRVUtils core = DiscordSRVUtils.get();
    private List<PluginHook> hooks = new ArrayList<>();

    public PluginHookManager() {
        main = this;
    }

    public static PluginHookManager get() {
        return main;
    }

    public List<PluginHook> getHooks() {
        return hooks;
    }

    public void hookAll() {
        for (PluginHook hook : new ArrayList<>(hooks)) {
            if (DiscordSRVUtils.getPlatform().getServer().isPluginEnabled(hook.getRequiredPlugin())) {
                try {
                    hook.hook();
                } catch (Exception e) {
                    core.getLogger().severe("Failed to hook into " + hook.getRequiredPlugin());
                }
            }
        }
    }

    public void removeHookAll() {
        for (PluginHook hook : new ArrayList<>(hooks)) {
            hook.removeHook();
        }
    }
}
