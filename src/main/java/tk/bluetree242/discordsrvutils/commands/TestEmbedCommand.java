package tk.bluetree242.discordsrvutils.commands;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import net.dv8tion.jda.api.Permission;
import org.json.JSONException;
import tk.bluetree242.discordsrvutils.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandType;
import tk.bluetree242.discordsrvutils.embeds.EmbedManager;
import tk.bluetree242.discordsrvutils.exceptions.EmbedNotFoundException;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;

public class TestEmbedCommand extends Command {
    public TestEmbedCommand() {
        super("testembed", CommandType.EVERYWHERE, "Test an Embed by it's name", "[P]testembed <name>", null, "te");
        setAdminOnly(true);
    }

    @Override
    public void run(CommandEvent e) throws Exception {
        String[] args = e.getArgs();
        if (!(args.length >= 2)) {
            e.reply(getHelpEmbed()).queue();
        } else {
            String name = args[1];
            try {
                e.replyMessage("embed:" + name).queue();
            } catch (EmbedNotFoundException ex) {
                e.replyErr("Embed does not exist").queue();
            } catch (JSONException ex) {
                e.replyErr("Embed is invalid").queue();
            }
        }
    }
}
