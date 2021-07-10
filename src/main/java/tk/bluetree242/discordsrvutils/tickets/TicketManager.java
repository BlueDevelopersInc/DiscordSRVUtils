package tk.bluetree242.discordsrvutils.tickets;

import tk.bluetree242.discordsrvutils.DiscordSRVUtils;

public class TicketManager {
    private static TicketManager main;
    private DiscordSRVUtils core = DiscordSRVUtils.get();
    private static TicketManager get() {
        return main;
    }
    public static TicketManager getInstance() {
        return get();
    }

}
