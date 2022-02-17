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

package tk.bluetree242.discordsrvutils.commands.discord.suggestions;

import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandCategory;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandType;
import tk.bluetree242.discordsrvutils.systems.suggestions.SuggestionManager;
import tk.bluetree242.discordsrvutils.utils.Utils;

public class SuggestionNoteCommand extends Command {
    public SuggestionNoteCommand() {
        super("suggestionnote", CommandType.GUILDS, "Add a Note to a suggestion", "[P]suggestionnote <Suggestion Number> <Note>", null, CommandCategory.SUGGESTIONS_ADMIN,
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

        String[] args = e.getArgs();
        if (!(args.length >= 3)) {
            e.replyErr("Missing Arguments. Usage: suggestionnote <Suggestion Number> <Note>" + getCommandPrefix() + "").queue();
        } else {
            if (!Utils.isInt(args[1])) {
                e.replyErr("Invalid Suggestion Number").queue();
                return;
            }
            int number = Integer.parseInt(args[1]);
            String noteText = Utils.parseArgs(args, 2);
            e.handleCF(SuggestionManager.get().getSuggestionByNumber(number), false, "Error fetching suggestion").thenAcceptAsync(suggestion -> {
                if (suggestion == null) {
                    e.replyErr("Suggestion not found").queue();
                    return;
                }
                e.handleCF(suggestion.addNote(e.getAuthor().getIdLong(), noteText), false, "Successfully added note", "Could not add note");
            });
        }
    }
}
