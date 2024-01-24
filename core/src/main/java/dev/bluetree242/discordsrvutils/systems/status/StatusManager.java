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

package dev.bluetree242.discordsrvutils.systems.status;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import dev.bluetree242.discordsrvutils.utils.FileWriter;
import dev.bluetree242.discordsrvutils.utils.Utils;
import github.scarsz.discordsrv.dependencies.jackson.databind.JsonNode;
import github.scarsz.discordsrv.dependencies.jackson.databind.ObjectMapper;
import github.scarsz.discordsrv.dependencies.jackson.databind.node.ObjectNode;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.exceptions.ErrorResponseException;
import github.scarsz.discordsrv.dependencies.jda.api.requests.ErrorResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Timer;

@RequiredArgsConstructor
public class StatusManager {
    @Getter
    private final DiscordSRVUtils core;
    private final ObjectMapper mapper = Utils.OBJECT_MAPPER;
    private StatusTimer timer = new StatusTimer(this);

    public Path getDataPath() {
        return core.getPlatform().getDataFolder().toPath().resolve("data").resolve("status-message.json");
    }


    public Message getStatusMessage(boolean online) {
        PlaceholdObjectList holders = new PlaceholdObjectList(core);
        holders.add(new PlaceholdObject(core, core.getServer(), "server"));
        return core.getMessageManager().parseMessageFromJson(core.getMessageManager().getMessageJSONByName("status-" + (online ? "online" : "offline")), holders, null).build();
    }

    public void newMessage(TextChannel channel) {
        // Path for some temp storage which should not be stored in database
        File file = getDataPath().toFile();
        ObjectNode json = mapper.createObjectNode();
        json.put("channel", channel.getIdLong());
        // Its already async, complete() should be fine
        Message msg = channel.sendMessage(getStatusMessage(true)).complete();
        json.put("message", msg.getIdLong());
        try {
            if (!file.exists()) {
                getDataPath().getParent().toFile().mkdirs();
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);
            writer.write(json.toString());
            writer.close();
            // Should be written successfully
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Long getMessageId() throws IOException {
        File file = getDataPath().toFile();
        if (!file.exists()) return null;
        JsonNode json = mapper.readTree(file);
        return json.get("message").asLong();
    }

    public Long getChannelId() throws IOException {
        File file = getDataPath().toFile();
        if (!file.exists()) return null;
        JsonNode json = mapper.readTree(file);
        return json.get("channel").asLong();
    }

    public void editMessage(boolean online) {
        Message toSend = getStatusMessage(online);
        try {
            Long messageId = getMessageId();
            Long channelId = getChannelId();
            if (messageId == null || channelId == null) return;
            TextChannel channel = core.getPlatform().getDiscordSRV().getMainGuild().getTextChannelById(channelId);
            if (channel == null) {
                core.getLogger().severe("Failed to update status message because the channel does not exist anymore. To fix this. run/status <new channel> or /status only (on discord) to disable.");
                return;
            }
            Message msg = channel.retrieveMessageById(messageId).complete();
            // Its async so it should be fine.. complete() to make sure it does it before discordsrv shuts down when doing offline message
            msg.editMessage(toSend).complete();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
            // Ignore the error for now
        } catch (ErrorResponseException ex) {
            if (ex.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE)
                core.getLogger().severe("Failed to update status message because the message does not exist anymore. To fix this. run /status <channel> or /status only to disable.");
        } catch (IllegalStateException e) {
            if (e.getMessage().startsWith("Attempted to update message that was not sent by this account."))
                core.getLogger().severe("Failed to update status message because the message was sent by another bot. To fix this. run /status <channel> or /status only to disable. Or return to the old bot.");
            else throw e;
        }
    }

    public void registerTimer() {
        if (timer != null) {
            timer.cancel();
        }
        new Timer().schedule(timer = new StatusTimer(this), 1000, core.getStatusConfig().update_delay() * 1000);
    }

    public void unregisterTimer() {
        if (timer != null)
            timer.cancel();
        timer = null;
    }

    public void reloadTimer() {
        unregisterTimer();
        registerTimer();
    }
}
