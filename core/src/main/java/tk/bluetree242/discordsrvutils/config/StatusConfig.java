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

package tk.bluetree242.discordsrvutils.config;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.List;

public interface StatusConfig {

    @AnnotationBasedSorter.Order(10)
    @ConfComments("# Events to update the status message on.")
    @ConfDefault.DefaultStrings({"org.bukkit.event.player.PlayerJoinEvent", "org.bukkit.event.player.PlayerQuitEvent"})
    List<String> update_events();

    @AnnotationBasedSorter.Order(20)
    @ConfComments("# Delay to update the status message in seconds. Keep in mind discord has rate limits")
    @ConfDefault.DefaultLong(60)
    Long update_delay();

}
