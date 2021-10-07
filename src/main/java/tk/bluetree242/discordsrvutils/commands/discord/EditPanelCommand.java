package tk.bluetree242.discordsrvutils.commands.discord;

import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandCategory;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandType;
import tk.bluetree242.discordsrvutils.embeds.Embed;
import tk.bluetree242.discordsrvutils.tickets.TicketManager;
import tk.bluetree242.discordsrvutils.waiters.EditPanelWaiter;

public class EditPanelCommand extends Command {
    public EditPanelCommand() {
        super("editpanel", CommandType.GUILDS, "Edit a panel", "[P]editpanel <Panel ID>", null, CommandCategory.TICKETS_ADMIN, "ep");
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
                    DiscordSRVUtils.get().handleCF(TicketManager.getInstance().getPanelById(args[1]), s -> {
                        if (panel == null) {
                            e.reply(Embed.error("Panel not found, use " + getCommandPrefix() + "panelist for list of panels")).queue();
                        } else {
                                e.getChannel().sendMessage(EditPanelWaiter.getEmbed()).queue(msg -> {
                                    new EditPanelWaiter((TextChannel) e.getChannel(), e.getAuthor(), panel.getEditor(), msg);
                                    EditPanelWaiter.addReactions(msg);

                            }, error -> {DiscordSRVUtils.get().defaultHandle(error, e.getChannel());});
                        }
                    }, error -> {DiscordSRVUtils.get().defaultHandle(error, e.getChannel());});
                }
            }, error -> {DiscordSRVUtils.get().defaultHandle(error, e.getChannel());});
        }
    }
}
