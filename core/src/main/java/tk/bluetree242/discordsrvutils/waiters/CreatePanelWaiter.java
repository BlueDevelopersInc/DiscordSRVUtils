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

package tk.bluetree242.discordsrvutils.waiters;

import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import tk.bluetree242.discordsrvutils.systems.tickets.Panel;
import tk.bluetree242.discordsrvutils.waiter.Waiter;
import tk.bluetree242.discordsrvutils.waiter.WaiterManager;

public class CreatePanelWaiter extends Waiter {
    private final Panel.Builder builder = new Panel.Builder();
    private int step = 1;
    private final TextChannel channel;
    private final User user;

    public CreatePanelWaiter(TextChannel channel, User user) {
        this.channel = channel;
        this.user = user;
    }


    public static CreatePanelWaiter getWaiter(TextChannel channel, User user) {
        for (Waiter w : WaiterManager.get().getWaiterByName("CreatePanel")) {
            CreatePanelWaiter waiter = (CreatePanelWaiter) w;
            if (waiter.getChannel().getIdLong() == channel.getIdLong()) {
                if (waiter.getUser().getIdLong() == user.getIdLong()) {
                    return waiter;
                }
            }
        }
        return null;
    }

    public User getUser() {
        return user;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int num) {
        step = num;
    }

    public Panel.Builder getBuilder() {
        return builder;
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
