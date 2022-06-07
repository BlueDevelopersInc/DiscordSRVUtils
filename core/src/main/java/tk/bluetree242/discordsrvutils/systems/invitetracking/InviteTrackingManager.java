/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2022 BlueTree242
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

package tk.bluetree242.discordsrvutils.systems.invitetracking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jooq.DSLContext;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.jooq.tables.InviteTrackingTable;
import tk.bluetree242.discordsrvutils.jooq.tables.records.InviteTrackingRecord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class InviteTrackingManager {

    private final DiscordSRVUtils core;

    @Getter
    private final Set<CachedInvite> cachedInvites = new HashSet<>();

    public UserInvites getInvites(List<InviteTrackingRecord> records, long userId) {
        List<UserInvites.Invite> invites = new ArrayList<>();
        for (InviteTrackingRecord invite : records) {
            invites.add(new UserInvites.Invite(invite.getJoinTime(), invite.getUserId(), invite.getInviterId(), invite.getLeftServer(), invite.getGuildId()));
        }
        return new UserInvites(core, userId, invites);
    }

    public UserInvites getInvites(DSLContext conn, long userId) {
        List<InviteTrackingRecord> records = conn.selectFrom(InviteTrackingTable.INVITE_TRACKING)
                .where(InviteTrackingTable.INVITE_TRACKING.INVITER_ID.eq(userId))
                .fetch();
        return getInvites(records, userId);
    }

    public void addInvite(DSLContext conn, long userId, long inviterId, long guildId) {
        //check if it's already there but previous time, to prevent duplicates
        InviteTrackingRecord record = conn.selectFrom(InviteTrackingTable.INVITE_TRACKING)
                .where(InviteTrackingTable.INVITE_TRACKING.INVITER_ID.eq(inviterId))
                .and(InviteTrackingTable.INVITE_TRACKING.USER_ID.eq(userId))
                .and(InviteTrackingTable.INVITE_TRACKING.GUILD_ID.eq(guildId))
                .fetchOne();
        if (record == null) {
            //add it
            conn.insertInto(InviteTrackingTable.INVITE_TRACKING)
                    .set(InviteTrackingTable.INVITE_TRACKING.INVITER_ID, inviterId)
                    .set(InviteTrackingTable.INVITE_TRACKING.GUILD_ID, guildId)
                    .set(InviteTrackingTable.INVITE_TRACKING.USER_ID, userId)
                    .set(InviteTrackingTable.INVITE_TRACKING.JOIN_TIME, System.currentTimeMillis())
                    .set(InviteTrackingTable.INVITE_TRACKING.LEFT_SERVER, false)
                    .execute();
        } else {
            //modify old one
            conn.update(InviteTrackingTable.INVITE_TRACKING)
                    .set(InviteTrackingTable.INVITE_TRACKING.INVITER_ID, inviterId)
                    .set(InviteTrackingTable.INVITE_TRACKING.GUILD_ID, guildId)
                    .set(InviteTrackingTable.INVITE_TRACKING.USER_ID, userId)
                    .set(InviteTrackingTable.INVITE_TRACKING.JOIN_TIME, System.currentTimeMillis())
                    .set(InviteTrackingTable.INVITE_TRACKING.LEFT_SERVER, false)
                    .where(InviteTrackingTable.INVITE_TRACKING.INVITER_ID.eq(inviterId))
                    .and(InviteTrackingTable.INVITE_TRACKING.USER_ID.eq(userId))
                    .and(InviteTrackingTable.INVITE_TRACKING.GUILD_ID.eq(guildId))
                    .execute();
        }
    }

    public void leftServer(DSLContext conn, long userId) {
        conn.update(InviteTrackingTable.INVITE_TRACKING)
                .set(InviteTrackingTable.INVITE_TRACKING.LEFT_SERVER, true)
                .where(InviteTrackingTable.INVITE_TRACKING.USER_ID.eq(userId))
                .execute();
    }

    @AllArgsConstructor
    public static class CachedInvite {
        @Getter
        private final String code;
        @Getter
        private final long userId;
        @Getter
        private final long guildId;
        @Getter @Setter
        private int uses;
    }
}
