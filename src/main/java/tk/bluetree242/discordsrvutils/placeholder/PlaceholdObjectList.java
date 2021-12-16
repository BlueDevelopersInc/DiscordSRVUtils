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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandManager;
import tk.bluetree242.discordsrvutils.leveling.LevelingManager;
import tk.bluetree242.discordsrvutils.tickets.TicketManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaceholdObjectList extends ArrayList<PlaceholdObject> {

    public static PlaceholdObjectList ofArray(PlaceholdObject... holders) {
        PlaceholdObjectList list = new PlaceholdObjectList();
        for (PlaceholdObject holder : holders) {
            list.add(holder);
        }
        return list;
    }

    public String apply(String s, Player placehold) {
        final String[] val = {s};
        Map<String, Object> variables = new HashMap<>();
        variables.put("guild", DiscordSRVUtils.get().getGuild());
        variables.put("jda", DiscordSRVUtils.get().getJDA());
        variables.put("server", Bukkit.getServer());
        variables.put("DSU", DiscordSRVUtils.get());
        variables.put("TicketManager", TicketManager.get());
        variables.put("LevelingManager", LevelingManager.get());
        variables.put("CommandManager", CommandManager.get());
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

        val[0] = PlaceholdObject.applyPlaceholders(val[0], placehold);
        val[0] = NamedValueFormatter.formatExpressions(val[0], DiscordSRVUtils.get(), variables);
        return val[0];
    }

    public String apply(String s) {
        return apply(s, null);
    }

}
