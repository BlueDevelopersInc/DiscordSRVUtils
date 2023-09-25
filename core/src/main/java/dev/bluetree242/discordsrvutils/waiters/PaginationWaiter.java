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

package dev.bluetree242.discordsrvutils.waiters;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.waiter.Waiter;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Emoji;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.Interaction;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.ActionRow;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.interactions.ReplyAction;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PaginationWaiter extends Waiter {
    private final DiscordSRVUtils core;
    private final MessageChannel channel;
    private final List<MessageEmbed> embeds;
    private final User user;
    @Getter
    private final Interaction interaction;
    private int currentPage = 1;

    public PaginationWaiter(DiscordSRVUtils core, MessageChannel channel, List<MessageEmbed> embeds, User user, Interaction interaction) {
        this.core = core;
        this.embeds = embeds;
        this.user = user;
        this.channel = channel;
        this.interaction = interaction;
    }

    public static ReplyAction setupMessage(ReplyAction action, int pageCount) {
        action.addActionRows(getActionRow(pageCount, 1));
        return action;
    }

    public static ActionRow getActionRow(int pageCount, int page) {
        List<Button> buttons = new ArrayList<>();
        if (page == 1) {
            buttons.add(Button.primary("backward", Emoji.fromUnicode("▶️")).asDisabled());
        } else {
            buttons.add(Button.primary("backward", Emoji.fromUnicode("▶️")).asEnabled());
        }
        if (page == pageCount) {
            buttons.add(Button.primary("forward", Emoji.fromUnicode("▶️")).asDisabled());
        } else {
            buttons.add(Button.primary("forward", Emoji.fromUnicode("▶️")).asEnabled());
        }
        buttons.add(Button.danger("delete", Emoji.fromUnicode("\uD83D\uDDD1️")));
        return ActionRow.of(buttons);
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public List<MessageEmbed> getEmbeds() {
        return embeds;
    }

    public User getUser() {
        return user;
    }

    public int getPage() {
        return currentPage;
    }

    public PaginationWaiter setPage(int page) {
        this.currentPage = page;
        return this;
    }

    @Override
    public long getExpirationTime() {
        return getStartTime() + 600000;
    }

    @Override
    public String getWaiterName() {
        return "PaginationWaiter";
    }

    @Override
    public void whenExpired() {
        interaction.getHook().editOriginal(":timer: Timed out").setActionRows().setEmbeds(Collections.emptyList()).queue();
    }


}
