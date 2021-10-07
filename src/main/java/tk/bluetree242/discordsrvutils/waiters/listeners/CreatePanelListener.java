package tk.bluetree242.discordsrvutils.waiters.listeners;


import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.embeds.Embed;
import tk.bluetree242.discordsrvutils.utils.Utils;
import tk.bluetree242.discordsrvutils.waiters.CreatePanelWaiter;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreatePanelListener extends ListenerAdapter {
    private DiscordSRVUtils core = DiscordSRVUtils.get();

    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        EmbedBuilder embed = new EmbedBuilder();
        CreatePanelWaiter waiter = CreatePanelWaiter.getWaiter(e.getChannel(), e.getAuthor());
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
                waiter.getBuilder().setName(name);
                waiter.setStep(2);
                embed.setColor(Color.ORANGE);
                embed.setDescription("**Step 2: Please mention the channel the panel should be sent to**");
                e.getChannel().sendMessage(embed.build()).queue();
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
                waiter.getBuilder().setChannelId(channel.getIdLong());
                waiter.setStep(3);
                embed.setColor(Color.ORANGE);
                embed.setDescription("**Step 3: Please Send the ID of the category opened tickets should be in**");
                e.getChannel().sendMessage(embed.build()).queue();
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
                waiter.getBuilder().setOpenedCategory(id);
                waiter.setStep(4);
                embed.setColor(Color.ORANGE);
                embed.setDescription("**Step 4: Please Send the ID of the category closed tickets should be in**");
                e.getChannel().sendMessage(embed.build()).queue();
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
                waiter.getBuilder().setClosedCategory(id);
                waiter.setStep(5);
                embed.setColor(Color.ORANGE);
                embed.setDescription("**Step 5: Please mention the roles or send ids of the roles that can view the tickets\n\nSay \"none\" For none**");
                e.getChannel().sendMessage(embed.build()).queue();
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
                    waiter.getBuilder().setAllowedRoles(rls);
                }
                waiter.expire(false);
                core.handleCF(waiter.getBuilder().create(), panel -> {
                    e.getChannel().sendMessage(Embed.success("Panel successfully created with ID " + panel.getId())).queue();
                }, failure -> {
                    core.defaultHandle(failure, e.getChannel());
                });
            }
        }
    }


}
