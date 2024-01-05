/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2024 BlueTree242
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

package dev.bluetree242.discordsrvutils.waiters;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.systems.tickets.Panel;
import dev.bluetree242.discordsrvutils.waiter.Waiter;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import lombok.Getter;

@Getter
public class CreatePanelWaiter extends Waiter {
    private final Panel.Builder builder;
    private final TextChannel channel;
    private final User user;
    private int step = 1;

    public CreatePanelWaiter(DiscordSRVUtils core, TextChannel channel, User user) {
        builder = new Panel.Builder(core);
        this.channel = channel;
        this.user = user;
    }


    public static CreatePanelWaiter getWaiter(TextChannel channel, User user) {
        for (Waiter w : DiscordSRVUtils.get().getWaiterManager().getWaiterByName("CreatePanel")) {
            CreatePanelWaiter waiter = (CreatePanelWaiter) w;
            if (waiter.getChannel().getIdLong() == channel.getIdLong()) {
                if (waiter.getUser().getIdLong() == user.getIdLong()) {
                    return waiter;
                }
            }
        }
        return null;
    }

    public void setStep(int num) {
        step = num;
    }

    @Override
    public long getExpirationTime() {
        return getStartTime() + 180000;
    }

    @Override
    public String getWaiterName() {
        return "CreatePanel";
    }

    @Override
    public void whenExpired() {
        channel.sendMessage("**Cancelled: Timed Out**").queue();
    }
}
