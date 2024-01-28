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
import dev.bluetree242.discordsrvutils.bukkit.discordsrv.SlashCommandProvider;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.bstats.bukkit.Metrics;
import github.scarsz.discordsrv.dependencies.bstats.charts.AdvancedPie;
import github.scarsz.discordsrv.dependencies.bstats.charts.SimplePie;
import github.scarsz.discordsrv.dependencies.kyori.adventure.platform.bukkit.BukkitAudiences;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Getter
public class DiscordSRVUtilsBukkit extends JavaPlugin {

    static {
        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(DiscordSRVUtilsBukkit.class.getClassLoader());
        try {
            Method method = LoggerFactory.class.getDeclaredMethod("bind");
            method.setAccessible(true);
            method.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }
    }

    private BukkitAudiences adventure;
    private DiscordSRVUtils core = null;

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("DiscordSRV") == null) {
            getLogger().severe("DiscordSRV is not installed or failed to start. Download DiscordSRV at https://modrinth.com/plugin/discordsrv");
            getLogger().severe("Disabling...");
            core = null;
            disable();
            return;
        }
        if (!AccountLinkManager.class.isInterface()) {
            // DiscordSRV is out of date
            getLogger().severe("Plugin could not be enabled because the version of DiscordSRV you are using is not supported. Please make sure you are on DiscordSRV 1.27.0+.");
            getLogger().severe("Disabling...");
            disable();
            return;
        }
        adventure = BukkitAudiences.create(this);
        if (core == null) {
            core = new DiscordSRVUtils(new BukkitPlugin(this));
            ((BukkitPlugin) core.getPlatform()).setDiscordSRVUtils(core);
        }
        new SlashCommandProviderInitializer().initialize();
        core.onEnable();
        if (!isEnabled()) return;
        new BstatsInitializer().initialize();
    }

    @Override
    public void onDisable() {
        if (core == null) return;
        try {
            core.onDisable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        core = null;
    }

    @Override
    public void onLoad() {
        if (getServer().getPluginManager().getPlugin("DiscordSRV") != null) {
            core = new DiscordSRVUtils(new BukkitPlugin(this));
            ((BukkitPlugin) core.getPlatform()).setDiscordSRVUtils(core);
        }
    }

    public void disable() {
        setEnabled(false);
    }


    private boolean isAnyPunishmentsPluginInstalled() {
        if (core.getPluginHookManager().isHooked("AdvancedBan")) return true;
        if (core.getPluginHookManager().isHooked("Litebans")) return true;
        return core.getPluginHookManager().isHooked("Libertybans");
    }

    private class SlashCommandProviderInitializer {
        public void initialize() {
            DiscordSRV.api.addSlashCommandProvider(new SlashCommandProvider(DiscordSRVUtilsBukkit.this));
        }
    }

    private class BstatsInitializer {
        public void initialize() {
            Metrics metrics = new Metrics(DiscordSRVUtilsBukkit.this, 9456);
            metrics.addCustomChart(new AdvancedPie("features", () -> {
                Map<String, Integer> valueMap = new HashMap<>();
                if (core.getLevelingConfig().enabled()) valueMap.put("Leveling", 1);
                if (core.getSuggestionsConfig().enabled()) valueMap.put("Suggestions", 1);
                if (core.getMainConfig().welcomer_enabled()) valueMap.put("Welcomer", 1);
                if (core.getMainConfig().track_invites()) valueMap.put("Invite Tracking", 1);
                if (core.getBansConfig().isSendPunishmentMsgsToDiscord() && isAnyPunishmentsPluginInstalled())
                    valueMap.put("Punishment Messages", 1);
                if (core.getPluginHookManager().isHooked("Essentials") && core.getMainConfig().afk_message_enabled())
                    valueMap.put("AFK Messages", 1);
                return valueMap;
            }));
            metrics.addCustomChart(new SimplePie("discordsrv_versions", () -> DiscordSRV.getPlugin().getDescription().getVersion()));
            metrics.addCustomChart(new SimplePie("admins", () -> String.valueOf(core.getJdaManager().getAdminIds().size())));
        }
    }
}
