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

package tk.bluetree242.discordsrvutils.events;

import github.scarsz.discordsrv.api.events.Event;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import tk.bluetree242.discordsrvutils.commandmanagement.Command;

public class CommandExecuteEvent extends Event {


    private final Command command;
    private final MessageChannel channel;
    private final User user;
    private final MessageReceivedEvent event;

    public CommandExecuteEvent(Command command, MessageChannel channel, User user, MessageReceivedEvent event) {
        this.command = command;
        this.channel = channel;
        this.user = user;
        this.event = event;
    }

    public Command getCommand() {
        return command;
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public User getUser() {
        return user;
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }
}
