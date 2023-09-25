/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2022 BlueTree242
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

package dev.bluetree242.discordsrvutils.bukkit.listeners.punishments.libertybans;


import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.interfaces.Punishment;
import space.arim.libertybans.api.LibertyBans;
import space.arim.libertybans.api.event.PostPardonEvent;
import space.arim.libertybans.api.event.PostPunishEvent;
import space.arim.omnibus.Omnibus;
import space.arim.omnibus.OmnibusProvider;
import space.arim.omnibus.events.EventConsumer;
import space.arim.omnibus.events.ListenerPriorities;
import space.arim.omnibus.events.RegisteredListener;

public class LibertybansListener {
    private final LibertyBans plugin;
    private final DiscordSRVUtils core;
    private final RegisteredListener pListener;
    private final RegisteredListener pardonListener;

    public LibertybansListener(DiscordSRVUtils core) {
        this.core = core;
        Omnibus omnibus = OmnibusProvider.getOmnibus();
        plugin = omnibus.getRegistry().getProvider(LibertyBans.class).orElseThrow();
        pListener = omnibus.getEventBus().registerListener(PostPunishEvent.class, ListenerPriorities.NORMAL, new PunishmentListener());
        pardonListener = omnibus.getEventBus().registerListener(PostPardonEvent.class, ListenerPriorities.NORMAL, new PardonListener());
    }

    public void unregister() {
        Omnibus omnibus = OmnibusProvider.getOmnibus();
        if (pListener != null)
            omnibus.getEventBus().unregisterListener(pListener);
        if (pardonListener != null)
            omnibus.getEventBus().unregisterListener(pardonListener);
    }


    public class PunishmentListener implements EventConsumer<PostPunishEvent> {

        @Override
        public void accept(PostPunishEvent e) {
            core.getAsyncManager().executeAsync(() -> {
                LibertyBansPunishment punishment = new LibertyBansPunishment(e.getPunishment(), e.getPunishment().getOperator(), false, plugin);
                Punishment.handlePunishment(punishment, core);
            });
        }
    }

    public class PardonListener implements EventConsumer<PostPardonEvent> {

        @Override
        public void accept(PostPardonEvent e) {
            core.getAsyncManager().executeAsync(() -> {
                LibertyBansPunishment punishment = new LibertyBansPunishment(e.getPunishment(), e.getPunishment().getOperator(), true, plugin);
                Punishment.handlePunishment(punishment, core);
            });
        }
    }
}
