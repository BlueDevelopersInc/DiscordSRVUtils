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

package tk.bluetree242.discordsrvutils.waiters.listeners;


import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.embeds.Embed;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;
import tk.bluetree242.discordsrvutils.systems.tickets.Panel;
import tk.bluetree242.discordsrvutils.utils.Utils;
import tk.bluetree242.discordsrvutils.waiters.CreatePanelWaiter;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class CreatePanelListener extends ListenerAdapter {
    private final DiscordSRVUtils core;

    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent e) {
        core.getAsyncManager().executeAsync(() -> {
            EmbedBuilder embed = new EmbedBuilder();
            CreatePanelWaiter waiter = CreatePanelWaiter.getWaiter(e.getChannel(), e.getAuthor());
            if (waiter != null) {
                if (e.getMessage().getContentDisplay().equalsIgnoreCase("cancel")) {
                    waiter.expire(false);
                    e.getChannel().sendMessageEmbeds(Embed.error("Ok, Cancelled")).queue();
                    return;
                }
                if (waiter.getStep() == 1) {
                    String name = e.getMessage().getContentDisplay();
                    if (name.length() > 32) {
                        e.getChannel().sendMessageEmbeds(Embed.error("Name cannot be more than 32 characters. Try Again.")).queue();
                        return;
                    }
                    waiter.getBuilder().setName(name);
                    waiter.setStep(2);
                    embed.setColor(Color.ORANGE);
                    embed.setDescription("**Step 2: Please mention the channel the panel should be sent to**");
                    e.getChannel().sendMessageEmbeds(embed.build()).queue();
                } else if (waiter.getStep() == 2) {
                    if (e.getMessage().getMentionedChannels().isEmpty()) {
                        e.getChannel().sendMessageEmbeds(Embed.error("You did not mention a channel. Please try again")).queue();
                        return;
                    }
                    TextChannel channel = e.getMessage().getMentionedChannels().get(0);
                    if (core.getPlatform().getDiscordSRV().getMainGuild().getIdLong() != core.getPlatform().getDiscordSRV().getMainGuild().getIdLong()) {
                        e.getChannel().sendMessageEmbeds(Embed.error("Channel cannot be outside of the main guild")).queue();
                        return;
                    }
                    waiter.getBuilder().setChannelId(channel.getIdLong());
                    waiter.setStep(3);
                    embed.setColor(Color.ORANGE);
                    embed.setDescription("**Step 3: Please Send the ID of the category opened tickets should be in**");
                    e.getChannel().sendMessageEmbeds(embed.build()).queue();
                } else if (waiter.getStep() == 3) {
                    if (!Utils.isLong(e.getMessage().getContentDisplay())) {
                        e.getChannel().sendMessageEmbeds(Embed.error("This is not even a valid id, try again")).queue();
                        return;
                    }
                    long id = Long.parseLong(e.getMessage().getContentDisplay());
                    if (core.getPlatform().getDiscordSRV().getMainGuild().getCategoryById(id) == null) {
                        e.getChannel().sendMessageEmbeds(Embed.error("Category not found, is it inside this guild? Try Again")).queue();
                        return;
                    }
                    waiter.getBuilder().setOpenedCategory(id);
                    waiter.setStep(4);
                    embed.setColor(Color.ORANGE);
                    embed.setDescription("**Step 4: Please Send the ID of the category closed tickets should be in**");
                    e.getChannel().sendMessageEmbeds(embed.build()).queue();
                } else if (waiter.getStep() == 4) {
                    if (!Utils.isLong(e.getMessage().getContentDisplay())) {
                        e.getChannel().sendMessageEmbeds(Embed.error("This is not even a valid id, try again")).queue();
                        return;
                    }
                    long id = Long.parseLong(e.getMessage().getContentDisplay());
                    if (core.getPlatform().getDiscordSRV().getMainGuild().getCategoryById(id) == null) {
                        e.getChannel().sendMessageEmbeds(Embed.error("Category not found, is it inside this guild? Try Again")).queue();
                        return;
                    }
                    waiter.getBuilder().setClosedCategory(id);
                    waiter.setStep(5);
                    embed.setColor(Color.ORANGE);
                    embed.setDescription("**Step 5: Please mention the roles or send ids of the roles that can view the tickets\n\nSay \"none\" For none**");
                    e.getChannel().sendMessageEmbeds(embed.build()).queue();
                } else if (waiter.getStep() == 5) {
                    if (!e.getMessage().getContentDisplay().equalsIgnoreCase("none")) {
                        List<Role> roles = new ArrayList<>();
                        if (!e.getMessage().getMentionedRoles().isEmpty()) {

                            roles = e.getMessage().getMentionedRoles();
                        } else {
                            String[] roleIds = e.getMessage().getContentDisplay().split(" ");
                            for (String role : roleIds) {
                                if (!Utils.isLong(role)) {
                                    e.getChannel().sendMessageEmbeds(Embed.error("Invalid, Try Again")).queue();
                                    return;
                                }
                                if (core.getPlatform().getDiscordSRV().getMainGuild().getRoleById(Long.parseLong(role)) == null) {
                                    e.getChannel().sendMessageEmbeds(Embed.error("One of the Ids is invalid, try again")).queue();
                                    return;
                                }
                                roles.add(core.getPlatform().getDiscordSRV().getMainGuild().getRoleById(Long.parseLong(role)));
                            }
                        }

                        for (Role role : roles) {
                            if (core.getPlatform().getDiscordSRV().getMainGuild().getIdLong() != core.getPlatform().getDiscordSRV().getMainGuild().getIdLong()) {
                                e.getChannel().sendMessageEmbeds(Embed.error("One of the roles you mentioned is not in this guild")).queue();
                                return;
                            }
                        }
                        Set<Long> rls = new HashSet<>();
                        roles.forEach(r -> {
                            rls.add(r.getIdLong());
                        });
                        waiter.getBuilder().setAllowedRoles(rls);
                    }
                    waiter.expire(false);
                    try (Connection conn = core.getDatabaseManager().getConnection()) {
                        Panel panel = waiter.getBuilder().create(core.getDatabaseManager().jooq(conn));
                        e.getChannel().sendMessageEmbeds(Embed.success("Panel created with id " + panel.getId())).queue();
                    } catch (SQLException ex) {
                        core.getErrorHandler().defaultHandle(ex, e.getChannel());
                    }
                }
            }
        });

    }


}
