package tk.bluetree242.discordsrvutils.waiters.listeners;

import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.GuildChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.react.MessageReactionAddEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.embeds.Embed;
import tk.bluetree242.discordsrvutils.waiter.Waiter;
import tk.bluetree242.discordsrvutils.waiter.WaiterManager;
import tk.bluetree242.discordsrvutils.waiters.PaginationWaiter;

public class PaginationListener extends ListenerAdapter {


    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        DiscordSRVUtils.get().executeAsync(() -> {
            boolean backward = e.getReactionEmote().getName().equals("⏪");
            PaginationWaiter waiter = getWaiter(e.getMessageIdLong(), e.getUser().getIdLong());
            if (waiter != null) {
                if (waiter.getUser().getIdLong() != e.getUser().getIdLong()) {
                    if (e.getChannel() instanceof TextChannel && e.getGuild().getSelfMember().hasPermission((GuildChannel) e.getChannel(), Permission.MESSAGE_MANAGE)) {
                        e.getReaction().removeReaction(e.getUser()).submit();
                    }
                    return;
                }
                if (e.getReactionEmote().getName().equals("🗑️")) {
                    waiter.expire(false);
                    if (e.getChannel() instanceof TextChannel && e.getGuild().getSelfMember().hasPermission((GuildChannel) e.getChannel(), Permission.MESSAGE_MANAGE))
                        waiter.getMessage().clearReactions().queue();

                    waiter.getMessage().editMessage("Cancelled by user.").override(true).queue();
                    return;
                }
                if (e.getChannel() instanceof TextChannel && e.getGuild().getSelfMember().hasPermission((GuildChannel) e.getChannel(), Permission.MESSAGE_MANAGE)) {
                    e.getReaction().removeReaction(e.getUser()).submit();
                }
                int page = waiter.getPage() + (backward ? (-1) : (1));
                if (page == 0) return;
                if (waiter.getEmbeds().size() < page) return;
                MessageEmbed embed = waiter.getEmbeds().get(page - 1);
                if (embed == null) return;
                waiter.getMessage().editMessage(embed).submit();
                waiter.setPage(page);

            }
        });
    }

    public PaginationWaiter getWaiter(long message, long userID) {
        for (Waiter w : WaiterManager.get().getWaiterByName("PaginationWaiter")) {
            PaginationWaiter waiter = (PaginationWaiter) w;
            if (waiter.getMessage().getIdLong() == message) {
                if (waiter.getUser().getIdLong() == userID)
                return waiter;
            }
        }
        return null;
    }

    /*public void onButtonClick(ButtonClickEvent e) {
        Renon.get().getThreadPool().execute(() -> {
            boolean backward = e.getButton().getId().equals("backward");
            PaginationWaiter waiter = getWaiter(e.getMessageIdLong());
            if (waiter != null) {
                if (waiter.getUser().getIdLong() != e.getUser().getIdLong()) {
                    e.replyEmbeds(Embed.error("Only the person who triggered this pagination can navigate.")).setEphemeral(true).queue();
                    return;
                }
                e.deferEdit().queue();
                if (e.getButton().getId().equals("delete")) {
                    waiter.expire(false);
                    waiter.getMessage().editMessage("Cancelled by user.").setActionRows().override(true).queue();
                    return;
                }
                int page = waiter.getPage() + (backward ? (-1) : (1));
                if (page == 0) return;
                if (waiter.getEmbeds().size() < page) return;
                MessageEmbed embed = waiter.getEmbeds().get(page - 1);
                if (embed == null) return;
                waiter.setPage(page);
                waiter.getMessage().editMessage(embed).setActionRows(waiter.getActionRow()).submit();

            }
        });
    }

     */


}
