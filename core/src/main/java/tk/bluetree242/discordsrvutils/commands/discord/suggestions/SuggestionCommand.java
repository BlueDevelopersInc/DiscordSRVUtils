package tk.bluetree242.discordsrvutils.commands.discord.suggestions;

import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.systems.commandmanagement.CommandCategory;

public abstract class SuggestionCommand extends Command {

    public SuggestionCommand(DiscordSRVUtils core, String cmd, String description, String usage, Permission requiredPermission, CommandCategory category, OptionData... options) {
        super(core, cmd, description, usage, requiredPermission, category, options);
    }

    public boolean isEnabled() {
        return core.getSuggestionsConfig().enabled() && super.isEnabled();
    }
}
