/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2022 BlueTree242
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

package dev.bluetree242.discordsrvutils.bukkit.discordsrv;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.embeds.Embed;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommand;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.exceptions.InsufficientPermissionException;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import lombok.RequiredArgsConstructor;
import dev.bluetree242.discordsrvutils.bukkit.DiscordSRVUtilsBukkit;
import dev.bluetree242.discordsrvutils.systems.commandmanagement.Command;
import dev.bluetree242.discordsrvutils.systems.commandmanagement.CommandEvent;
import dev.bluetree242.discordsrvutils.systems.commandmanagement.CommandManager;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class SlashCommandProvider implements github.scarsz.discordsrv.api.commands.SlashCommandProvider {
    private final DiscordSRVUtilsBukkit core;

    @Override
    public Set<PluginSlashCommand> getSlashCommands() {
        Set<PluginSlashCommand> commands = new HashSet<>();
        if (core.getCore() == null || !core.getCore().isEnabled() || !core.getCore().getMainConfig().register_slash())
            return commands;
        CommandManager manager = core.getCore().getCommandManager();
        for (Command command : manager.getCommands()) {
            if (!command.isEnabled()) continue;
            commands.add(getCmd(command.getCmd(), command));
            for (String alias : command.getAliases()) {
                commands.add(getCmd(alias, command));
            }
        }
        return commands;
    }


    private PluginSlashCommand getCmd(String alias, Command cmd) {
        return new PluginSlashCommand(core, new CommandData(alias, cmd.getDescription()).addOptions(cmd.getOptions()), DiscordSRV.getPlugin().getMainGuild().getId());
    }

    @SlashCommand(path = "*")
    public void onCommand(SlashCommandEvent e) {
        DiscordSRVUtils core = this.core.getCore();
        if (core.getMainConfig().bungee_mode()) return;
        String cmd = e.getName();
        Command executor = core.getCommandManager().getCommandHashMap().get(cmd);
        if (executor == null || !executor.isEnabled()) return;
        CommandEvent event = new CommandEvent(core, e.getMember(), e.getUser(), e.getChannel(), e.getJDA(), e);
        try {
            if (executor.getRequiredPermission() != null) {
                if (e.getChannel() instanceof TextChannel) {
                    if (!e.getMember().hasPermission(executor.getRequiredPermission())) {
                        e.replyEmbeds(Embed.error("You don't have permission to use this command.", "Required: " + executor.getRequiredPermission())).queue();
                        return;
                    }
                }
            }
            if (e.getChannel() instanceof TextChannel) {
                if (executor.isOwnerOnly()) {
                    if (!e.getMember().isOwner()) {
                        e.replyEmbeds(Embed.error("Only Guild Owner can use this command.")).queue();
                        return;
                    }
                }
                if (executor.isAdminOnly()) {
                    if (!core.getJdaManager().isAdmin(e.getUser().getIdLong())) {
                        e.replyEmbeds(Embed.error("Only Admins can use this command.", "Your id must be in admin list on the config.yml")).queue();
                        return;
                    }
                }
            }
            core.getLogger().info(e.getUser().getAsTag() + " Used " + "/" + cmd + " Command");
            executor.run(event);
        } catch (InsufficientPermissionException ex) {
            ex.printStackTrace();
            e.replyEmbeds(Embed.error("An error happened while executing this Command. Please report to the devs!", "The bot is missing the following permission: " + ex.getPermission())).queue();
        } catch (Exception exception) {
            exception.printStackTrace();
            e.replyEmbeds(Embed.error("An error happened while executing this Command. Please report to the devs!")).queue();
        }
        if (event.isConnOpen()) {
            try {
                event.getConnection().configuration().connectionProvider().acquire().close();
            } catch (SQLException throwables) {
                core.getErrorHandler().defaultHandle(throwables);
            }
        }

    }
}
