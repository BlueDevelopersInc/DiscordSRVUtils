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

package dev.bluetree242.discordsrvutils.systems.messages;

import dev.bluetree242.discordsrvutils.exceptions.MessageNotFoundException;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.MessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.interactions.ReplyAction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import dev.bluetree242.discordsrvutils.platform.PlatformPlayer;
import dev.bluetree242.discordsrvutils.utils.FileWriter;
import dev.bluetree242.discordsrvutils.utils.Utils;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class MessageManager {
    //default messages to use
    @Getter
    private final Map<String, String> defaultMessages = new HashMap<>();
    private final DiscordSRVUtils core;
    //messages folder path[https://discordsrvutils.xyz/support]


    public Path getMessagesDirectory() {
        return Paths.get(core.getPlatform().getDataFolder().toString() + core.fileseparator + "messages");
    }


    public void init() {
        if (getMessagesDirectory().toFile().mkdir()) {
            defaultMessages.forEach((key, val) -> {
                try {
                    File file = new File(getMessagesDirectory() + core.fileseparator + key + ".json");
                    file.createNewFile();
                    FileWriter writer = new FileWriter(file);
                    writer.write(val);
                    writer.close();
                } catch (FileNotFoundException e) {
                    core.getLogger().severe("Error creating default message \"" + key + "\"");
                } catch (IOException e) {
                    core.getLogger().severe("Error writing default message \"" + key + "\"");
                }
            });
        }
    }

    public void initDefaultMessages() {
        //prepare a list of all messages
        String[] messages = new String[]{"afk",
                "ban",
                "level",
                "mute",
                "no-longer-afk",
                "panel",
                "suggestion",
                "suggestion-approved",
                "suggestion-denied",
                "suggestion-noted",
                "suggestion-noted-approved",
                "suggestion-noted-denied",
                "ticket-close",
                "ticket-open",
                "ticket-reopen",
                "unban",
                "unmute",
                "welcome",
                "status-online",
                "status-offline",
                "warn",
                "temp-ban",
                "invites"};
        for (String msg : messages) {
            try {
                //add them to the map
                defaultMessages.put(msg, new String(IOUtils.toByteArray(core.getPlatform().getResource("messages/" + msg + ".json"))));
            } catch (IOException e) {
                core.getLogger().severe("Could not load " + msg + ".json");
            }
        }
    }

    public EmbedBuilder parseEmbedFromJSON(JSONObject json, PlaceholdObjectList holders, PlatformPlayer placehold) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(getStringFromJson(json, "title", holders, placehold), getStringFromJson(json, "url", holders, placehold));
        if (!json.isNull("color")) {
            int color = json.get("color") instanceof Integer ? json.getInt("color") : colorOf(json.getString("color")).getRGB();

            embed.setColor(color);
        }
        if (!json.isNull("footer")) {
            JSONObject footer = json.getJSONObject("footer");
            embed.setFooter(getStringFromJson(footer, "text", holders, placehold), getStringFromJson(footer, "icon_url", holders, placehold));
        }
        if (!json.isNull("thumbnail")) {
            JSONObject thumbnail = json.getJSONObject("thumbnail");
            embed.setThumbnail(getStringFromJson(thumbnail, "url", holders, placehold));
        }
        if (!json.isNull("image")) {
            JSONObject image = json.getJSONObject("image");
            embed.setImage(getStringFromJson(image, "url", holders, placehold));
        }
        if (!json.isNull("author")) {
            JSONObject author = json.getJSONObject("author");
            embed.setAuthor(getStringFromJson(author, "name", holders, placehold), getStringFromJson(author, "url", holders, placehold), getStringFromJson(author, "icon_url", holders, placehold));
        }
        embed.setTimestamp(getStringFromJson(json, "timestamp", holders, placehold) != null ? Instant.now() : null);
        if (!json.isNull("fields")) {
            JSONArray fields = json.getJSONArray("fields");
            for (Object o : fields) {
                JSONObject field = (JSONObject) o;
                embed.addField(getStringFromJson(field, "name", holders, placehold), getStringFromJson(field, "value", holders, placehold), !field.isNull("inline") && field.getBoolean("inline"));
            }
        }

        embed.setDescription(getStringFromJson(json, "description", holders, placehold));
        return embed;
    }

    private Color colorOf(String color) {
        for (Field clr : Color.class.getFields()) {
            if (clr.getName().equalsIgnoreCase(color)) {
                if (clr.getType() == Color.class) {
                    try {
                        return (Color) clr.get(null);
                    } catch (IllegalAccessException e) {
                        return null;
                    }
                }
            }
        }
        try {
            return Color.decode(color);
        } catch (Exception s) {
            return null;
        }
    }

    public EmbedBuilder parseEmbedFromJSON(JSONObject json) {
        return parseEmbedFromJSON(json, null, null);
    }

    public MessageBuilder parseMessageFromJson(JSONObject json, PlaceholdObjectList holders, PlatformPlayer placehold) {
        MessageBuilder msg = new MessageBuilder();
        if (!json.isNull("embed"))
            msg.setEmbeds(parseEmbedFromJSON(json.getJSONObject("embed"), holders, placehold).build());
        if (!json.isNull("content")) {
            msg.setContent(getStringFromJson(json, "content", holders, placehold));
        }
        return msg;
    }


    private String getStringFromJson(JSONObject ob, String val, PlaceholdObjectList holders, PlatformPlayer placehold) {
        if (!ob.isNull(val)) {
            String raw = ob.getString(val);
            if (holders != null) {
                raw = holders.apply(raw, placehold);
            } else raw = new PlaceholdObjectList(core).apply(raw, placehold);
            return raw;
        }
        return null;
    }

    private String getStringFromJson(JSONObject ob, String val) {
        return getStringFromJson(ob, val, null, null);
    }

    public JSONObject getMessageJSONByName(String name) {
        String[] split = name.split("/");
        Path path = getMessagesDirectory();
        File file = null;
        if (split.length == 1) file = path.resolve(name + ".json").toFile();
        else {
            int index = 0;
            for (String s : split) {
                index++;
                if (index < split.length)
                path = path.resolve(s);
                else file = path.resolve(s + ".json").toFile();
            }
        }
        if (!file.exists()) {
            if (defaultMessages.containsKey(name)) {
                try {
                    file.createNewFile();
                    FileWriter writer = new FileWriter(file);
                    writer.write(defaultMessages.get(name));
                    writer.close();
                    return getMessageJSONByName(name);
                } catch (Exception ex) {
                    throw new MessageNotFoundException(name);
                }
            }
            throw new MessageNotFoundException(name);
        }
        try {
            return new JSONObject(Utils.readFile(file.getPath()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public MessageBuilder getMessage(String content, PlaceholdObjectList holders, PlatformPlayer placehold) {
        MessageBuilder msg = new MessageBuilder();
        if (content.startsWith("message:")) {
            String embedname = content.replaceFirst("message:", "");
            JSONObject json = getMessageJSONByName(embedname);
            if (!json.isNull("content")) {
                msg.setContent(getStringFromJson(json, "content", holders, placehold));
            }
            if (!json.isNull("embed")) {
                msg.setEmbeds(parseEmbedFromJSON(json.getJSONObject("embed"), holders, placehold).build());
            }
        } else {
            if (holders != null) {
                content = holders.apply(content);
            }
            msg.setContent(content);
        }
        return msg;
    }


    public MessageBuilder getMessage(String content) {
        return getMessage(content, null, null);
    }

    public ReplyAction messageToReplyAction(ReplyAction action, Message msg) {
        action.addEmbeds(msg.getEmbeds());
        action.setContent(msg.getContentRaw());
        return action;
    }


}
