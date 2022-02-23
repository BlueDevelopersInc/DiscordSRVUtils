/*
 *  LICENSE
 *  DiscordSRVUtils
 *  -------------
 *  Copyright (C) 2020 - 2021 BlueTree242
 *  -------------
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program.  If not, see
 *  <http://www.gnu.org/licenses/gpl-3.0.html>.
 *  END
 */

package tk.bluetree242.discordsrvutils.systems.tickets.listeners;


import github.scarsz.discordsrv.dependencies.jda.api.events.channel.text.TextChannelDeleteEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TicketDeleteListener extends ListenerAdapter {

    public void onTextChannelDelete(TextChannelDeleteEvent e) {
        if (DiscordSRVUtils.get().getMainConfig().bungee_mode()) return;
        DiscordSRVUtils.get().getAsyncManager().executeAsync(() -> {
            try (Connection conn = DiscordSRVUtils.get().getDatabase()) {
                PreparedStatement p1 = conn.prepareStatement("DELETE FROM tickets WHERE Channel=?");
                p1.setLong(1, e.getChannel().getIdLong());
                p1.execute();
            } catch (SQLException ex) {
                throw new UnCheckedSQLException(ex);
            }
        });
    }
}
