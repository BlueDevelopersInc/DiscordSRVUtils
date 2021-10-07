package tk.bluetree242.discordsrvutils.commands.discord;

import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import tk.bluetree242.discordsrvutils.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandCategory;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandType;
import tk.bluetree242.discordsrvutils.suggestions.SuggestionManager;
import tk.bluetree242.discordsrvutils.utils.Utils;

public class SuggestionNoteCommand extends Command {
    public SuggestionNoteCommand() {
        super("suggestionnote", CommandType.GUILDS, "Add a Note to a suggestion", "[P]suggestionnote <Suggestion Number> <Note>", null, CommandCategory.SUGGESTIONS_ADMIN, "note", "addnote");
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
