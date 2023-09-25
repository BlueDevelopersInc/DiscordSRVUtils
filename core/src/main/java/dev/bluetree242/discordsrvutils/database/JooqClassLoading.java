/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2023 BlueTree242
 * -------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * END
 */

package dev.bluetree242.discordsrvutils.database;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.jooq.tables.InviteTrackingTable;
import dev.bluetree242.discordsrvutils.jooq.tables.LevelingTable;
import dev.bluetree242.discordsrvutils.jooq.tables.SuggestionsTable;
import dev.bluetree242.discordsrvutils.jooq.tables.TicketsTable;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

@RequiredArgsConstructor
public class JooqClassLoading {

    private final DiscordSRVUtils core;

    public void preInitializeJooqClasses() {
        long startNanos = System.nanoTime();
        //this forces jvm to load the classes
        DSLContext context = core.getDatabaseManager().newRenderOnlyJooq();

        context
                .insertInto(TicketsTable.TICKETS)
                .set(TicketsTable.TICKETS.CHANNEL, 0L)
                .getSQL();
        context
                .insertInto(SuggestionsTable.SUGGESTIONS)
                .set(SuggestionsTable.SUGGESTIONS.MESSAGEID, 0L)
                .getSQL();

        context.selectFrom(InviteTrackingTable.INVITE_TRACKING)
                .where(InviteTrackingTable.INVITE_TRACKING.LEFT_SERVER.eq(true))
                .and(InviteTrackingTable.INVITE_TRACKING.USER_ID.eq(0L))
                .getSQL();

        context.update(LevelingTable.LEVELING)
                .set(LevelingTable.LEVELING.DISCORDMESSAGES, 0)
                .set(LevelingTable.LEVELING.LEVEL, 20000000)
                .getSQL();
        long elapsedMillis = (System.nanoTime() - startNanos) / 1_000_000L;
        if (elapsedMillis >= 1000) {
            String elapsedSeconds = String.format("%.2f", ((double) elapsedMillis) / 1000D); //more user readable
            core.getLogger().info("Pre-Loaded JOOQ classes in " + elapsedSeconds + " Seconds.");
        }
    }
}
