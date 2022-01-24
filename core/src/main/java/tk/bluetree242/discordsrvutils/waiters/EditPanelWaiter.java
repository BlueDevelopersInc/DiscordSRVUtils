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

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import tk.bluetree242.discordsrvutils.systems.tickets.Panel;
import tk.bluetree242.discordsrvutils.waiter.Waiter;
import tk.bluetree242.discordsrvutils.waiter.WaiterManager;

import java.awt.*;

public class EditPanelWaiter extends Waiter {
    private final Panel.Editor editor;
    private int step = 0;
    private final TextChannel channel;
    private final User user;
    private Message msg;

    public EditPanelWaiter(TextChannel channel, User user, Panel.Editor editor, Message msg) {
        this.channel = channel;
        this.user = user;
        this.editor = editor;
        this.msg = msg;
    }

    public static MessageEmbed getEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.ORANGE);
        embed.setDescription(String.join("\n", new String[]{
                "1️⃣ Name",
                "2️⃣ Message Channel",
                "3️⃣ Opened Category",
                "4️⃣ Closed Category",
                "5️⃣ Allowed Roles",
                "✅ Finished and apply changes",
                "❌ Cancel"
        }));
        return embed.build();
    }

    public static void addReactions(Message msg) {
        msg.addReaction("1️⃣").queue();
        msg.addReaction("2️⃣").queue();
        msg.addReaction("3️⃣").queue();
        msg.addReaction("4️⃣").queue();
        msg.addReaction("5️⃣").queue();
        msg.addReaction("✅").queue();
        msg.addReaction("❌").queue();
    }

    public static EditPanelWaiter getWaiter(TextChannel channel, User user) {
        for (Waiter w : WaiterManager.get().getWaiterByName("EditPanel")) {
            EditPanelWaiter waiter = (EditPanelWaiter) w;
            if (waiter.getChannel().getIdLong() == channel.getIdLong()) {
                if (waiter.getUser().getIdLong() == user.getIdLong()) {
                    return waiter;
                }
            }
        }
        return null;
    }

    public Message getMessage() {
        return msg;
    }

    public void setMessage(Message msg) {
        this.msg = msg;
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

    public Panel.Editor getEditor() {
        return editor;
    }

    @Override
    public long getExpirationTime() {
        return getStartTime() + 180000;
    }

    @Override
    public String getWaiterName() {
        return "EditPanel";
    }

    @Override
    public void whenExpired() {
        channel.sendMessage("**Cancelled: Timed Out**").queue();
    }
}
