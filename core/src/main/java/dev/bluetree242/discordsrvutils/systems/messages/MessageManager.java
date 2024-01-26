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

package dev.bluetree242.discordsrvutils.systems.messages;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.exceptions.InvalidMessageException;
import dev.bluetree242.discordsrvutils.exceptions.MessageNotFoundException;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import dev.bluetree242.discordsrvutils.platform.PlatformPlayer;
import dev.bluetree242.discordsrvutils.utils.FileWriter;
import dev.bluetree242.discordsrvutils.utils.Utils;
import github.scarsz.discordsrv.dependencies.commons.io.IOUtils;
import github.scarsz.discordsrv.dependencies.jackson.databind.JsonNode;
import github.scarsz.discordsrv.dependencies.jackson.databind.ObjectMapper;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.MessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.interactions.ReplyAction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
public class MessageManager {
    // Default messages to use
    @Getter
    private final Map<String, String> defaultMessages = new HashMap<>();
    private final DiscordSRVUtils core;
    ObjectMapper objectMapper = Utils.OBJECT_MAPPER;

    public Path getMessagesDirectory() {
        return core.getPlatform().getDataFolder().toPath().resolve("messages");
    }

    public void init() {
        if (getMessagesDirectory().toFile().mkdir()) {
            defaultMessages.forEach((key, val) -> {
                try {
                    File file = getMessagesDirectory().resolve(key + ".json").toFile();
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
        // Prepare a list of all messages
        String[] messages = new String[]{
                "afk",
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
                "invites",
                "kick"};
        for (String msg : messages) {
            try {
                // Add them to the map
                defaultMessages.put(msg, new String(IOUtils.toByteArray(core.getPlatform().getResource("messages/" + msg + ".json"))));
            } catch (IOException e) {
                core.getLogger().severe("Could not load " + msg + ".json");
            }
        }
    }

    public EmbedBuilder parseEmbedFromJSON(JsonNode json, PlaceholdObjectList holders, PlatformPlayer<?> placehold) {
        EmbedBuilder embed = new EmbedBuilder();

        // Title and color
        embed.setTitle(placehold(json.get("title"), holders, placehold), placehold(json.get("url"), holders, placehold));
        if (json.hasNonNull("color"))
            embed.setColor(json.get("color").isInt() ? json.get("color").asInt() : colorOf(json.get("color").asText()).getRGB());

        // Footer
        JsonNode footer = json.get("footer");
        if (footer != null)
            embed.setFooter(placehold(footer.get("text"), holders, placehold), placehold(footer.get("icon_url"), holders, placehold));

        // Thumbnail
        JsonNode thumbnail = json.get("thumbnail");
        if (thumbnail != null) embed.setThumbnail(placehold(thumbnail.get("url"), holders, placehold));

        // Image
        JsonNode image = json.get("image");
        if (image != null) embed.setImage(placehold(image.get("url"), holders, placehold));

        // Author & Timestamp
        JsonNode author = json.get("author");
        if (author != null)
            embed.setAuthor(placehold(author.get("name"), holders, placehold), placehold(author.get("url"), holders, placehold), placehold(author.get("icon_url"), holders, placehold));
        embed.setTimestamp(parseTimestamp(json, holders, placehold));

        // Fields & Description
        JsonNode fields = json.get("fields");
        if (fields != null) {
            for (JsonNode field : fields) {
                embed.addField(placehold(field.get("name"), holders, placehold), placehold(field.get("value"), holders, placehold), field.get("inline") != null && field.get("inline").asBoolean());
            }
        }
        embed.setDescription(placehold(json.get("description"), holders, placehold));
        return embed;
    }

    private @Nullable Instant parseTimestamp(JsonNode json, PlaceholdObjectList holders, PlatformPlayer placehold) {
        if (json.hasNonNull("timestamp")) {
            if (json.get("timestamp").isLong()) return Instant.ofEpochSecond(json.get("timestamp").asLong());
            else {
                if (json.get("timestamp").asText().equalsIgnoreCase("now")) return Instant.now();
                String timestamp = placehold(json.get("timestamp"), holders, placehold);
                if (timestamp.equalsIgnoreCase("now")) return Instant.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS'Z']");
                LocalDateTime dateTime = LocalDateTime.parse(timestamp, formatter);
                return dateTime.toInstant(ZoneOffset.UTC);
            }
        } else return null;
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

    public EmbedBuilder parseEmbedFromJSON(JsonNode json) {
        return parseEmbedFromJSON(json, null, null);
    }

    public MessageBuilder parseMessageFromJson(JsonNode json, PlaceholdObjectList holders, PlatformPlayer placehold) {
        MessageBuilder msg = new MessageBuilder();

        // Content
        if (json.hasNonNull("content")) {
            msg.setContent(placehold(json.get("content"), holders, placehold));
        }

        // Embed
        JsonNode embeds = json.hasNonNull("embeds") ? json.get("embeds") : json.get("embed");
        if (embeds.isArray()) {
            List<MessageEmbed> messageEmbeds = new ArrayList<>();
            for (JsonNode embed : embeds) {
                messageEmbeds.add(parseEmbedFromJSON(embed, holders, placehold).build());
            }
            msg.setEmbeds(messageEmbeds);
        } else msg.setEmbeds(parseEmbedFromJSON(embeds, holders, placehold).build());

        // Allowed Mentions
        String[] allowedMentions = json.has("allowed_mentions") ?
                (json.get("allowed_mentions").isArray() ?
                        StreamSupport.stream(json.get("allowed_mentions").spliterator(), false).map(JsonNode::asText).toArray(String[]::new)
                        : new String[]{json.get("allowed_mentions").asText()})
                : null;
        msg.setAllowedMentions(allowedMentions == null ? null : Arrays.stream(allowedMentions).map(s -> Message.MentionType.valueOf(s.toUpperCase(Locale.ROOT))).collect(Collectors.toSet()));
        return msg;
    }

    private String placehold(JsonNode ob, PlaceholdObjectList holders, PlatformPlayer<?> placehold) {
        if (ob != null) {
            String raw = ob.asText();
            if (holders != null) {
                raw = holders.apply(raw, placehold);
            } else raw = new PlaceholdObjectList(core).apply(raw, placehold);
            return raw;
        } else return null;
    }

    public JsonNode getMessageJSONByName(String name) {
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
                } catch (Exception ex) {
                    throw new MessageNotFoundException(name);
                }
            } else throw new MessageNotFoundException(name);
        }
        try {
            return objectMapper.readTree(file);
        } catch (Throwable ex) {
            throw new InvalidMessageException(name, ex);
        }
    }

    public MessageBuilder getMessage(String content, PlaceholdObjectList holders, PlatformPlayer placehold) {
        MessageBuilder msg = new MessageBuilder();
        if (content.startsWith("message:")) {
            String messageName = content.replaceFirst("message:", "");
            return parseMessageFromJson(getMessageJSONByName(messageName), holders, placehold);
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
