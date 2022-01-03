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
import tk.bluetree242.discordsrvutils.commandmanagement.*;
import tk.bluetree242.discordsrvutils.embeds.Embed;

import java.awt.*;
import java.time.Instant;
import java.util.StringJoiner;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help", CommandType.EVERYWHERE, "Get Help about commands", "[P]help [Command]", null, "h");
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        String[] args = e.getArgs();
        if (args.length <= 1) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.GREEN);
            embed.setThumbnail(e.getJDA().getSelfUser().getEffectiveAvatarUrl());
            embed.setTitle(e.getJDA().getSelfUser().getName() + " Commands");
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
            embed.setDescription("Use " + getCommandPrefix() + "help <Command> to get Help for a command");
            e.reply(embed.build()).queue();
        } else {
            String cmd = args[1].toLowerCase();
            Command executor = CommandManager.get().getCommandHashMap().get(cmd);
            if (executor == null) {
                e.reply(Embed.error("Command not found")).queue();
            } else {
                e.reply(executor.getHelpEmbed()).queue();
            }
        }

    }


}
