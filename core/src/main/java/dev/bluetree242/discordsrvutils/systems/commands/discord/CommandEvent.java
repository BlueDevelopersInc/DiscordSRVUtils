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

package dev.bluetree242.discordsrvutils.systems.commands.discord;


import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.embeds.Embed;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import dev.bluetree242.discordsrvutils.platform.PlatformPlayer;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionMapping;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.interactions.ReplyAction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;

@RequiredArgsConstructor()
public class CommandEvent {
    private final DiscordSRVUtils core;
    @Getter
    private final Member member;
    @Getter
    private final User author;
    @Getter
    private final MessageChannel channel;
    private final JDA jda;
    @Getter
    private final SlashCommandEvent event;
    private DSLContext connection;

    public ReplyAction reply(String content) {
        return event.reply(content);
    }

    public ReplyAction reply(MessageEmbed embed) {
        return event.replyEmbeds(embed);
    }

    public ReplyAction reply(Message msg) {
        return event.reply(msg);
    }

    public ReplyAction replyMessage(String content, PlaceholdObjectList holders, PlatformPlayer<?> placehold) {
        holders.add(new PlaceholdObject(core, getAuthor(), "user"));
        if (getChannel() instanceof TextChannel) {
            holders.add(new PlaceholdObject(core, getMember(), "member"));
            holders.add(new PlaceholdObject(core, core.getPlatform().getDiscordSRV().getMainGuild(), "guild"));
        }
        holders.add(new PlaceholdObject(core, getChannel(), "channel"));
        return reply(core.getMessageManager().getMessage(content, holders, placehold).build());
    }

    public ReplyAction replyMessage(String content, PlaceholdObjectList holders) {
        return replyMessage(content, holders, null);
    }

    public ReplyAction replyMessage(String content) {
        return replyMessage(content, new PlaceholdObjectList(core), null);
    }

    public ReplyAction replyErr(String msg) {
        return reply(Embed.error(msg));
    }

    public ReplyAction replyErr(String msg, String footer) {
        return reply(Embed.error(msg, footer));
    }

    public ReplyAction replySuccess(String msg) {
        return reply(Embed.success(msg));
    }

    public JDA getJDA() {
        return jda;
    }

    public Guild getGuild() {
        return ((TextChannel) getChannel()).getGuild();
    }

    public OptionMapping getOption(String name) {
        return getEvent().getOption(name);
    }

    public DSLContext getConnection() {
        if (!isConnOpen()) return connection = core.getDatabaseManager().jooq();
        return connection;
    }

    public boolean isConnOpen() {
        return connection != null;
    }

}
