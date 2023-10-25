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

package dev.bluetree242.discordsrvutils.waiters;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.systems.tickets.Panel;
import dev.bluetree242.discordsrvutils.waiter.Waiter;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.Interaction;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.ActionRow;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class EditPanelWaiter extends Waiter {
    private final dev.bluetree242.discordsrvutils.systems.tickets.Panel.Editor editor;
    private final TextChannel channel;
    private final User user;
    private final Interaction interaction;
    private int step = 0;
    @Setter
    private Message message;

    public EditPanelWaiter(TextChannel channel, User user, Panel.Editor editor, Interaction interaction) {
        this.channel = channel;
        this.user = user;
        this.editor = editor;
        this.interaction = interaction;
    }

    public static MessageEmbed getEmbed(boolean first) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.ORANGE);
        embed.setDescription(first ? "Please select what you would like to change: " : "Changes Added! Would you like to change anything else? Click what you would like to change, and Apply to apply previous changes");
        return embed.build();
    }

    public static List<ActionRow> getActionRows() {
        List<ActionRow> list = new ArrayList<>();
        list.add(ActionRow.of(
                Button.primary("name", "Name"),
                Button.primary("message_channel", "Message Channel"),
                Button.primary("opened_category", "Opened Category"),
                Button.primary("closed_category", "Closed Category"),
                Button.primary("allowed_roles", "Allowed Roles")
        ));
        list.add(ActionRow.of(
                Button.success("apply", "Apply"),
                Button.danger("cancel", "Cancel")
        ));
        return list;
    }

    public static EditPanelWaiter getWaiter(Message.Interaction interaction, Message message) {
        for (Waiter w : DiscordSRVUtils.get().getWaiterManager().getWaiterByName("EditPanel")) {
            EditPanelWaiter waiter = (EditPanelWaiter) w;
            if (interaction != null && waiter.getInteraction().getIdLong() == interaction.getIdLong()) return waiter;
            if (waiter.getMessage().getIdLong() == message.getIdLong()) return waiter;
        }
        return null;
    }

    public static EditPanelWaiter getWaiter(User user, TextChannel channel) {
        for (Waiter w : DiscordSRVUtils.get().getWaiterManager().getWaiterByName("EditPanel")) {
            EditPanelWaiter waiter = (EditPanelWaiter) w;
            if (waiter.getUser().getIdLong() == user.getIdLong() && waiter.getChannel().getIdLong() == channel.getIdLong())
                return waiter;
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
        return "EditPanel";
    }

    @Override
    public void whenExpired() {
        channel.sendMessage("**Cancelled: Timed Out**").queue();
    }
}
