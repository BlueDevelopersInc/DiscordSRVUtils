/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2024 BlueTree242
 * -------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * END
 */

package dev.bluetree242.discordsrvutils.systems.commands.discord;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.utils.Utils;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
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
    @Setter
    private boolean adminOnly = false;
    @Setter
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

    public final Permission getRequiredPermission() {
        return requestPermission;
    }

    public MessageEmbed getHelpEmbed() {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("/" + cmd + " command")
                .setColor(Color.GREEN)
                .addField("Description", Utils.trim(getDescription()), false)
                .addField("Usage", "/" + cmd + " " + getUsage(), false)
                .setThumbnail(core.getJDA().getSelfUser().getEffectiveAvatarUrl());

        if (!aliases.isEmpty()) embed.addField("aliases", getAliasesString(), false);
        return embed.build();

    }

    public void addAliases(String... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
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

    public final boolean isAdminOnly() {
        return adminOnly;
    }

    public boolean isEnabled() {
        return !core.getCommandManager().getDisabledCommands(true).contains(this);
    }

}
