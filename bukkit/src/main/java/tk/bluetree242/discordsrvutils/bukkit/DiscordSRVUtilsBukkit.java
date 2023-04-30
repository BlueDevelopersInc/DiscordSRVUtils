/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2023 BlueTree242
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

package tk.bluetree242.discordsrvutils.bukkit;

import github.scarsz.discordsrv.DiscordSRV;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.LoggerFactory;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.bukkit.discordsrv.SlashCommandProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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

    @Getter
    private DiscordSRVUtils core = null;

    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("DiscordSRV") == null) {
            getLogger().severe("DiscordSRV is not installed or failed to start. Download DiscordSRV at https://www.spigotmc.org/resources/discordsrv.18494/");
            getLogger().severe("Disabling...");
            core = null;
            disable();
            return;
        }
        if (core == null) {
            core = new DiscordSRVUtils(new BukkitPlugin(this));
            ((BukkitPlugin) core.getPlatform()).setDiscordSRVUtils(core);
        }
        core.onEnable();
        if (!isEnabled()) return;
        //bstats stuff
        Metrics metrics = new Metrics(this, 9456);
        metrics.addCustomChart(new AdvancedPie("features", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            //Removed Tickets Because it caused lag on a few servers
                /*
                if (!core.getTicketManager().getPanels().get().isEmpty())
                valueMap.put("Tickets", 1);
                 */
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
        metrics.addCustomChart(new SimplePie("admins", () -> core.getJdaManager().getAdminIds().size() + ""));

        //discordsrv slash commands api
        DiscordSRV.api.addSlashCommandProvider(new SlashCommandProvider(this));
    }

    public void onDisable() {
        if (core == null) return;
        try {
            core.onDisable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        core = null;
    }

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
        if (core.getPluginHookManager().isHooked("Libertybans")) return true;
        return false;
    }
}
