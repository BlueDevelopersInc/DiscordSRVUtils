package tk.bluetree242.discordsrvutils.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class PlaceholdObject {
    private final Map<String, Method> map = new HashMap<>();
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
            } catch (Exception e) {}
        });
        return val[0];
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
