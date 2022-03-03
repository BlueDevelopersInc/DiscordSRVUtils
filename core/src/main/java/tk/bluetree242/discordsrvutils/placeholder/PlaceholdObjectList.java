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

package tk.bluetree242.discordsrvutils.placeholder;

import github.scarsz.discordsrv.util.NamedValueFormatter;
import lombok.RequiredArgsConstructor;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.platform.PlatformPlayer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class PlaceholdObjectList extends ArrayList<PlaceholdObject> {
    private final DiscordSRVUtils core;

    public static PlaceholdObjectList ofArray(DiscordSRVUtils core, PlaceholdObject... holders) {
        PlaceholdObjectList list = new PlaceholdObjectList(core);
        for (PlaceholdObject holder : holders) {
            list.add(holder);
        }
        return list;
    }

    public String apply(String s, PlatformPlayer placehold) {
        final String[] val = {s};
        Map<String, Object> variables = new HashMap<>();
        variables.put("guild", core.getPlatform().getDiscordSRV().getMainGuild());
        variables.put("jda", core.getJDA());
        variables.put("DSU", core);
        variables.put("server", core.getServer().getOriginal());
        variables.put("TicketManager", core.getTicketManager());
        variables.put("LevelingManager", core.getLevelingManager());
        variables.put("CommandManager", core.getCommandManager());
        for (PlaceholdObject holder : this) {
            variables.put(holder.display, holder.getObject());
            Map<String, Method> map = holder.getholdersMap();
            map.forEach((key, result) -> {
                try {
                    if (val[0].contains("[" + holder.display + "." + key + "]")) {
                        Object invoked = result.invoke(holder.getObject());
                        String value = null;
                        if (invoked != null) {
                            value = invoked.toString();
                        }
                        val[0] = val[0].replace("[" + holder.display + "." + key + "]", value == null ? "null" : value);
                    }
                } catch (Exception e) {
                }
            });
        }

        val[0] = core.getPlatform().placehold(placehold, val[0]);
        val[0] = NamedValueFormatter.formatExpressions(val[0], core, variables);
        return val[0];
    }

    public String apply(String s) {
        return apply(s, null);
    }

}
