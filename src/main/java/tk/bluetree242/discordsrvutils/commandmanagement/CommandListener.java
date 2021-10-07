package tk.bluetree242.discordsrvutils.commandmanagement;

import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.PrivateChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageUpdateEvent;
import github.scarsz.discordsrv.dependencies.jda.api.exceptions.InsufficientPermissionException;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import org.w3c.dom.Text;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.embeds.Embed;

import java.util.regex.Pattern;

public class CommandListener extends ListenerAdapter {
    private final DiscordSRVUtils main = DiscordSRVUtils.get();


    public void onMessageReceived(MessageReceivedEvent e) {
        main.executeAsync(() -> {
            if (e.getMessage().isWebhookMessage() || e.getAuthor().isBot()) return;

            String[] args = e.getMessage().getContentRaw().split(" ");
            String cmd = args[0].toLowerCase();
            if (cmd.startsWith(main.getCommandPrefix())) {
                cmd = cmd.replaceFirst(Pattern.quote(main.getCommandPrefix()), "");
                Command executor = CommandManager.get().getCommandHashMap().get(cmd);
                if (executor == null) return;
                try {
                    if (executor.getCommandType() != CommandType.EVERYWHERE) {
                        if (executor.getCommandType() == CommandType.GUILDS) {
                            if (!(e.getChannel() instanceof TextChannel)) {
                                e.getMessage().reply(Embed.error("This command can only be used in guilds.")).queue();
                                return;
                            }
                        } else {
                            if (!(e.getChannel() instanceof PrivateChannel)) {
                                e.getMessage().reply(Embed.error("This command can only be used in DMS.")).queue();
                                return;
                            }
                        }
                    }
                    if (e.getChannel() instanceof TextChannel) {
                        if (e.getGuild().getIdLong() != DiscordSRVUtils.get().getGuild().getIdLong()) return;
                    }
                    if (executor.getRequiredPermission() != null) {
                        if (e.getChannel() instanceof TextChannel) {
                            if (!e.getMember().hasPermission(executor.getRequiredPermission())) {
                                e.getChannel().sendMessage(Embed.error("You don't have permission to use this command.", "Required: " + executor.getRequiredPermission().toString())).queue();
                                return;
                            }
                        }
                    }
                    if (e.getChannel() instanceof TextChannel) {
                        if (executor.isOwnerOnly()) {
                            if (!e.getMember().isOwner()) {
                                e.getMessage().reply(Embed.error("Only Guild Owner can use this command.")).queue();
                                return;
                            }
                        }
                        if (executor.isAdminOnly()) {
                            if (!main.isAdmin(e.getAuthor().getIdLong())) {
                                e.getMessage().reply(Embed.error("Only Admins can use this command.")).queue();
                                return;
                            }
                        }
                    }
                    if (e.getChannel() instanceof TextChannel) {
                        if (!e.getGuild().getSelfMember().hasPermission(e.getTextChannel(), Permission.MESSAGE_WRITE)) {
                            return;
                        }
                        if (!e.getGuild().getSelfMember().hasPermission(e.getTextChannel(), Permission.MESSAGE_EMBED_LINKS)) {
                            e.getChannel().sendMessage("❌ The bot is missing the " + Permission.MESSAGE_EMBED_LINKS.getName() + " Permission (for embeds) in this channel. This command is not executed").queue();
                            return;
                        }
                    }
                    main.getLogger().info(e.getAuthor().getAsTag() + " Used " + main.getCommandPrefix() + cmd + " Command");
                    executor.run(new CommandEvent(e.getMember(), e.getMessage(), e.getAuthor(), e.getChannel(), e.getJDA()));
                } catch (InsufficientPermissionException ex) {
                    ex.printStackTrace();
                    e.getMessage().reply(Embed.error("An error happened while executing this Command. Please report to the devs!", "The bot is missing the following permission: " + ex.getPermission())).queue();
                } catch (Exception exception) {
                    exception.printStackTrace();
                    e.getMessage().reply(Embed.error("An error happened while executing this Command. Please report to the devs!")).queue();
                }
            }
        });

    }

    public void onMessageUpdate(MessageUpdateEvent e) {
        onMessageReceived(new MessageReceivedEvent(e.getJDA(), e.getResponseNumber(), e.getMessage()));
    }
}
