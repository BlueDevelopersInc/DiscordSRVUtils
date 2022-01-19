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

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.hooks.PluginHook;
import tk.bluetree242.discordsrvutils.leveling.LevelingManager;

import java.util.List;

public class PAPIExpansion extends PlaceholderExpansion {
    private DiscordSRVUtils core = DiscordSRVUtils.get();

    @Override
    public String getIdentifier() {
        return "discordsrvutils";
    }

    @Override
    public boolean canRegister() {
        return DiscordSRVUtils.get().isEnabled();
    }

    @Override
    public String getAuthor() {
        return "BlueTree242";
    }

    @Override
    public String getVersion() {
        return core.getDescription().getVersion();
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
        if (!DiscordSRVUtils.get().isReady()) return "...";
        identifier = identifier.toLowerCase();
        if (identifier.equalsIgnoreCase("level")) {
            if (p == null) return "Unknown";
            return LevelingManager.get().getCachedStats(p.getUniqueId()).getLevel() + "";
        } else if (identifier.equalsIgnoreCase("xp")) {
            if (p == null) return "Unknown";
            return LevelingManager.get().getCachedStats(p.getUniqueId()).getXp() + "";
        } else if (identifier.equalsIgnoreCase("rank")) {
            if (p == null) return "Unknown";
            return LevelingManager.get().getCachedStats(p.getUniqueId()).getRank() + "";
        }
        return null;
    }

    public static class Hook extends PluginHook {
        private PAPIExpansion expansion;

        public Hook() {
            if (Bukkit.getPluginManager().isPluginEnabled(getRequiredPlugin())) hook();
        }

        @Override
        public String getRequiredPlugin() {
            return "PlaceholderAPI";
        }

        @Override
        public void hook() {
            removeHook();
            (expansion = new PAPIExpansion()).register();
        }

        @Override
        public void removeHook() {
            if (expansion == null) return;
            expansion.unregister();
            expansion = null;
        }
    }
}
