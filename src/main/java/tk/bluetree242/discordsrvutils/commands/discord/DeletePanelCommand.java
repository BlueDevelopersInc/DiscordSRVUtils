package tk.bluetree242.discordsrvutils.commands.discord;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.commandmanagement.*;
import tk.bluetree242.discordsrvutils.embeds.Embed;
import tk.bluetree242.discordsrvutils.tickets.TicketManager;
import tk.bluetree242.discordsrvutils.waiters.PaginationWaiter;

import java.awt.*;
import java.time.Instant;
import java.util.StringJoiner;

public class DeletePanelCommand extends Command {

    public DeletePanelCommand() {
        super("deletepanel", CommandType.GUILDS, "Delete a panel", "[P]deletepanel <Panel ID>", null, CommandCategory.TICKETS_ADMIN, "dp");
        setAdminOnly(true);
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        String[] args = e.getArgs();
        if (args.length <= 1) {
            e.reply(Embed.error("Please specify the id of panel, for panel list use " + getCommandPrefix() + "panelist")).queue();
        } else {
            DiscordSRVUtils.get().handleCF(TicketManager.get().getPanelById(args[1]), panel -> {
                if (panel == null) {
                    e.reply(Embed.error("Panel not found, use " + getCommandPrefix() + "panelist for list of panels")).queue();
                } else {
                    DiscordSRVUtils.get().handleCF(panel.delete(), s -> {
                        e.replySuccess("Successfully deleted panel. Note that deleting ticket channels may take a while").queue();
                    }, error -> {DiscordSRVUtils.get().defaultHandle(error, e.getChannel());});
                }
            }, error -> {DiscordSRVUtils.get().defaultHandle(error, e.getChannel());});
        }

    }
}
