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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tk.bluetree242.discordsrvutils.bukkit.listeners.afk.afkplus.AfkPlusHook;
import tk.bluetree242.discordsrvutils.bukkit.listeners.afk.cmi.CMIHook;
import tk.bluetree242.discordsrvutils.bukkit.listeners.afk.essentials.EssentialsHook;
import tk.bluetree242.discordsrvutils.bukkit.listeners.punishments.advancedban.AdvancedBanHook;
import tk.bluetree242.discordsrvutils.bukkit.listeners.punishments.libertybans.LibertybansHook;
import tk.bluetree242.discordsrvutils.bukkit.listeners.punishments.litebans.LitebansHook;
import tk.bluetree242.discordsrvutils.bukkit.status.BukkitStatusListener;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.platform.PlatformPlayer;
import tk.bluetree242.discordsrvutils.platform.PlatformPluginDescription;
import tk.bluetree242.discordsrvutils.platform.PlatformServer;
import tk.bluetree242.discordsrvutils.platform.PluginPlatform;
import tk.bluetree242.discordsrvutils.platform.listener.PlatformListener;
import tk.bluetree242.discordsrvutils.status.StatusListener;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BukkitPlugin<JavaPlugin> extends PluginPlatform {
    private final List<PlatformListener> listeners = new ArrayList<>();
    private final DiscordSRVUtilsBukkit main;

    public BukkitPlugin(DiscordSRVUtilsBukkit main) {
        this.main = main;
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
        return new BukkitPlatformServer();
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
        Bukkit.getServer().getPluginManager().registerEvents(new BukkitListener(), main);
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
        //1 command, just using the simple onCommand in JavaPlugin is fine
    }

    @Override
    public DiscordSRVUtilsBukkit getOriginal() {
        return main;
    }

    @Override
    public StatusListener getStatusListener() {
        return StatusListener.get() == null ? new BukkitStatusListener() : StatusListener.get();
    }

    @Override
    public void addHooks() {
        //Punishments
        new AdvancedBanHook();
        new LibertybansHook();
        new LitebansHook();
        //Afk
        new EssentialsHook();
        new CMIHook();
        new AfkPlusHook();
        //PAPI
        new PAPIExpansion.Hook();
    }

    @Override
    public List<PlatformListener> getListeners() {
        return listeners;
    }

    @Override
    public void addListener(PlatformListener listener) {
        listeners.add(listener);
    }

    @Override
    public String placehold(PlatformPlayer player, String s) {
        Player p = player != null ? player instanceof BukkitPlayer ? ((BukkitPlayer) player).player : null : null;
        return PlaceholdObject.applyPlaceholders(s, p);
    }
}
