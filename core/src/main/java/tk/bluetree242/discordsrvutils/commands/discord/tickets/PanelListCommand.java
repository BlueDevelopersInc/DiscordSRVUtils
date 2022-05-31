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

package tk.bluetree242.discordsrvutils.commands.discord.tickets;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandCategory;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.systems.tickets.Panel;
import tk.bluetree242.discordsrvutils.waiters.PaginationWaiter;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

public class PanelListCommand extends Command {
    public PanelListCommand(DiscordSRVUtils core) {
        super(core, "panelist", "Get list of panels", "[P]panellist", null, CommandCategory.TICKETS_ADMIN, "panellist");
        setAdminOnly(true);
    }

    private static int getPageCount(Set templates) {
        return (int) Math.ceil(Double.parseDouble((Float.parseFloat(templates.size() + ".0") / 5) + ""));

    }

    @Override
    public void run(CommandEvent e) throws Exception {
        Set<Panel> panels = core.getTicketManager().getPanels(e.getConnection());
        if (panels.isEmpty()) {
            e.replyErr("There are no panels to show").queue();
            return;
        }
        List<MessageEmbed> embeds = getEmbeds(panels);
        PaginationWaiter.setupMessage(e.reply(embeds.get(0)), embeds.size()).queue(m -> new PaginationWaiter(core, e.getChannel(), embeds, e.getAuthor(), m.getInteraction()));
    }

    public List<MessageEmbed> getEmbeds(Set<Panel> panels) {
        List<MessageEmbed> returnvalue = new ArrayList<>();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setTimestamp(Instant.now());
        embed.setThumbnail(core.getJDA().getSelfUser().getEffectiveAvatarUrl());
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
                embed.setThumbnail(core.getJDA().getSelfUser().getEffectiveAvatarUrl());
                embed.setTitle("Panels");
                embed.setFooter("Page " + currentPage + "/" + getPageCount(panels));
            }
            currentInPage++;
            embed.addField(panel.getName(), String.join("\n", new String[]{
                    "**ID: **" + panel.getId(),
                    "**Message Channel: **" + "<#" + panel.getChannelId() + ">",
                    "**Opened Category: **" + (core.getPlatform().getDiscordSRV().getMainGuild().getCategoryById(panel.getOpenedCategory()) == null ? String.valueOf(panel.getOpenedCategory()) : core.getPlatform().getDiscordSRV().getMainGuild().getCategoryById(panel.getOpenedCategory()).getName()).toUpperCase(),
                    "**Closed Category: **" + (core.getPlatform().getDiscordSRV().getMainGuild().getCategoryById(panel.getClosedCategory()) == null ? String.valueOf(panel.getClosedCategory()) : core.getPlatform().getDiscordSRV().getMainGuild().getCategoryById(panel.getClosedCategory()).getName()).toUpperCase(),
                    "**Allowed Roles: **" + parseRoles(panel.getAllowedRoles()),
                    /*language=md*/ "\n[Panel Message](" + "https://discord.com/channels/" + core.getPlatform().getDiscordSRV().getMainGuild().getId() + "/" + panel.getChannelId() + "/" + panel.getMessageId() + ")"
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
            joiner.add(core.getPlatform().getDiscordSRV().getMainGuild().getRoleById(role) == null ? role + "" : core.getPlatform().getDiscordSRV().getMainGuild().getRoleById(role).getAsMention());
        }
        return joiner.toString();
    }
}
