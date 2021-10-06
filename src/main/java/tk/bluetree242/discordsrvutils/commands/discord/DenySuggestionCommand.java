package tk.bluetree242.discordsrvutils.commands.discord;

import tk.bluetree242.discordsrvutils.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandType;
import tk.bluetree242.discordsrvutils.suggestions.SuggestionManager;
import tk.bluetree242.discordsrvutils.utils.Utils;

public class DenySuggestionCommand extends Command {
    public DenySuggestionCommand() {
        super("denysuggestion", CommandType.GUILDS, "Deny a suggestion", "[P]denysuggestion <Suggestion Number>", null, "deny");
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
                e.handleCF(suggestion.setApproved(false, e.getAuthor().getIdLong()), false, "Successfully denied suggestion", "Could not deny suggestion");
            });
        }
    }}
