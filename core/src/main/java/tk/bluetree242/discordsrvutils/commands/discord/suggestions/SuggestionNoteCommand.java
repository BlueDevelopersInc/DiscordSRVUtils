/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2022 BlueTree242
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

package tk.bluetree242.discordsrvutils.commands.discord.suggestions;

import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandCategory;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.systems.suggestions.Suggestion;

public class SuggestionNoteCommand extends SuggestionCommand {
    public SuggestionNoteCommand(DiscordSRVUtils core) {
        super(core, "suggestionnote", "Add a Note to a suggestion", "[P]suggestionnote <Suggestion Number> <Note>", null, CommandCategory.SUGGESTIONS_ADMIN,
                new OptionData(OptionType.INTEGER, "number", "Suggestion Number", true),
                new OptionData(OptionType.STRING, "note", "The Note to add", true));
        addAliases("note");
        setAdminOnly(true);
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        if (!core.getSuggestionsConfig().enabled()) {
            e.replyErr("Suggestions are not enabled").queue();
            return;
        }

        int number = (int) e.getOption("number").getAsLong();
        String noteText = e.getOption("note").getAsString();
        Suggestion suggestion = core.getSuggestionManager().getSuggestionByNumber(number);
        if (suggestion == null) {
            e.replyErr("Suggestion not found").queue();
            return;
        }
        suggestion.addNote(e.getAuthor().getIdLong(), noteText);
        e.replySuccess("Successfully added note").queue();

    }
}
