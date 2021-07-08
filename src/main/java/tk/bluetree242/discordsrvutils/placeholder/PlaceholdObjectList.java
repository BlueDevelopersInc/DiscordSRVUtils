package tk.bluetree242.discordsrvutils.placeholder;

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
            Map<String, Object> map = holder.getholdersMap();
            map.forEach((key, value) -> {
                val[0] = val[0].replace("[" + holder.display + "."  + key + "]", value == null ? "null" : value.toString());
            });
        }
        return val[0];
    }

}
