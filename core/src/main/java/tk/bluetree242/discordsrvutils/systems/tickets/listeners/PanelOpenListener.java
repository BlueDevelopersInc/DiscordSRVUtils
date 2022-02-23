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

package tk.bluetree242.discordsrvutils.systems.tickets.listeners;


import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.ButtonClickEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.interactions.ReplyAction;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;

public class PanelOpenListener extends ListenerAdapter {
    private final DiscordSRVUtils core = DiscordSRVUtils.get();

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        if (core.getMainConfig().bungee_mode()) return;
        core.getAsyncManager().handleCF(core.getTicketManager().getPanelByMessageId(e.getMessageIdLong()), panel -> {
            if (panel != null) {
                if (e.getUser().isBot()) return;
                e.getReaction().removeReaction(e.getUser()).queue();
                if (e.getMember().getRoles().contains(core.getPlatform().getDiscordSRV().getMainGuild().getRoleById(core.getTicketsConfig().ticket_banned_role()))) {
                    return;
                }
                core.getAsyncManager().handleCF(panel.openTicket(e.getUser()), null, er -> {
                    core.getErrorHandler().defaultHandle(er);
                });
            }
        }, null);
    }

    public void onButtonClick(ButtonClickEvent e) {
        if (core.getMainConfig().bungee_mode()) return;
        core.getAsyncManager().handleCF(core.getTicketManager().getPanelByMessageId(e.getMessageIdLong()), panel -> {
            if (panel != null) {
                if (e.getUser().isBot()) return;
                if (e.getMember().getRoles().contains(core.getPlatform().getDiscordSRV().getMainGuild().getRoleById(core.getTicketsConfig().ticket_banned_role()))) {
                    e.deferReply(true).setContent("You are Ticket Muted").queue();
                    return;
                }
                core.getAsyncManager().handleCF(panel.openTicket(e.getUser()).thenAcceptAsync(t -> {
                    ReplyAction action = e.deferReply(true);
                    PlaceholdObjectList holders = PlaceholdObjectList.ofArray(
                            new PlaceholdObject(core.getJDA().getTextChannelById(t.getChannelID()), "channel"),
                            new PlaceholdObject(e.getUser(), "user"),
                            new PlaceholdObject(t, "ticket"),
                            new PlaceholdObject(panel, "panel")
                    );
                    core.getMessageManager().messageToReplyAction(action, core.getMessageManager().getMessage(core.getTicketsConfig().ticket_open_ephemeral_msg(), holders, null).build()).queue();
                }), null, er -> {
                    core.getErrorHandler().defaultHandle(er);
                });
            }
        }, null);
    }

}
