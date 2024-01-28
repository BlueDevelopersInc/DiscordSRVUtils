/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2024 BlueTree242
 * -------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * END
 */

package dev.bluetree242.discordsrvutils.commands.discord;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.embeds.Embed;
import dev.bluetree242.discordsrvutils.systems.commands.discord.Command;
import dev.bluetree242.discordsrvutils.systems.commands.discord.CommandCategory;
import dev.bluetree242.discordsrvutils.systems.commands.discord.CommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.time.Instant;
import java.util.StringJoiner;

public class HelpCommand extends Command {
    public HelpCommand(DiscordSRVUtils core) {
        super(core, "help", "Provides help for most commands.", "[command]", null,
                new OptionData(OptionType.STRING, "command", "Command to get help of", false));
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        if (e.getOption("command") == null) {
            if (!core.getMainConfig().help_response().isEmpty()) {
                e.replyMessage(core.getMainConfig().help_response()).queue();
                return;
            }
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.GREEN);
            embed.setThumbnail(e.getJDA().getSelfUser().getEffectiveAvatarUrl());
            embed.setTitle(e.getJDA().getSelfUser().getName() + "'s commands");
            embed.setFooter("Executed by " + e.getAuthor().getAsTag(), e.getAuthor().getEffectiveAvatarUrl());
            embed.setTimestamp(Instant.now());
            for (CommandCategory category : CommandCategory.values()) {
                if (!category.getEnabledCommands().isEmpty()) {
                    StringJoiner joiner = new StringJoiner("`, `", "`", "`");
                    category.getCommands().forEach(cmd -> {
                        if (cmd.isEnabled())
                            joiner.add(cmd.getCmd());
                    });
                    embed.addField(category.toString(), joiner.toString(), false);
                }
            }
            embed.setDescription("Use `" + "/" + "help <command>` to get help for a specific command");
            e.reply(embed.build()).queue();
        } else {
            String cmd = e.getOption("command").getAsString();
            Command executor = core.getCommandManager().getCommandHashMap().get(cmd);
            if (executor == null) {
                e.reply(Embed.error("Command not found")).queue();
            } else {
                e.reply(executor.getHelpEmbed()).queue();
            }
        }

    }


}
