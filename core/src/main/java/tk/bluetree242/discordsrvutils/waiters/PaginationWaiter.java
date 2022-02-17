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

import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.MessageAction;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.waiter.Waiter;

import java.util.List;

public class PaginationWaiter extends Waiter {

    private final MessageChannel channel;
    private final List<MessageEmbed> embeds;
    private final User user;
    private Message message;
    private int currentPage = 1;

    public PaginationWaiter(MessageChannel channel, List<MessageEmbed> embeds, User user) {
        this.embeds = embeds;
        this.user = user;
        this.channel = channel;


        channel.sendMessage(embeds.get(0)).queue(msg -> {
            this.message = msg;
            msg.addReaction("⏪").queue();
            msg.addReaction("⏩").queue();
            msg.addReaction("\uD83D\uDDD1️").queue();
        });

    }

    public static MessageAction setupMessage(MessageAction action) {
        //action.setActionRows(ActionRow.of((Button.success("backward", Emoji.BACKWARD.toJDAEmoji())).asDisabled(), Button.success("forward", Emoji.FORWARD.toJDAEmoji()),Button.danger("delete", Emoji.DELETE.toJDAEmoji())));
        return action;
    }

   /* public ActionRow getActionRow() {
        int page = currentPage;
        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.danger("delete", Emoji.DELETE.toJDAEmoji()));
        if (page == 1) {
            buttons.add(Button.primary("backward", Emoji.BACKWARD.toJDAEmoji()).asDisabled());
        } else {
            buttons.add(Button.primary("backward", Emoji.BACKWARD.toJDAEmoji()).asEnabled());
        }
        if (page == getEmbeds().size()) {
            buttons.add(Button.primary("forward", Emoji.FORWARD.toJDAEmoji()).asDisabled());
        } else {
            buttons.add(Button.primary("forward", Emoji.FORWARD.toJDAEmoji()).asEnabled());
        }
        return ActionRow.of(buttons);
    }

    */

    public Message getMessage() {
        return message;
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
        message.editMessage(":timer: Timed out")/*setActionRows()*/.override(true).queue();
        if (message.getChannel() instanceof TextChannel && DiscordSRVUtils.get().getGuild().getSelfMember().hasPermission((GuildChannel) message.getChannel(), Permission.MESSAGE_MANAGE))
            message.clearReactions().queue();
    }


}
