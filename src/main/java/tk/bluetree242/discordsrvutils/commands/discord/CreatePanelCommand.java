package tk.bluetree242.discordsrvutils.commands.discord;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import tk.bluetree242.discordsrvutils.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandCategory;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandType;
import tk.bluetree242.discordsrvutils.embeds.Embed;
import tk.bluetree242.discordsrvutils.waiters.CreatePanelWaiter;

import java.awt.*;

public class CreatePanelCommand extends Command {
    public CreatePanelCommand() {
        super("createpanel", CommandType.GUILDS, "Create a ticket panel", "[P]createpanel", null, CommandCategory.TICKETS, "cp");
        setAdminOnly(true);
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        if (CreatePanelWaiter.getWaiter((TextChannel) e.getChannel(), e.getMember().getUser()) != null) {
            e.getChannel().sendMessage(Embed.error("You are already creating one")).queue();
            return;
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.ORANGE);
        embed.setDescription("**Step 1: Please Send the name of the panel**");
        e.getChannel().sendMessage(embed.build()).queue();
        new CreatePanelWaiter((TextChannel) e.getChannel(), e.getMember().getUser());
    }
}
