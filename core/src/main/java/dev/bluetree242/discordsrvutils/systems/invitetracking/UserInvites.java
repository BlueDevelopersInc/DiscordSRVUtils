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

package dev.bluetree242.discordsrvutils.systems.invitetracking;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserInvites {
    private final DiscordSRVUtils core;
    @Getter
    private final long userId;
    @Getter
    private final List<Invite> invites;

    public int getTotalInvites() {
        return invites.size();
    }

    public int getStayedInvites() {
        return (int) invites.stream().filter(i -> !i.isLeft()).count();
    }

    public int getLeftInvites() {
        return (int) invites.stream().filter(Invite::isLeft).count();
    }

    @Getter
    @RequiredArgsConstructor
    public static class Invite {
        private final long joinTime;
        private final long userId;
        private final long inviterId;
        private final boolean left;
        private final long guildId;
    }
}
