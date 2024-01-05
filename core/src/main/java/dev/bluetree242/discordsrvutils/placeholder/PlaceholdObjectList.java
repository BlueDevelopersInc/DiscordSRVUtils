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

package dev.bluetree242.discordsrvutils.placeholder;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.platform.PlatformPlayer;
import github.scarsz.discordsrv.util.NamedValueFormatter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class PlaceholdObjectList extends ArrayList<PlaceholdObject> {
    private final DiscordSRVUtils core;

    public static PlaceholdObjectList ofArray(DiscordSRVUtils core, PlaceholdObject... holders) {
        PlaceholdObjectList list = new PlaceholdObjectList(core);
        list.addAll(Arrays.asList(holders));
        return list;
    }

    public String apply(String s, PlatformPlayer placehold) {
        String val = s;
        Map<String, Object> variables = new HashMap<>();
        variables.put("guild", core.getPlatform().getDiscordSRV().getMainGuild());
        variables.put("jda", core.getJDA());
        variables.put("DSU", core);
        variables.put("server", core.getServer().getOriginal());
        variables.put("TicketManager", core.getTicketManager());
        variables.put("LevelingManager", core.getLevelingManager());
        variables.put("CommandManager", core.getCommandManager());
        val = NamedValueFormatter.formatExpressions(val, core, variables);
        val = core.getPlatform().placehold(placehold, val);
        for (PlaceholdObject holder : this) {
            variables.put(holder.display, holder.getObject());
            val = holder.apply(val, placehold, false);
        }
        return val;
    }

    public String apply(String s) {
        return apply(s, null);
    }

}
