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

package tk.bluetree242.discordsrvutils.bukkit.listeners.punishments.litebans;

import litebans.api.Entry;
import litebans.api.Events;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.interfaces.Punishment;


public class LitebansPunishmentListener extends Events.Listener {

    private final DiscordSRVUtils core;

    public LitebansPunishmentListener(DiscordSRVUtils core) {
        this.core = core;
        litebans.api.Events.get().register(this);
    }

    public void unregister() {
        litebans.api.Events.get().unregister(this);
    }

    public void entryAdded(Entry e) {
        core.getAsyncManager().executeAsync(() -> {
            if (!core.isReady()) return;
            LitebansPunishment punishment = new LitebansPunishment(e);
            Punishment.handlePunishment(punishment, core);
        });
    }

    public void entryRemoved(Entry e) {
        if (!core.isReady()) return;
        core.getAsyncManager().executeAsync(() -> {
            LitebansPunishment punishment = new LitebansPunishment(e);
            Punishment.handlePunishment(punishment, core);
        });
    }


}
