package tk.bluetree242.discordsrvutils.config;


import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.List;

public interface Config {


    @AnnotationBasedSorter.Order(10)
    @ConfComments("#Admins who can use the bot without limits, Only IDs")
    @ConfDefault.DefaultLongs({})
    List<Long> admins();

}
