package tk.bluetree242.discordsrvutils.placeholder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

public class PlaceholdObjectList extends ArrayList<PlaceholdObject> {

    public static PlaceholdObjectList ofArray(PlaceholdObject... holders) {
        PlaceholdObjectList list = new PlaceholdObjectList();
        for (PlaceholdObject holder : holders) {
            list.add(holder);
        }
        return list;
    }

    public String apply(String s) {
        final String[] val = {s};
        for (PlaceholdObject holder : this) {
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
        return val[0];
    }

}
