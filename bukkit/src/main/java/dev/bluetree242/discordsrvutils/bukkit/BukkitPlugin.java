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

package dev.bluetree242.discordsrvutils.bukkit;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.bukkit.cmd.BukkitCommandListener;
import dev.bluetree242.discordsrvutils.bukkit.listeners.afk.afkplus.AfkPlusHook;
import dev.bluetree242.discordsrvutils.bukkit.listeners.afk.cmi.CMIHook;
import dev.bluetree242.discordsrvutils.bukkit.listeners.afk.essentials.EssentialsHook;
import dev.bluetree242.discordsrvutils.bukkit.listeners.punishments.advancedban.AdvancedBanHook;
import dev.bluetree242.discordsrvutils.bukkit.listeners.punishments.libertybans.LibertybansHook;
import dev.bluetree242.discordsrvutils.bukkit.listeners.punishments.litebans.LitebansHook;
import dev.bluetree242.discordsrvutils.bukkit.status.BukkitStatusListener;
import dev.bluetree242.discordsrvutils.platform.PlatformPlayer;
import dev.bluetree242.discordsrvutils.platform.PlatformPluginDescription;
import dev.bluetree242.discordsrvutils.platform.PlatformServer;
import dev.bluetree242.discordsrvutils.platform.PluginPlatform;
import dev.bluetree242.discordsrvutils.systems.status.StatusListener;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Logger;

public class BukkitPlugin extends PluginPlatform<JavaPlugin> {
    private final DiscordSRVUtilsBukkit main;
    @Getter
    private final BukkitDiscordSRV discordSRV;
    private DiscordSRVUtils core;
    private StatusListener statusListener;

    public BukkitPlugin(DiscordSRVUtilsBukkit main) {
        this.main = main;
        discordSRV = new BukkitDiscordSRV();
    }

    private String applyPlaceholders(String s, Player player) {
        if (!core.isEnabled()) return s;
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            String to = s.replace("&", "** ** *");
            String fina = PlaceholderAPI.setPlaceholders(player, to);
            return fina.replace("** ** *", "&");
        }
        return s;
    }

    private String applyPlaceholders(String s, OfflinePlayer player) {
        if (!core.isEnabled()) return s;
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            String to = s.replace("&", "** ** *");
            String fina = PlaceholderAPI.setPlaceholders(player, to);
            return fina.replace("** ** *", "&");
        }
        return s;
    }

    @Override
    public Logger getLogger() {
        return main.getLogger();
    }

    @Override
    public File getDataFolder() {
        return main.getDataFolder();
    }

    @Override
    public PlatformServer getServer() {
        return new BukkitPlatformServer(core, main);
    }

    @Override
    public void disable() {
        main.disable();
    }

    @Override
    public PlatformPluginDescription getDescription() {
        return new BukkitPluginDescription(main);
    }

    @Override
    public void registerListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(new BukkitListener(core), main);
    }

    @Override
    public InputStream getResource(String name) {
        return main.getResource(name);
    }

    @Override
    public boolean isEnabled() {
        return main.isEnabled();
    }

    @Override
    public void registerCommands() {
        BukkitCommandListener listener = new BukkitCommandListener(core);
        Objects.requireNonNull(main.getCommand("discordsrvutils")).setExecutor(listener);
        Objects.requireNonNull(main.getCommand("discordsrvutils")).setTabCompleter(listener);
    }

    @Override
    public DiscordSRVUtilsBukkit getOriginal() {
        return main;
    }

    @Override
    public StatusListener getStatusListener() {
        return statusListener == null ? new BukkitStatusListener(core) : statusListener;
    }

    protected void setDiscordSRVUtils(DiscordSRVUtils core) {
        this.core = core;
    }

    @Override
    public void addHooks() {
        //Punishments
        new AdvancedBanHook(core);
        new LibertybansHook(core);
        new LitebansHook(core);
        //Afk
        new EssentialsHook(core);
        new CMIHook(core);
        new AfkPlusHook(core);
        //PAPI
        new PAPIExpansion.Hook(core);
    }

    @Override
    public String placehold(PlatformPlayer player, String s) {
        if (player instanceof BukkitPlayer) return applyPlaceholders(s, ((BukkitPlayer) player).getOriginal());
        else if (player instanceof BukkitOfflinePlayer)
            return applyPlaceholders(s, ((BukkitOfflinePlayer) player).getOriginal());
        else return applyPlaceholders(s, null);
    }
}