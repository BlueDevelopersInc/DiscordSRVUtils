package tk.bluetree242.discordsrvutils.config;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.ConfKey;

@ConfHeader("#Tickets system config.\n")
public interface TicketsConfig {
    @ConfKey("panel-message")
    @ConfDefault.DefaultString("message:panel")
    @ConfComments("#The Message of the panel users react to.")
    String panel_message();
}
