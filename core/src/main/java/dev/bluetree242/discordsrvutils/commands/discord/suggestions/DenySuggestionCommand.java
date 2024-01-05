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
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;

public class DenySuggestionCommand extends SuggestionCommand {
    public DenySuggestionCommand(DiscordSRVUtils core) {
        super(core, "denysuggestion", "Deny a suggestion", "[P]denysuggestion <Suggestion Number>", null, CommandCategory.SUGGESTIONS_ADMIN,
                new OptionData(OptionType.INTEGER, "number", "Suggestion Number", true));
        addAliases("deny");
        setAdminOnly(true);
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        if (!core.getSuggestionsConfig().enabled()) {
            e.replyErr("Suggestions are not enabled").queue();
            return;
        }

        int number = (int) e.getOption("number").getAsLong();
        Suggestion suggestion = core.getSuggestionManager().getSuggestionByNumber(number);
        if (suggestion == null) {
            e.replyErr("Suggestion not found").queue();
            return;
        }
        suggestion.setApproved(false, e.getAuthor().getIdLong());
        e.replySuccess("Successfully denied suggestion").queue();
    }
}
