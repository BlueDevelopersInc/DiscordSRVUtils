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
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.react.MessageReactionAddEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.MessageReaction;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.embeds.Embed;
import tk.bluetree242.discordsrvutils.utils.Utils;
import tk.bluetree242.discordsrvutils.waiters.CreatePanelWaiter;
import tk.bluetree242.discordsrvutils.waiters.EditPanelWaiter;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EditPanelListener extends ListenerAdapter {
    private DiscordSRVUtils core = DiscordSRVUtils.get();

    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        EmbedBuilder embed = new EmbedBuilder();
        EditPanelWaiter waiter = EditPanelWaiter.getWaiter(e.getChannel(), e.getAuthor());
        if (waiter != null) {
            if (e.getMessage().getContentDisplay().equalsIgnoreCase("cancel")) {
                waiter.expire(false);
                e.getChannel().sendMessage(Embed.error("Ok, Cancelled")).queue();
                return;
            }
            if (waiter.getStep() ==1) {
                String name = e.getMessage().getContentDisplay();
                if (name.length() > 32) {
                    e.getChannel().sendMessage(Embed.error("Name cannot be more than 32 characters. Try Again.")).queue();
                    return;
                }
                waiter.getEditor().setName(name);
                waiter.setStep(0);
                e.getChannel().sendMessage(EditPanelWaiter.getEmbed()).content("Ok, Want to edit anything else?").queue(msg -> {
                    EditPanelWaiter.addReactions(msg);
                    waiter.setMessage(msg);
                });
            } else if (waiter.getStep() ==2) {
                TextChannel channel = e.getMessage().getMentionedChannels().get(0);
                if (channel == null) {
                    e.getChannel().sendMessage(Embed.error("You did not mention a channel. Please try again")).queue();
                    return;
                }
                if (channel.getGuild().getIdLong() != core.getGuild().getIdLong()) {
                    e.getChannel().sendMessage(Embed.error("Channel cannot be outside of the main guild")).queue();
                    return;
                }
                waiter.getEditor().setChannelId(channel.getIdLong());
                waiter.setStep(0);
                e.getChannel().sendMessage(EditPanelWaiter.getEmbed()).content("Ok, Want to edit anything else?").queue(msg -> {
                    EditPanelWaiter.addReactions(msg);
                    waiter.setMessage(msg);
                });
            } else if (waiter.getStep() == 3) {
                if (!Utils.isLong(e.getMessage().getContentDisplay())) {
                    e.getChannel().sendMessage(Embed.error("This is not even a valid id, try again")).queue();
                    return;
                }
                long id = Long.parseLong(e.getMessage().getContentDisplay());
                if (core.getGuild().getCategoryById(id) == null) {
                    e.getChannel().sendMessage(Embed.error("Category not found, is it inside this guild? Try Again")).queue();
                    return;
                }
                waiter.getEditor().setOpenedCategory(id);
                waiter.setStep(0);
                e.getChannel().sendMessage(EditPanelWaiter.getEmbed()).content("Ok, Want to edit anything else?").queue(msg -> {
                    EditPanelWaiter.addReactions(msg);
                    waiter.setMessage(msg);
                });
            } else if (waiter.getStep() == 4) {
                if (!Utils.isLong(e.getMessage().getContentDisplay())) {
                    e.getChannel().sendMessage(Embed.error("This is not even a valid id, try again")).queue();
                    return;
                }
                long id = Long.parseLong(e.getMessage().getContentDisplay());
                if (core.getGuild().getCategoryById(id) == null) {
                    e.getChannel().sendMessage(Embed.error("Category not found, is it inside this guild? Try Again")).queue();
                    return;
                }
                waiter.getEditor().setClosedCategory(id);
                waiter.setStep(0);
                e.getChannel().sendMessage(EditPanelWaiter.getEmbed()).content("Ok, Want to edit anything else?").queue(msg -> {
                    EditPanelWaiter.addReactions(msg);
                    waiter.setMessage(msg);
                });
            } else if (waiter.getStep() == 5) {
                if (!e.getMessage().getContentDisplay().equalsIgnoreCase("none")) {
                    List<Role> roles = new ArrayList<>();
                    if (!e.getMessage().getMentionedRoles().isEmpty()) {

                        roles = e.getMessage().getMentionedRoles();
                    } else {
                        String[] roleIds = e.getMessage().getContentDisplay().split(" ");
                        for (String role : roleIds) {
                            if (!Utils.isLong(role)) {
                                e.getChannel().sendMessage(Embed.error("Invalid, Try Again")).queue();
                                return;
                            }
                            if (e.getGuild().getRoleById(Long.parseLong(role)) == null) {
                                e.getChannel().sendMessage(Embed.error("One of the Ids is invalid, try again")).queue();
                                return;
                            }
                            roles.add(e.getGuild().getRoleById(Long.parseLong(role)));
                        }
                    }

                    for (Role role : roles) {
                        if (role.getGuild().getIdLong() != e.getGuild().getIdLong()) {
                            e.getChannel().sendMessage(Embed.error("One of the roles you mentioned is not in this guild")).queue();
                            return;
                        }
                    }
                    Set<Long> rls = new HashSet<>();
                    roles.forEach(r -> {rls.add(r.getIdLong());});
                    waiter.getEditor().setAllowedRoles(rls);
                    waiter.setStep(0);
                    e.getChannel().sendMessage(EditPanelWaiter.getEmbed()).content("Ok, Want to edit anything else?").queue(msg -> {
                        EditPanelWaiter.addReactions(msg);
                        waiter.setMessage(msg);
                    });
                }
            }
        }
    }

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        core.executeAsync(() -> {
            EditPanelWaiter waiter = EditPanelWaiter.getWaiter(e.getChannel(), e.getUser());
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.ORANGE);
            if (waiter == null) return;
            if (waiter.getMessage().getIdLong() == e.getMessageIdLong()) {

                String name = e.getReactionEmote().getName();
                if (name.equals("✅")) {
                    waiter.expire(false);
                    core.handleCF(waiter.getEditor().apply(), panel -> {
                        if (panel == null) {
                            e.getChannel().sendMessage(Embed.error("Something unexpected happened, please contact the devs")).queue();
                        } else {
                            e.getChannel().sendMessage(Embed.success("Successfully applied changes")).queue();
                        }
                    }, err -> {core.defaultHandle(err, e.getChannel());});
                } else if (name.equals("❌")) {
                    waiter.expire(false);
                    e.getChannel().sendMessage(Embed.error("Ok, Cancelled")).queue();
                } else if (waiter.getStep() != 0) {
                    e.getReaction().removeReaction(e.getUser()).queue();
                }
                else if (name.equals("1️⃣")) {

                     waiter.setStep(1);
                     embed.setDescription("Please send the new name for the panel");
                     e.getChannel().sendMessage(embed.build()).queue();
                } else if (name.equals("2️⃣")) {
                    waiter.setStep(2);
                    embed.setDescription("Please mention the new channel for the panel");
                    e.getChannel().sendMessage(embed.build()).queue();
                } else if (name.equals("3️⃣")) {
                    waiter.setStep(3);
                    embed.setDescription("Please send the ID of the Opened Category for the panel");
                    e.getChannel().sendMessage(embed.build()).queue();
                } else if (name.equals("4️⃣")) {
                    waiter.setStep(4);
                    embed.setDescription("Please send the ID of the Closed Category for the panel");
                    e.getChannel().sendMessage(embed.build()).queue();
                } else if (name.equals("5️⃣")) {
                    waiter.setStep(5);
                    embed.setDescription(" Please mention the roles or send ids of the roles that can view the panel tickets\n\nSay \"none\" For none");
                    e.getChannel().sendMessage(embed.build()).queue();
                }
            }
        });
    }
}
