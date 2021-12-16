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

package tk.bluetree242.discordsrvutils.status;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.exceptions.ErrorResponseException;
import org.bukkit.Bukkit;
import org.json.JSONObject;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.messages.MessageManager;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import tk.bluetree242.discordsrvutils.utils.FileWriter;
import tk.bluetree242.discordsrvutils.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.concurrent.CompletableFuture;

public class StatusManager {
    private static StatusManager main;
    private DiscordSRVUtils core = DiscordSRVUtils.get();
    private StatusTimer timer = new StatusTimer();
    private Path tempPath = Paths.get(core.getBukkitMain().getDataFolder() + core.fileseparator + "tmp" + core.fileseparator + "status-message.json");
    public StatusManager() {
        this.main = this;
        tempPath.getParent().toFile().mkdir();
    }
    public static StatusManager get() {
        return main;
    }

    public Message getStatusMessage(boolean online) {
        PlaceholdObjectList holders = new PlaceholdObjectList();
        holders.add(new PlaceholdObject(Bukkit.getServer(), "server"));
        return MessageManager.get().parseMessageFromJson(MessageManager.get().getMessageJSONByName("status-" + (online ? "online" : "offline")), holders, null).build();
    }

    public CompletableFuture<Message> newMessage(TextChannel channel) {
        return core.completableFuture(() -> {
            //path for some temp storage which should not be stored in database
            File file = tempPath.toFile();
            JSONObject json = new JSONObject();
            json.put("channel", channel.getIdLong());
            //Its already async, complete() should be fine
            Message msg = channel.sendMessage(getStatusMessage(true)).complete();
            json.put("message", msg.getIdLong());
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter writer = new FileWriter(file);
                writer.write(json.toString());
                writer.close();
                //Should be written successfully
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            return msg;
        });
    }

    public Long getMessageId() throws IOException{
        File file = tempPath.toFile();
        if (!file.exists()) return null;
        JSONObject json = new JSONObject(Utils.readFile(file.getPath()));
        return json.getLong("message");
    }
    public Long getChannelId() throws IOException {
        File file = tempPath.toFile();
        if (!file.exists()) return null;
        JSONObject json = new JSONObject(Utils.readFile(file.getPath()));
        return json.getLong("channel");
    }

    public CompletableFuture<Void> editMessage(boolean online) {
        return core.completableFutureRun(() -> {
            StatusManager manager = StatusManager.get();
            Message toSend = manager.getStatusMessage(online);
            try {
                Long messageId = manager.getMessageId();
                Long channelId = manager.getChannelId();
                if (messageId == null || channelId == null) return;
                Message msg = core.getGuild().getTextChannelById(channelId).retrieveMessageById(messageId).complete();
                if (msg == null) return;
                //Its async so it should be fine.. complete() to make sure it does it before discordsrv shuts down when doing offline message
                msg.editMessage(toSend).complete();
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
                //Ignore the error for now
            } catch (ErrorResponseException ex) {
                //message does not exist, ok that is fine
            }

        });
    }

    public void registerTimer() {
        if (timer != null) {
            timer.cancel();
        }
        new Timer().schedule(timer = new StatusTimer(), 1000, core.getStatusConfig().update_delay() * 1000);
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
