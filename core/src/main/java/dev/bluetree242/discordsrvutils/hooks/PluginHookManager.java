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

package dev.bluetree242.discordsrvutils.hooks;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PluginHookManager {

    private final DiscordSRVUtils core;
    @Getter
    private final List<PluginHook> hooks = new ArrayList<>();

    public void hookAll() {
        for (PluginHook hook : new ArrayList<>(hooks)) {
            if (core.getServer().isPluginEnabled(hook.getRequiredPlugin())) {
                try {
                    if (!hook.isHooked()) {
                        hook.hook();
                        core.getLogger().info("Successfully hooked into " + hook.getRequiredPlugin());
                    }
                } catch (Throwable e) {
                    core.getLogger().severe("Failed to hook into " + hook.getRequiredPlugin());
                }
            }
        }
    }

    public boolean isHooked(String name) {
        return hooks.stream().anyMatch(h -> h.isHooked() && h.getRequiredPlugin().equals(name));
    }

    public void removeHookAll() {
        for (PluginHook hook : new ArrayList<>(hooks)) {
            hook.removeHook();
        }
    }
}
