package tk.bluetree242.discordsrvutils.commandmanagement;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;

import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.utils.Utils;

import java.awt.*;

public abstract class Command {
    private final String cmd;
    private final DiscordSRVUtils main = DiscordSRVUtils.get();
    private final CommandType type;
    private final Permission requestPermission;
    private final String description;
    private final String usage;
    private final String[] aliases;
    private boolean adminOnly = false;
    private boolean ownerOnly = false;
    private CommandCategory category = null;

    public Command(String cmd, CommandType type, String description, String usage, Permission requiredPermission, String... aliases) {
        this.cmd = cmd;
        this.type = type;
        this.description = description;
        this.usage = usage;
        this.aliases = aliases;
        this.requestPermission = requiredPermission;
    }

    public Command(String cmd, CommandType type, String description, String usage, Permission requiredPermission, CommandCategory category, String... aliases) {
        this.cmd = cmd;
        this.type = type;
        this.description = description;
        this.usage = usage;
        this.aliases = aliases;
        this.requestPermission = requiredPermission;
        this.category = category;
        category.addCommand(this);
    }

    public abstract void run(CommandEvent e) throws Exception;

    public final String getDescription() {
        return description;
    }

    public final String getUsage() {
        return usage.replace("[P]", getCommandPrefix());
    }

    public final String[] getAliases() {
        return aliases;
    }

    public final String getCmd() {
        return cmd;
    }

    public final CommandType getCommandType() {
        return type;
    }

    public final Permission getRequiredPermission() {
        return requestPermission;
    }

    public MessageEmbed getHelpEmbed() {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(getCommandPrefix() + cmd + " Help")
                .setColor(Color.GREEN)
                .addField("Description", Utils.trim(getDescription()), false)
                .addField("Usage", getUsage(), false)
                .setThumbnail(main.getJDA().getSelfUser().getEffectiveAvatarUrl());

        if (aliases.length >= 1) embed.addField("aliases", getAliasesString(), false);
        return embed.build();

    }

    public final String getCommandPrefix() {
        return CommandManager.get().getCommandPrefix();
    }

    public final String getAliasesString() {
        String a = "";
        for (String s : aliases) {
            if (a.isEmpty()) {
                a = getCommandPrefix() + s;
            } else {
                a = a + "\n" + getCommandPrefix() + s;
            }
        }
        return a;
    }

    public final boolean isOwnerOnly() {
        return ownerOnly;
    }

    public void setOwnerOnly(boolean b) {
        ownerOnly = b;
    }

    public final boolean isAdminOnly() {
        return adminOnly;
    }

    public void setAdminOnly(boolean b) {
        adminOnly = b;
    }

}
