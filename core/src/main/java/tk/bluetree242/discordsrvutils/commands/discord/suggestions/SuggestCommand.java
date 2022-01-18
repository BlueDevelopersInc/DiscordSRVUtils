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

import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import tk.bluetree242.discordsrvutils.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandCategory;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandType;
import tk.bluetree242.discordsrvutils.leveling.LevelingManager;
import tk.bluetree242.discordsrvutils.suggestions.SuggestionManager;
import tk.bluetree242.discordsrvutils.utils.Utils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class SuggestCommand extends Command {

    public final Long ANTISPAM_EXPIRATION = Duration.ofSeconds(120L).toNanos();
    public final Map<Long, Long> antispamMap = new HashMap<>();

    public SuggestCommand() {
        super("suggest", CommandType.GUILDS, "Add a new suggestion", "[P]suggest <suggestion>", null, CommandCategory.SUGGESTIONS);
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        if (!core.getSuggestionsConfig().enabled()) {
            e.replyErr("Suggestions are not enabled").queue();
            return;
        }
        Long channelId = core.getSuggestionsConfig().suggestions_channel();
        if (channelId == 0) {
            e.replyErr("Suggestions Channel set to 0... Please change it").queue();
            return;
        }
        TextChannel channel = core.getGuild().getTextChannelById(channelId);
        if (channel == null) {
            e.replyErr("Suggestions Channel not found").queue();
            return;
        }

        if (e.getMember().getRoles().contains(core.getGuild().getRoleById(core.getSuggestionsConfig().suggestion_muted_role()))) {
            e.replyErr("You are suggestion muted").queue();
            return;
        }

        Long val = antispamMap.get(e.getAuthor().getIdLong());
        if (val == null) {
        } else {
            if (!(System.nanoTime() - val >= LevelingManager.get().MAP_EXPIRATION_NANOS)) {
                e.replyErr("Slow down.. you need to wait 2 minutes before every new suggestion").queue();
                return;
            }
        }

        String[] args = e.getArgs();
        if (!(args.length >= 2)) {
            e.replyErr("What is your suggestion? Usage: " + getCommandPrefix() + "suggest <suggestion>").queue();
            return;
        } else {
            String suggestionText = Utils.parseArgs(args, 1);
            e.handleCF(SuggestionManager.get().makeSuggestion(suggestionText, e.getAuthor().getIdLong()), false, "Error creating suggestion").thenAcceptAsync(suggestion -> {
                antispamMap.put(e.getAuthor().getIdLong(), System.nanoTime());
                e.replySuccess("Successfully created suggestion").queue();

            });
        }

    }
}
