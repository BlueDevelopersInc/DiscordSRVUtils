package tk.bluetree242.discordsrvutils.commands.discord;

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
