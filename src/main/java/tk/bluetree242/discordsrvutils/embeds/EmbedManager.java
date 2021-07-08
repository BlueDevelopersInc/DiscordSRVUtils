package tk.bluetree242.discordsrvutils.embeds;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.MessageBuilder;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.exceptions.EmbedNotFoundException;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import tk.bluetree242.discordsrvutils.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;

public class EmbedManager {
    private static EmbedManager instance;
    private DiscordSRVUtils core = DiscordSRVUtils.get();
    public static EmbedManager get() {
        return instance;
    }

    public EmbedManager() {
        instance = this;
    }


    public EmbedBuilder parseFromJSON(JSONObject json, PlaceholdObjectList holders, Player placehold) {
        EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(getStringFromJson(json, "title", holders, placehold), getStringFromJson(json, "url", holders, placehold));
            if (!json.isNull("color")) {
                embed.setColor(json.getInt("color"));
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
                embed.setAuthor(getStringFromJson(author, "name", holders, placehold),getStringFromJson(author, "url", holders, placehold),getStringFromJson(author, "icon_url", holders, placehold));
            }
            embed.setTimestamp(getStringFromJson(json, "timestamp", holders, placehold) != null ? Instant.now() : null);
            if (!json.isNull("fields")) {
                JSONArray fields = json.getJSONArray("fields");
                for (Object o : fields) {
                    JSONObject field = (JSONObject) o;
                    embed.addField(getStringFromJson(field, "name", holders, placehold), getStringFromJson(field, "value", holders, placehold), field.isNull("inline") ? false : field.getBoolean("inline"));
                }
            }
            return embed;
    }

    public EmbedBuilder parseFromJSON(JSONObject json) {
        return parseFromJSON(json, null, null);
    }


    private String getStringFromJson(JSONObject ob, String val, PlaceholdObjectList holders, Player placehold) {
        if (!ob.isNull(val)) {
            String raw = ob.getString(val);
            if (holders != null) {
                raw =holders.apply(raw);
            }
               raw = PlaceholdObject.applyPlaceholders(raw, placehold);
            return raw;
        }
        return null;
    }

    private String getStringFromJson(JSONObject ob, String val) {
        return getStringFromJson(ob, val, null, null);
    }

    public JSONObject getEmbedJSONByName(String name) {
        File file = new File(core.embedsDirectory + "/" + name + ".json");
        if (!file.exists()) {
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
        if (content.startsWith("embed:")) {
            String embedname = content.replaceFirst("embed:", "");
            JSONObject json = getEmbedJSONByName(embedname);
            if (!json.isNull("content")) {
                msg.setContent(json.getString("content"));
            }
            if (!json.isNull("embed")) {
                msg.setEmbed(parseFromJSON(json.getJSONObject("embed"), holders, placehold).build());
            }
        } else {
            if (holders != null) {
                content = holders.apply(content);
            }
            content = PlaceholdObject.applyPlaceholders(content, placehold);
            msg.setContent(content);
        }
        return msg;
    }



    public MessageBuilder getMessage(String content) {
        return  getMessage(content, null, null);
    }



    }
