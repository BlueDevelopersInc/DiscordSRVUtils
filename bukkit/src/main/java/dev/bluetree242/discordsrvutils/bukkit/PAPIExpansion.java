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
import dev.bluetree242.discordsrvutils.hooks.PluginHook;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class PAPIExpansion extends PlaceholderExpansion {
    private final DiscordSRVUtils core;

    @Override
    public @NotNull String getIdentifier() {
        return "discordsrvutils";
    }

    @Override
    public boolean canRegister() {
        return core.isEnabled();
    }

    @Override
    public @NotNull String getAuthor() {
        return "BlueTree242";
    }

    @Override
    public @NotNull String getVersion() {
        return core.getPlatform().getDescription().getVersion();
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
    public String onPlaceholderRequest(Player p, @NotNull String identifier) {
        return onPlaceholderRequest(p != null ? p.getUniqueId() : null, identifier);
    }

    @Override
    public String onRequest(OfflinePlayer p, @NotNull String identifier) {
        return onPlaceholderRequest(p != null ? p.getUniqueId() : null, identifier);
    }

    public String onPlaceholderRequest(UUID p, String identifier) {
        if (!core.isReady()) return "...";
        identifier = identifier.toLowerCase();
        if (identifier.equalsIgnoreCase("level")) {
            if (p == null) return "Unknown";
            return String.valueOf(core.getLevelingManager().getCachedStats(p).getLevel());
        } else if (identifier.equalsIgnoreCase("xp")) {
            if (p == null) return "Unknown";
            return String.valueOf(core.getLevelingManager().getCachedStats(p).getXp());
        } else if (identifier.equalsIgnoreCase("rank")) {
            if (p == null) return "Unknown";
            return String.valueOf(core.getLevelingManager().getCachedStats(p).getRank());
        } else if (identifier.equalsIgnoreCase("xp_total_required")) {
            if (p == null) return "Unknown";
            return String.valueOf(core.getLevelingManager().getCachedStats(p).getTotalXpRequired());
        } else if (identifier.equalsIgnoreCase("xp_percentage")) {
            if (p == null) return "Unknown";
            return core.getLevelingManager().getCachedStats(p).getXpPercentage() + "%";
        } else if (identifier.equalsIgnoreCase("xp_left")) {
            if (p == null) return "Unknown";
            return String.valueOf(core.getLevelingManager().getCachedStats(p).getTotalXpRequired() -
                    core.getLevelingManager().getCachedStats(p).getXp());
        } else if (identifier.equalsIgnoreCase("xp_percentage_left")) {
            if (p == null) return "Unknown";
            return 100 - core.getLevelingManager().getCachedStats(p).getXpPercentage() + "%";
        }
        return null;
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return Arrays.asList(
                "%discordsrvutils_level%",
                "%discordsrvutils_xp%",
                "%discordsrvutils_rank%",
                "%discordsrvutils_xp_total_required%",
                "%discordsrvutils_xp_total_percentage%",
                "%discordsrvutils_xp_percentage%",
                "%discordsrvutils_xp_left%"
        );
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
            // On next tick because of those bukkit sync errors when PAPI fires the registration event
            Bukkit.getScheduler().runTask((Plugin) core.getPlatform().getOriginal(), () -> (expansion = new PAPIExpansion(core)).register());
        }

        @Override
        public void removeHook() {
            if (expansion == null) return;
            expansion.unregister();
            expansion = null;
        }

        @Override
        public boolean isHooked() {
            return expansion != null;
        }
    }
}
