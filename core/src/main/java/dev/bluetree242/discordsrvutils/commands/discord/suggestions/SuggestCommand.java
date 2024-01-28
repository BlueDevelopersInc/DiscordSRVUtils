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

package dev.bluetree242.discordsrvutils.commands.discord.suggestions;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.systems.commands.discord.CommandCategory;
import dev.bluetree242.discordsrvutils.systems.commands.discord.CommandEvent;
import dev.bluetree242.discordsrvutils.systems.suggestions.Suggestion;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;

import java.util.HashMap;
import java.util.Map;

public class SuggestCommand extends SuggestionCommand {

    public final Map<Long, Long> antispamMap = new HashMap<>();

    public SuggestCommand(DiscordSRVUtils core) {
        super(core, "suggest", "Add a new suggestion", "<suggestion>", null, CommandCategory.SUGGESTIONS,
                new OptionData(OptionType.STRING, "suggestion", "Your Suggestion", true));
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        Long channelId = core.getSuggestionsConfig().suggestions_channel();
        if (channelId == 0) {
            e.replyErr("Suggestions Channel set to 0... Please change it").setEphemeral(useEphemeral()).queue();
            return;
        }
        TextChannel channel = core.getPlatform().getDiscordSRV().getMainGuild().getTextChannelById(channelId);
        if (channel == null) {
            e.replyErr("Suggestions Channel not found").setEphemeral(useEphemeral()).queue();
            return;
        }

        if (e.getMember().getRoles().contains(core.getPlatform().getDiscordSRV().getMainGuild().getRoleById(core.getSuggestionsConfig().suggestion_muted_role()))) {
            e.replyErr("You are suggestion muted").setEphemeral(useEphemeral()).queue();
            return;
        }

        Long val = antispamMap.get(e.getAuthor().getIdLong());
        if (val != null) {
            if (!(System.nanoTime() - val >= core.getLevelingManager().MAP_EXPIRATION_NANOS)) {
                e.replyErr("Slow down.. you need to wait 2 minutes before every new suggestion").setEphemeral(useEphemeral()).queue();
                return;
            }
        }

        String suggestionText = e.getOption("suggestion").getAsString();
        Suggestion suggestion = core.getSuggestionManager().makeSuggestion(suggestionText, e.getAuthor().getIdLong());
        e.replySuccess("Successfully created suggestion").setEphemeral(useEphemeral()).queue();
    }
}
