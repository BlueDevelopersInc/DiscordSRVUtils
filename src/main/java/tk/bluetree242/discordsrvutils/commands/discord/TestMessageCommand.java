package tk.bluetree242.discordsrvutils.commands.discord;

import org.json.JSONException;
import tk.bluetree242.discordsrvutils.commandmanagement.Command;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandEvent;
import tk.bluetree242.discordsrvutils.commandmanagement.CommandType;
import tk.bluetree242.discordsrvutils.exceptions.EmbedNotFoundException;

public class TestMessageCommand extends Command {
    public TestMessageCommand() {
        super("testmessage", CommandType.EVERYWHERE, "Test an Embed by it's name", "[P]testmessage <name>", null, "tm");
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
                e.replyMessage("message:" + name).queue();
            } catch (EmbedNotFoundException ex) {
                e.replyErr("Embed does not exist").queue();
            } catch (JSONException ex) {
                e.replyErr("Embed is invalid").queue();
            }
        }
    }
}
