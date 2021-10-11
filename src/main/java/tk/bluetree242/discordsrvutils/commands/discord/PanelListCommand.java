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

package tk.bluetree242.discordsrvutils.commands.discord;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandCategory;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandType;
import tk.bluetree242.discordsrvutils.tickets.Panel;
import tk.bluetree242.discordsrvutils.tickets.TicketManager;
import tk.bluetree242.discordsrvutils.utils.Utils;
import tk.bluetree242.discordsrvutils.waiters.PaginationWaiter;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

public class PanelListCommand extends Command {
    public PanelListCommand() {
        super("panelist", CommandType.GUILDS, "Get list of panels", "[P]panellist", null, CommandCategory.TICKETS_ADMIN, "panellist");
        setAdminOnly(true);
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        DiscordSRVUtils.get().handleCF(TicketManager.get().getPanels(), panels -> {
            if (panels.isEmpty()) {
                e.getChannel().sendMessage("There are no panels to show").queue();
            }
            new PaginationWaiter(e.getChannel(), getEmbeds(panels), e.getAuthor());
        }, failure -> {
            DiscordSRVUtils.get().defaultHandle(failure, (TextChannel) e.getChannel());
        });
    }

    private static int getPageCount(Set templates) {
        return (int) Math.ceil(Double.parseDouble((Float.parseFloat(templates.size() + ".0") / 5) + ""));

    }

    public List<MessageEmbed> getEmbeds(Set<Panel> panels) {
        List<MessageEmbed> returnvalue = new ArrayList<>();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setTimestamp(Instant.now());
        embed.setThumbnail(DiscordSRVUtils.get().getJDA().getSelfUser().getEffectiveAvatarUrl());
        embed.setTitle("Panels");
        embed.setFooter("Page 1/" + getPageCount(panels));
        int currentPage = 1;
        int currentInPage = 0;
        for (Panel panel : panels) {
            if (currentInPage == 5) {
                currentInPage = 0;
                currentPage++;
                returnvalue.add(embed.build());
                embed = new EmbedBuilder();
                embed.setColor(Color.GREEN);
                embed.setTimestamp(Instant.now());
                embed.setThumbnail(DiscordSRVUtils.get().getJDA().getSelfUser().getEffectiveAvatarUrl());
                embed.setTitle("Panels");
                embed.setFooter("Page " + currentPage + "/" + getPageCount(panels));
            }
            currentInPage++;
            embed.addField(panel.getName(), String.join("\n", new String[]{
                    "**ID: **" + panel.getId(),
                    "**Message Channel: **" + "<#" + panel.getChannelId() + ">",
                    "**Opened Category: **" + (DiscordSRVUtils.get().getGuild().getCategoryById(panel.getOpenedCategory()) == null ? String.valueOf(panel.getOpenedCategory()) : DiscordSRVUtils.get().getGuild().getCategoryById(panel.getOpenedCategory()).getName()).toUpperCase(),
                    "**Closed Category: **" + (DiscordSRVUtils.get().getGuild().getCategoryById(panel.getClosedCategory()) == null ? String.valueOf(panel.getClosedCategory()) : DiscordSRVUtils.get().getGuild().getCategoryById(panel.getClosedCategory()).getName()).toUpperCase(),
                    "**Allowed Roles: **" + parseRoles(panel.getAllowedRoles()),
                    /*language=md*/ "\n[Panel Message](" + "https://discord.com/channels/" + DiscordSRVUtils.get().getGuild().getId() + "/" + panel.getChannelId() + "/" + panel.getMessageId() + ")"
            }), false);

        }
        returnvalue.add(embed.build());
        return returnvalue;
    }

    private String parseRoles(Set<Long> roles) {
        if (roles.isEmpty()) {
            return "None";
        }
        StringJoiner joiner = new StringJoiner(", ");
        for (Long role : roles) {
            joiner.add(DiscordSRVUtils.get().getGuild().getRoleById(role) == null ? role + "" : DiscordSRVUtils.get().getGuild().getRoleById(role).getAsMention());
        }
        return joiner.toString();
    }
}
