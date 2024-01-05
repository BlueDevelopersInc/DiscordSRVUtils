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
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class PlaceholdObject {
    private final Map<String, Method> map = new HashMap<>();
    private final Object ob;
    private final DiscordSRVUtils core;
    protected String display;

    public PlaceholdObject(DiscordSRVUtils core, Object ob, String display) {
        this.core = core;
        this.ob = ob;
        this.display = display;
    }

    public static String applyMultiple(DiscordSRVUtils core, String s, PlaceholdObject... holders) {
        return PlaceholdObjectList.ofArray(core, holders).apply(s);
    }


    public Object getObject() {
        return ob;
    }

    public String apply(@NotNull String s, PlatformPlayer placehold) {
        return apply(s, placehold, true);
    }

    public String apply(@NotNull String s, PlatformPlayer placehold, boolean doAllowCode) {
        final String[] val = {s};
        if (ob instanceof String || ob instanceof Integer || ob instanceof Double || ob instanceof Long || ob instanceof Float) {
            if (val[0].contains("[" + display + "]")) {
                val[0] = val[0].replace("[" + display + "]", ob.toString());
            }
        } else {
            Map<String, Method> map = getHoldersMap();
            if (doAllowCode) {
                Map<String, Object> variables = new HashMap<>();
                variables.put("guild", core.getPlatform().getDiscordSRV().getMainGuild());
                variables.put("jda", core.getJDA());
                variables.put("DSU", core);
                variables.put("TicketManager", core.getTicketManager());
                variables.put("server", core.getServer().getOriginal());
                variables.put("LevelingManager", core.getLevelingManager());
                variables.put("CommandManager", core.getCommandManager());
                variables.put(display, ob);
                val[0] = NamedValueFormatter.formatExpressions(val[0], core, variables);
                val[0] = core.getPlatform().placehold(placehold, val[0]);
            }
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
                } catch (Exception ignored) {
                }
            });
        }
        return val[0];
    }

    public String apply(@NotNull String s) {
        return apply(s, null);
    }

    public Map<String, Method> getHoldersMap() {
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
