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

import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.ButtonClickEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import lombok.RequiredArgsConstructor;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.embeds.Embed;
import tk.bluetree242.discordsrvutils.waiter.Waiter;
import tk.bluetree242.discordsrvutils.waiters.PaginationWaiter;

@RequiredArgsConstructor
public class PaginationListener extends ListenerAdapter {
    private final DiscordSRVUtils core;

    public PaginationWaiter getWaiter(long interactionId) {
        for (Waiter w : core.getWaiterManager().getWaiterByName("PaginationWaiter")) {
            PaginationWaiter waiter = (PaginationWaiter) w;
            if (waiter.getInteraction().getIdLong() == interactionId) {
                return waiter;
            }
        }
        return null;
    }

    public void onButtonClick(ButtonClickEvent e) {
        if (e.getMessage().getInteraction() == null) return;
        core.getAsyncManager().executeAsync(() -> {
            boolean backward = e.getButton().getId().equals("backward");

            PaginationWaiter waiter = getWaiter(e.getMessage().getInteraction().getIdLong());
            if (waiter != null) {
                if (waiter.getUser().getIdLong() != e.getUser().getIdLong()) {
                    e.replyEmbeds(Embed.error("Only the person who triggered this pagination can navigate.")).setEphemeral(true).queue();
                    return;
                }
                e.deferEdit().queue();
                if (e.getButton().getId().equals("delete")) {
                    waiter.expire(false);
                    e.getMessage().delete().queue();
                    return;
                }
                int page = waiter.getPage() + (backward ? (-1) : (1));
                if (page == 0) return;
                if (waiter.getEmbeds().size() < page) return;
                MessageEmbed embed = waiter.getEmbeds().get(page - 1);
                if (embed == null) return;
                waiter.setPage(page);
                e.getMessage().editMessageEmbeds(embed).setEmbeds(embed).setActionRows(PaginationWaiter.getActionRow(waiter.getEmbeds().size(), waiter.getPage())).queue();
            }
        });
    }


}
