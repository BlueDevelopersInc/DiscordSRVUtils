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
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;
import lombok.Getter;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Command {
    public final DiscordSRVUtils core;
    private final String cmd;
    private final Permission requestPermission;
    private final String description;
    private final String usage;
    @Getter
    private final List<String> aliases = new ArrayList<>();
    @Getter
    private final OptionData[] options;
    private boolean adminOnly = false;
    private boolean ownerOnly = false;
    private CommandCategory category = null;

    public Command(DiscordSRVUtils core, String cmd, String description, String usage, Permission requiredPermission, OptionData... options) {
        this.core = core;
        this.cmd = cmd;
        this.options = options;
        this.description = description;
        this.usage = usage;
        this.requestPermission = requiredPermission;
    }

    public Command(DiscordSRVUtils core, String cmd, String description, String usage, Permission requiredPermission, CommandCategory category, OptionData... options) {
        this.core = core;
        this.cmd = cmd;
        this.options = options;
        this.description = description;
        this.usage = usage;
        this.requestPermission = requiredPermission;
        this.category = category;
        category.addCommand(this);
    }

    public Command(DiscordSRVUtils core, String cmd, String description, String usage, Permission requiredPermission, CommandCategory category, String... aliases) {
        this.core = core;
        addAliases(aliases);
        this.cmd = cmd;
        options = new OptionData[0];
        this.description = description;
        this.usage = usage;
        this.requestPermission = requiredPermission;
        this.category = category;
        category.addCommand(this);
    }

    public abstract void run(CommandEvent e) throws Exception;

    public final String getDescription() {
        return description;
    }

    public final String getUsage() {
        return usage.replace("[P]", "/");
    }


    public final String getCmd() {
        return cmd;
    }

    public final Permission getRequiredPermission() {
        return requestPermission;
    }

    public MessageEmbed getHelpEmbed() {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("/" + cmd + " Command")
                .setColor(Color.GREEN)
                .addField("Description", Utils.trim(getDescription()), false)
                .addField("Usage", getUsage(), false)
                .setThumbnail(core.getJDA().getSelfUser().getEffectiveAvatarUrl());

        if (aliases.size() >= 1) embed.addField("aliases", getAliasesString(), false);
        return embed.build();

    }

    public void addAliases(String... aliases) {
        for (String alias : aliases) {
            this.aliases.add(alias);
        }
    }


    public final String getAliasesString() {
        String a = "";
        for (String s : aliases) {
            if (a.isEmpty()) {
                a = "/" + s;
            } else {
                a = a + "\n" + "/" + s;
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
        return !core.getCommandManager().getDisabledCommands(true).contains(this);
    }

}
