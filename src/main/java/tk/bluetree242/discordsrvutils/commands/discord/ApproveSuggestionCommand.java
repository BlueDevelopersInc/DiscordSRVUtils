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

import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import tk.bluetree242.discordsrvutils.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandCategory;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandType;
import tk.bluetree242.discordsrvutils.suggestions.SuggestionManager;
import tk.bluetree242.discordsrvutils.utils.Utils;

public class ApproveSuggestionCommand extends Command {
    public ApproveSuggestionCommand() {
        super("approvesuggestion", CommandType.GUILDS, "Approve a suggestion", "[P]approvesuggestion <Suggestion Number>", null, CommandCategory.SUGGESTIONS_ADMIN,"approve");
        setAdminOnly(true);
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        if (!core.getSuggestionsConfig().enabled()) {
            e.replyErr("Suggestions are not enabled").queue();
            return;
        }

        String[] args = e.getArgs();
        if (!(args.length >= 2)) {
            e.replyErr("Missing Arguments. Usage: approvesuggestion <Suggestion Number>" + getCommandPrefix() + "").queue();
        } else {
            if (!Utils.isInt(args[1])) {
                e.replyErr("Invalid Suggestion Number").queue();
                return;
            }
            int number = Integer.parseInt(args[1]);
            e.handleCF(SuggestionManager.get().getSuggestionByNumber(number), false, "Error fetching suggestion").thenAcceptAsync(suggestion -> {
                if (suggestion == null) {
                    e.replyErr("Suggestion not found").queue();
                    return;
                }
                e.handleCF(suggestion.setApproved(true, e.getAuthor().getIdLong()), false, "Successfully approved suggestion", "Could not approve suggestion");
            });
        }
    }
}
