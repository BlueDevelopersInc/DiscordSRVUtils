/*
 *  LICENSE
 *  DiscordSRVUtils
 *  -------------
 *  Copyright (C) 2020 - 2021 BlueTree242
 *  -------------
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program.  If not, see
 *  <http://www.gnu.org/licenses/gpl-3.0.html>.
 *  END
 */

package tk.bluetree242.discordsrvutils.systems.commandmanagement;

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
    public DiscordSRVUtils core = DiscordSRVUtils.get();
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

        embed.setTitle(getCommandPrefix() + cmd + " Command")
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

    public boolean isEnabled() {
        return !CommandManager.get().getDisabledCommands(true).contains(this);
    }

}
