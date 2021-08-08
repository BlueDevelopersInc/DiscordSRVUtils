package tk.bluetree242.discordsrvutils.config;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

@ConfHeader("#Tickets system config.\n")
public interface TicketsConfig {
    @ConfKey("panel-message")
    @AnnotationBasedSorter.Order(10)
    @ConfDefault.DefaultString("message:panel")
    @ConfComments("#The Message of the panel users react to.")
    String panel_message();

    @ConfKey("ticket-opened-message")
    @AnnotationBasedSorter.Order(20)
    @ConfDefault.DefaultString("message:ticket-open")
    @ConfComments("#Message to send in ticket when its opened")
    String ticket_opened_message();

    @ConfKey("ticket-closed-message")
    @AnnotationBasedSorter.Order(30)
    @ConfDefault.DefaultString("message:ticket-close")
    @ConfComments("#Message to send in ticket when its closed")
    String ticket_closed_message();

    @ConfKey("ticket-reopen-message")
    @AnnotationBasedSorter.Order(30)
    @ConfDefault.DefaultString("message:ticket-reopen")
    @ConfComments("#Message to send in ticket when its reopened")
    String ticket_reopen_message();
}
