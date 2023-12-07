/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2023 BlueTree242
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

package dev.bluetree242.discordsrvutils.commands.discord.admin;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import dev.bluetree242.discordsrvutils.systems.commands.discord.Command;
import dev.bluetree242.discordsrvutils.systems.commands.discord.CommandCategory;
import dev.bluetree242.discordsrvutils.systems.commands.discord.CommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.GuildChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;

public class EchoCommand extends Command {
    public EchoCommand(DiscordSRVUtils core) {
        super(core, "echo", "Test an Embed by it's name", "[P]testmessage <name>", null, CommandCategory.ADMIN,
                new OptionData(OptionType.STRING, "text", "Text to send", true),
                new OptionData(OptionType.CHANNEL, "channel", "Channel to send the message in", false));
        setAdminOnly(true);
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        String text = e.getOption("text").getAsString();
        Message msg = core.getMessageManager().getMessage(text, new PlaceholdObjectList(core), null).build();
        GuildChannel channel = e.getOption("channel") == null ? (GuildChannel) e.getChannel() : e.getOption("channel").getAsGuildChannel();
        if (channel != e.getChannel() && !(channel instanceof TextChannel)) {
            e.replyErr("We can only send in a text channel.").setEphemeral(true).queue();
        } else {
            e.reply("**Sending Message..**").setEphemeral(true).queue();
            ((TextChannel) channel).sendMessage(msg).queue();
        }
    }
}
