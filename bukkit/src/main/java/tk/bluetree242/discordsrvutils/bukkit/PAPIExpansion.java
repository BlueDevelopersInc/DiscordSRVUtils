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

package tk.bluetree242.discordsrvutils.bukkit;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.hooks.PluginHook;

import java.util.List;

@RequiredArgsConstructor
public class PAPIExpansion extends PlaceholderExpansion {
    private final DiscordSRVUtils core;

    @Override
    public String getIdentifier() {
        return "discordsrvutils";
    }

    @Override
    public boolean canRegister() {
        return core.isEnabled();
    }

    @Override
    public String getAuthor() {
        return "BlueTree242";
    }

    @Override
    public String getVersion() {
        return core.getPlatform().getDescription().getVersion();
    }

    @Override
    public List<String> getPlaceholders() {
        return List.of("level", "rank", "xp");
    }

    @Override
    public String getRequiredPlugin() {
        return "DiscordSRVUtils";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        if (!core.isReady()) return "...";
        identifier = identifier.toLowerCase();
        if (identifier.equalsIgnoreCase("level")) {
            if (p == null) return "Unknown";
            return core.getLevelingManager().getCachedStats(p.getUniqueId()).getLevel() + "";
        } else if (identifier.equalsIgnoreCase("xp")) {
            if (p == null) return "Unknown";
            return core.getLevelingManager().getCachedStats(p.getUniqueId()).getXp() + "";
        } else if (identifier.equalsIgnoreCase("rank")) {
            if (p == null) return "Unknown";
            return core.getLevelingManager().getCachedStats(p.getUniqueId()).getRank() + "";
        }
        return null;
    }

    protected static class Hook extends PluginHook {
        private final DiscordSRVUtils core;
        private PAPIExpansion expansion;

        public Hook(DiscordSRVUtils core) {
            this.core = core;
            if (Bukkit.getPluginManager().isPluginEnabled(getRequiredPlugin())) hook();
        }

        @Override
        public String getRequiredPlugin() {
            return "PlaceholderAPI";
        }

        @Override
        public void hook() {
            //on next tick because of those bukkit sync errors when PAPI fires the registration event
            Bukkit.getScheduler().runTask((Plugin) core.getPlatform().getOriginal(), () -> {
                (expansion = new PAPIExpansion(core)).register();
            });
        }

        @Override
        public void removeHook() {
            if (expansion == null) return;
            expansion.unregister();
            expansion = null;
        }
    }
}
