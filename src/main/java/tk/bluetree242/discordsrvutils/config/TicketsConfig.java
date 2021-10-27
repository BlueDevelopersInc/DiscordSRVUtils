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

    @AnnotationBasedSorter.Order(40)
    @ConfKey("ticket-banned-role")
    @ConfComments("# If user have this role they can't open a ticket")
    @ConfDefault.DefaultLong(0)
    Long ticket_banned_role();
}
