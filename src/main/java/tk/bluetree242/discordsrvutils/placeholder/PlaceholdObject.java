package tk.bluetree242.discordsrvutils.placeholder;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.scarsz.discordsrv.util.NamedValueFormatter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandManager;
import tk.bluetree242.discordsrvutils.leveling.LevelingManager;
import tk.bluetree242.discordsrvutils.tickets.TicketManager;

import java.io.Console;
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
            String to = s.replace("&", "** ** *");
            String fina = PlaceholderAPI.setPlaceholders(player, to);
            return fina.replace("** ** *", "&");
        }
        return s;
    }



    public Object getObject() {
        return ob;
    }

    public String apply(@NotNull String s, Player placehold)  {
        return apply(s, placehold, true);
    }

    public String apply(@NotNull String s, Player placehold, boolean doAllowCode)  {
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

        val[0] = PlaceholdObject.applyPlaceholders(val[0], placehold);
        if (doAllowCode) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("guild", DiscordSRVUtils.get().getGuild());
            variables.put("jda", DiscordSRVUtils.get().getJDA());
            variables.put("server", Bukkit.getServer());
            variables.put("DSU", DiscordSRVUtils.get());
            variables.put("TicketManager", TicketManager.get());
            variables.put("LevelingManager", LevelingManager.get());
            variables.put("CommandManager", CommandManager.get());
            variables.put(display, ob);
            val[0] = NamedValueFormatter.formatExpressions(val[0], DiscordSRVUtils.get(), variables);
        }
        return val[0];
    }
    public String apply(@NotNull String s)  {
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
