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

package tk.bluetree242.discordsrvutils.platform;


import tk.bluetree242.discordsrvutils.platform.listener.PlatformListener;
import tk.bluetree242.discordsrvutils.systems.status.StatusListener;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class PluginPlatform<K> {
    private final List<PlatformListener> listeners = new ArrayList<>();

    //abstract to allow some constant stuff that depend on some methods
    public abstract Logger getLogger();

    public abstract File getDataFolder();

    public abstract PlatformServer getServer();

    public abstract void disable();

    public abstract PlatformPluginDescription getDescription();

    public abstract void registerListeners();

    public abstract InputStream getResource(String name);

    public abstract boolean isEnabled();

    public abstract void registerCommands();

    public abstract K getOriginal();

    public abstract StatusListener getStatusListener();

    public abstract void addHooks();


    public abstract String placehold(PlatformPlayer player, String s);

    public abstract PlatformDiscordSRV getDiscordSRV();

    //platform-event stuff
    public List<PlatformListener> getListeners() {
        return listeners;
    }

    public void addListener(PlatformListener listener) {
        listeners.add(listener);
    }

}
