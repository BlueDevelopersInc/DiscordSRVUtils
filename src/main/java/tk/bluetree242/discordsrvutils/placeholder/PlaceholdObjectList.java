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
                } catch (Exception e) {}
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
