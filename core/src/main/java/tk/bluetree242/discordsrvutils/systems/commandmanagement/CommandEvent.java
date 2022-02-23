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

package tk.bluetree242.discordsrvutils.systems.commandmanagement;


import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.exceptions.InsufficientPermissionException;
import github.scarsz.discordsrv.dependencies.jda.api.exceptions.RateLimitedException;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionMapping;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.interactions.ReplyAction;
import github.scarsz.discordsrv.dependencies.jda.internal.utils.Checks;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.embeds.Embed;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedRateLimitedException;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import tk.bluetree242.discordsrvutils.platform.PlatformPlayer;
import tk.bluetree242.discordsrvutils.utils.Utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CommandEvent {
    private final Member member;
    private final User author;
    private final MessageChannel channel;
    private final JDA jda;
    @Getter
    private final SlashCommandEvent event;


    public Member getMember() {
        return member;
    }


    public User getAuthor() {
        return author;
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public ReplyAction reply(String content) {
        return event.reply(content);
    }

    public ReplyAction reply(MessageEmbed embed) {
        return event.replyEmbeds(embed);
    }

    public ReplyAction reply(Message msg) {
        return event.reply(msg);
    }

    public ReplyAction replyMessage(String content, PlaceholdObjectList holders, PlatformPlayer placehold) {
        holders.add(new PlaceholdObject(getAuthor(), "user"));
        if (getChannel() instanceof TextChannel) {
            holders.add(new PlaceholdObject(getMember(), "member"));
            holders.add(new PlaceholdObject(getGuild(), "guild"));
        }
        holders.add(new PlaceholdObject(getChannel(), "channel"));
        return reply(DiscordSRVUtils.get().getMessageManager().getMessage(content, holders, placehold).build());
    }

    public ReplyAction replyMessage(String content, PlaceholdObjectList holders) {
        return replyMessage(content, holders, null);
    }

    public ReplyAction replyMessage(String content) {
        return replyMessage(content, new PlaceholdObjectList(), null);
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

    public CompletableFuture handleCF(CompletableFuture cf, String success, String failure) {
        Checks.notNull(cf, "CompletableFuture");
        Checks.notNull(success, "Success Message");
        Checks.notNull(failure, "Failure Message");
        cf.thenRunAsync(() -> {
            reply(Embed.success(success)).queue();
        }).handleAsync((e, x) -> {
            Exception ex = (Exception) ((Throwable) x).getCause();
            while (ex instanceof ExecutionException) ex = (Exception) ex.getCause();
            if (ex instanceof UnCheckedRateLimitedException) {
                reply(Embed.error(failure, "Rate limited. Try again in: " + Utils.getDuration(((RateLimitedException) ((UnCheckedRateLimitedException) ex).getCause()).getRetryAfter()))).queue();
            } else if (!(ex instanceof InsufficientPermissionException)) {
                reply(Embed.error(failure)).queue();
                DiscordSRVUtils.get().getErrorHandler().defaultHandle(ex);
            } else {
                InsufficientPermissionException exc = (InsufficientPermissionException) ex;
                GuildChannel chnl = DiscordSRVUtils.get().getJDA().getShardManager().getGuildChannelById(exc.getChannelId());
                reply(Embed.error(failure, "Missing " + exc.getPermission().getName() + " Permission" + (chnl == null ? "" : " In #" + chnl.getName()))).queue();
            }
            return x;
        });
        return cf;
    }


    public <H> CompletableFuture<H> handleCF(CompletableFuture<H> cf, String failure) {
        Checks.notNull(cf, "CompletableFuture");
        Checks.notNull(failure, "Failure Message");
        cf.handleAsync((e, x) -> {
            Exception ex = (Exception) ((Throwable) x).getCause();
            while (ex instanceof ExecutionException) ex = (Exception) ex.getCause();
            if (ex instanceof UnCheckedRateLimitedException) {
                reply(Embed.error(failure, "Rate limited. Try again in: " + Utils.getDuration(((RateLimitedException) ((UnCheckedRateLimitedException) ex).getCause()).getRetryAfter()))).queue();
            } else if (!(ex instanceof InsufficientPermissionException)) {
                reply(Embed.error(failure)).queue();
                DiscordSRVUtils.get().getErrorHandler().defaultHandle(ex);
            } else {
                InsufficientPermissionException exc = (InsufficientPermissionException) ex;
                GuildChannel chnl = DiscordSRVUtils.get().getJDA().getGuildChannelById(exc.getChannelId());
                exc.getPermission();
                assert chnl != null;
                reply(Embed.error(failure, " In #" + chnl.getName())).queue();
            }
            return x;
        });
        return cf;
    }

}
