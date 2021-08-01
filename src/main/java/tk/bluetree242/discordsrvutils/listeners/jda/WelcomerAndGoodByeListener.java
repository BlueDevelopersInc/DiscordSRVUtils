package tk.bluetree242.discordsrvutils.listeners.jda;

import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;

import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberJoinEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberLeaveEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberRemoveEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.messages.MessageManager;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;

public class WelcomerAndGoodByeListener extends ListenerAdapter {
    private DiscordSRVUtils core = DiscordSRVUtils.get();

    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        core.executeAsync(() -> {
            if (!e.getUser().isBot()) {
                if (core.getMainConfig().welcomer_enabled()) {
                    MessageChannel channel = core.getMainConfig().welcomer_dm_user() ? e.getUser().openPrivateChannel().complete() : core.getChannel(core.getMainConfig().welcomer_channel());
                    if (channel == null) {
                        core.severe("No Text Channel was found with ID " + core.getMainConfig().welcomer_channel() + ". Join Message was not sent for " + e.getUser().getAsTag());
                        return;
                    }
                    PlaceholdObjectList holders = new PlaceholdObjectList();
                    holders.add(new PlaceholdObject(e.getUser(), "user"));
                    holders.add(new PlaceholdObject(e.getGuild(), "guild"));
                    holders.add(new PlaceholdObject(e.getMember(), "member"));
                    channel.sendMessage(MessageManager.get().getMessage(core.getMainConfig().welcomer_message(), holders, null).build()).queue();
                }
            }
        });
    }
    public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
        core.executeAsync(() -> {
            if (!e.getUser().isBot()) {
                if (core.getMainConfig().goodbye_enabled()) {
                    MessageChannel channel = core.getChannel(core.getMainConfig().goodbye_channel());
                    if (channel == null) {
                        core.severe("No Text Channel was found with ID " + core.getMainConfig().goodbye_channel() + ". Leave Message was not sent for " + e.getUser().getAsTag());
                        return;
                    }
                    PlaceholdObjectList holders = new PlaceholdObjectList();
                    holders.add(new PlaceholdObject(e.getUser(), "user"));
                    holders.add(new PlaceholdObject(e.getGuild(), "guild"));
                    holders.add(new PlaceholdObject(e.getMember(), "member"));
                    channel.sendMessage(MessageManager.get().getMessage(core.getMainConfig().goodbye_message(), holders, null).build()).queue();
                }
            }
        });
    }
}
