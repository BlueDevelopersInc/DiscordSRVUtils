package tk.bluetree242.discordsrvutils.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tk.bluetree242.discordsrvutils.exceptions.PlaceholdException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceholdObject {
    private Map<String, Object> map = new HashMap<>();
    private Object ob;
    protected String display;
    public PlaceholdObject(Object ob, String display) {
        this.ob = ob;
        this.display = display;
    }

    public static String applyMultiple(String s, PlaceholdObject... holders) {
        return PlaceholdObjectList.ofArray(holders).apply(s);
    }

    public static String applyPlaceholders(String s, Player player) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(player, s);
        }
        return s;
    }



    public Object getObject() {
        return ob;
    }

    public String apply(@NotNull String s)  {
        Map<String, Object> map = getholdersMap();
        final String[] val = {s};
        map.forEach((key, value) -> {
            val[0] = val[0].replace("[" + display + "."  + key + "]", value == null ? "null" : value.toString());
        });
        return val[0];
    }

    public Map<String, Object> getholdersMap() {
        if (map.isEmpty()) {
            for (Method method : ob.getClass().getMethods()) {
                if (method.getReturnType() != void.class) {
                    if (method.getName().startsWith("get") && !method.getName().equals("get")) {
                        if (method.getParameterTypes().length == 0) {
                            String str = method.getName().replaceFirst("get", "");
                            str = str.substring(0, 1).toLowerCase() + str.substring(1);
                            try {
                                map.put(str, method.invoke(ob));
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new PlaceholdException(e);
                            }
                        }
                    }
                }
            }
            return map;
        }
        return map;
    }
}
