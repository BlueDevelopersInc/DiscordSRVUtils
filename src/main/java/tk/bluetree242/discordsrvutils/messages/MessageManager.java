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

package tk.bluetree242.discordsrvutils.messages;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.MessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.interactions.ReplyAction;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.exceptions.EmbedNotFoundException;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import tk.bluetree242.discordsrvutils.utils.FileWriter;
import tk.bluetree242.discordsrvutils.utils.Utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.time.Instant;

public class MessageManager {
    private static MessageManager instance;
    private DiscordSRVUtils core = DiscordSRVUtils.get();

    public MessageManager() {
        instance = this;
    }

    public static MessageManager get() {
        return instance;
    }

    public EmbedBuilder parseEmbedFromJSON(JSONObject json, PlaceholdObjectList holders, Player placehold) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(getStringFromJson(json, "title", holders, placehold), getStringFromJson(json, "url", holders, placehold));
        if (!json.isNull("color")) {
            Integer color = json.get("color") instanceof Integer ? json.getInt("color") : colorOf(json.getString("color")).getRGB();

            embed.setColor(color);
        }
        if (!json.isNull("footer")) {
            JSONObject footer = json.getJSONObject("footer");
            embed.setFooter(getStringFromJson(footer, "text", holders, placehold), getStringFromJson(footer, "icon_url"));
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
                embed.addField(getStringFromJson(field, "name", holders, placehold), getStringFromJson(field, "value", holders, placehold), field.isNull("inline") ? false : field.getBoolean("inline"));
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
        return null;
    }

    public EmbedBuilder parseEmbedFromJSON(JSONObject json) {
        return parseEmbedFromJSON(json, null, null);
    }

    public MessageBuilder parseMessageFromJson(JSONObject json, PlaceholdObjectList holders, Player placehold) {
        MessageBuilder msg = new MessageBuilder();
        if (!json.isNull("embed"))
            msg.setEmbeds(parseEmbedFromJSON(json.getJSONObject("embed"), holders, placehold).build());
        if (!json.isNull("content")) {
            msg.setContent(getStringFromJson(json, "content", holders, placehold));
        }
        return msg;
    }


    private String getStringFromJson(JSONObject ob, String val, PlaceholdObjectList holders, Player placehold) {
        if (!ob.isNull(val)) {
            String raw = ob.getString(val);
            if (holders != null) {
                raw = holders.apply(raw);
            }
            raw = PlaceholdObject.applyPlaceholders(raw, placehold);
            return raw;
        }
        return null;
    }

    private String getStringFromJson(JSONObject ob, String val) {
        return getStringFromJson(ob, val, null, null);
    }

    public JSONObject getMessageJSONByName(String name) {
        File file = new File(core.messagesDirectory + core.fileseparator + name + ".json");
        if (!file.exists()) {
            if (core.defaultmessages.containsKey(name)) {
                try {
                    file.createNewFile();
                    FileWriter writer = new FileWriter(file);
                    writer.write(core.defaultmessages.get(name));
                    writer.close();
                    return getMessageJSONByName(name);
                } catch (Exception ex) {
                    throw new EmbedNotFoundException(name);
                }
            }
            throw new EmbedNotFoundException(name);
        }
        try {
            return new JSONObject(Utils.readFile(file.getPath()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public MessageBuilder getMessage(String content, PlaceholdObjectList holders, Player placehold) {
        MessageBuilder msg = new MessageBuilder();
        if (content.startsWith("message:")) {
            String embedname = content.replaceFirst("message:", "");
            JSONObject json = getMessageJSONByName(embedname);
            if (!json.isNull("content")) {
                msg.setContent(getStringFromJson(json, "content", holders, placehold));
            }
            if (!json.isNull("embed")) {
                msg.setEmbed(parseEmbedFromJSON(json.getJSONObject("embed"), holders, placehold).build());
            }
        } else {
            if (holders != null) {
                content = holders.apply(content);
            }
            content = PlaceholdObject.applyPlaceholders(content, placehold);
            content = Utils.colors(content);
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
