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
import org.jetbrains.annotations.NotNull;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandManager;
import tk.bluetree242.discordsrvutils.leveling.LevelingManager;
import tk.bluetree242.discordsrvutils.platform.PlatformPlayer;
import tk.bluetree242.discordsrvutils.tickets.TicketManager;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class PlaceholdObject {
    private final Map<String, Method> map = new HashMap<>();
    protected String display;
    private Object ob;

    public PlaceholdObject(Object ob, String display) {
        this.ob = ob;
        this.display = display;
    }

    public static String applyMultiple(String s, PlaceholdObject... holders) {
        return PlaceholdObjectList.ofArray(holders).apply(s);
    }


    public Object getObject() {
        return ob;
    }

    public String apply(@NotNull String s, PlatformPlayer placehold) {
        return apply(s, placehold, true);
    }

    public String apply(@NotNull String s, PlatformPlayer placehold, boolean doAllowCode) {
        Map<String, Method> map = getholdersMap();
        final String[] val = {s};
        map.forEach((key, result) -> {
            try {
                if (val[0].contains("[" + this.display + "." + key + "]")) {
                    Object invoked = result.invoke(this.getObject());
                    String value = null;
                    if (invoked != null) {
                        value = invoked.toString();
                    }
                    val[0] = val[0].replace("[" + this.display + "." + key + "]", value == null ? "null" : value);

                }
            } catch (Exception e) {
            }
        });

        val[0] = DiscordSRVUtils.get().getPlatform().placehold(placehold, s);
        if (doAllowCode) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("guild", DiscordSRVUtils.get().getGuild());
            variables.put("jda", DiscordSRVUtils.get().getJDA());
            variables.put("DSU", DiscordSRVUtils.get());
            variables.put("TicketManager", TicketManager.get());
            variables.put("server", DiscordSRVUtils.getPlatform().getServer().getOriginal());
            variables.put("LevelingManager", LevelingManager.get());
            variables.put("CommandManager", CommandManager.get());
            variables.put(display, ob);
            val[0] = NamedValueFormatter.formatExpressions(val[0], DiscordSRVUtils.get(), variables);
        }
        return val[0];
    }

    public String apply(@NotNull String s) {
        return apply(s, null);
    }

    public Map<String, Method> getholdersMap() {
        if (map.isEmpty()) {
            for (Method method : ob.getClass().getMethods()) {
                if (method.getReturnType() != void.class) {
                    if (method.getName().startsWith("get") && !method.getName().equals("get")) {
                        if (method.getParameterTypes().length == 0) {
                            String str = method.getName().replaceFirst("get", "");
                            str = str.substring(0, 1).toLowerCase() + str.substring(1);
                            map.put(str, method);
                        }
                    }
                }
            }
            return map;
        }
        return map;
    }
}
