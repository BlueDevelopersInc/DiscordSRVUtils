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

package dev.bluetree242.discordsrvutils.systems.leveling;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import dev.bluetree242.discordsrvutils.platform.PlatformPlayer;
import dev.bluetree242.discordsrvutils.utils.FileWriter;
import dev.bluetree242.discordsrvutils.utils.Utils;
import github.scarsz.discordsrv.dependencies.jackson.databind.JsonNode;
import github.scarsz.discordsrv.dependencies.jackson.databind.ObjectMapper;
import github.scarsz.discordsrv.dependencies.jackson.databind.node.ArrayNode;
import github.scarsz.discordsrv.dependencies.jackson.databind.node.ObjectNode;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
public class LevelingRewardsManager {
    private final DiscordSRVUtils core;
    @Getter
    private ObjectNode levelingRewardsRaw;
    @Getter
    @Setter
    private ObjectNode rewardCache;
    private File rewardCacheFile;
    private final ObjectMapper objectMapper = Utils.OBJECT_MAPPER;

    public void reloadLevelingRewards() {
        rewardCacheFile = core.getPlatform().getDataFolder().toPath().resolve("data").resolve("leveling-reward-cache.json").toFile();
        try {
            File file = core.getPlatform().getDataFolder().toPath().resolve("leveling-roles.json").toFile();
            File filer = core.getPlatform().getDataFolder().toPath().resolve("leveling-rewards.json").toFile();
            ObjectNode json;
            if (file.exists()) {
                if (filer.exists()) {
                    core.getLogger().warning("Found leveling-roles.json, and leveling-rewards.json. Not converting, using new leveling-rewards.json");
                    levelingRewardsRaw = (ObjectNode) objectMapper.readTree(filer);
                    return;
                }
                json = (ObjectNode) objectMapper.readTree(file);
                file.renameTo(filer);
                levelingRewardsRaw = objectMapper.createObjectNode();
                if (json.isEmpty() || (json.size() == 1 && json.has("_wiki"))) return;
                json = convertToRewards(json);
            } else {
                if (filer.exists()) {
                    levelingRewardsRaw = (ObjectNode) objectMapper.readTree(filer);
                    return;
                } else {
                    json = objectMapper.createObjectNode().put("_wiki", "https://wiki.discordsrvutils.xyz/leveling-rewards/"); // The time I wrote this, it's a 404
                }
            }
                FileWriter writer = new FileWriter(filer);
            writer.write(json.toPrettyString());
            writer.close();
            levelingRewardsRaw = json;
        } catch (IOException | JSONException e) {
            levelingRewardsRaw = objectMapper.createObjectNode();
            core.severe("Failed to load leveling-rewards.json: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void reloadRewardCache() {
        try {
            if (rewardCacheFile.exists()) {
                rewardCache = (ObjectNode) objectMapper.readTree(rewardCacheFile);
            } else {
                rewardCache = objectMapper.createObjectNode();
                if (needCache()) {
                    rewardCacheFile.getParentFile().mkdirs();
                    rewardCacheFile.createNewFile();
                    FileWriter writer = new FileWriter(rewardCacheFile);
                    writer.write(rewardCache.toString());
                    writer.close();
                }
            }
        } catch (IOException e) {
            rewardCache = objectMapper.createObjectNode();
            core.severe("Failed to load leveling reward cache: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private List<String> getCommands(ObjectNode level, PlayerStats stats) {
        if (!level.has("commands")) return null;
        return StreamSupport.stream(level.get("commands").spliterator(), false)
                .map(JsonNode::asText)
                .map(c -> PlaceholdObjectList.ofArray(core, new PlaceholdObject(core, stats, "stats")).apply(c)) // Placeholders
                .collect(Collectors.toList());
    }

    private List<String> getCommands(int level, int lastLevel, PlayerStats stats) {
        List<String> result = new ArrayList<>();
        for (int num = (lastLevel + 1); num <= level; num++) {
            JsonNode o = getLevelObject(num);
            if (o == null) continue;
            if (o.isObject()) {
                ObjectNode json = (ObjectNode) o;
                List<String> resultLevel = getCommands(json, stats);
                if (resultLevel != null) result.addAll(resultLevel);
            }
        }
        return result;
    }

    private int getLastLevel(UUID uuid) {
        return rewardCache.has(uuid.toString()) ? rewardCache.get(uuid.toString()).asInt() : 0;
    }

    public void rewardIfOnline(PlayerStats stats) {
        int lastLevel = getLastLevel(stats.getUuid());
        if (lastLevel == stats.getLevel()) return; // They got all rewards
        if (lastLevel > stats.getLevel()) {
            rewardCache.remove(stats.getUuid().toString());
            saveRewardCache();
        }
        PlatformPlayer player = core.getPlatform().getServer().getPlayer(stats.getUuid());
        if (player == null) return;
        List<String> commands = getCommands(stats.getLevel(), lastLevel, stats);
        if (commands.isEmpty()) return;
        core.getPlatform().getServer().executeConsoleCommands(commands.stream().toArray(i -> new String[commands.size()]));
        rewardCache.put(stats.getUuid().toString(), stats.getLevel());
        saveRewardCache();
    }

    public void saveRewardCache() {
        try {
            if (!rewardCacheFile.exists() && !needCache()) return;
            else if (!rewardCacheFile.exists()) rewardCacheFile.createNewFile();
            FileWriter writer = new FileWriter(rewardCacheFile);
            writer.write(rewardCache.toString());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean needCache() {
        Iterator<String> keys = levelingRewardsRaw.fieldNames();
        while(keys.hasNext()) {
            String key = keys.next();
            try {
                int num = Integer.parseInt(key);
                JsonNode v = getLevelObject(num);
                if (v == null) continue; // Unreachable but just in case
                if (v.isObject() && v.has("commands")) return true;
            } catch (NumberFormatException ex) {
                // Not a level
            }
        }
        return false;
    }

    private ObjectNode convertToRewards(ObjectNode roles) {
        ObjectNode result = objectMapper.createObjectNode();
        Iterator<String> keys = roles.fieldNames();
        while (keys.hasNext()) {
            String key = keys.next();
            JsonNode value = roles.get(key);
            if (value.isLong()) result.set(key, objectMapper.createObjectNode().set("roles", objectMapper.createArrayNode().add(value)));
            else result.set(key, value);
        }
        return result;
    }

    public List<Role> getRolesForLevel(int level) {
        List<Role> result = new ArrayList<>();
        boolean found;
        int num = getLastLevelWithRoles(level);
        if (num == -1) return result;
        ObjectNode json = (ObjectNode) levelingRewardsRaw.get(Integer.toString(num));
        result.addAll(getRoleIds(json));
        return result;
    }

    private int getLastLevelWithRoles(int level) {
        int result = -1;
        int num = level;
        while (num >= 0 && result == -1) {
            JsonNode o = getLevelObject(num);

            num--;
            if (o != null && o.isObject() && o.has("roles")) result = num + 1;
        }
        return result;
    }

    private JsonNode getLevelObject(int level) {
        if (levelingRewardsRaw.has(level + "")) return levelingRewardsRaw.get(Integer.toString(level));
        return null;
    }

    public List<Role> getRolesToRemove(Integer level) {

        List<Role> roles = new ArrayList<>();
        Iterator<String> keys = levelingRewardsRaw.fieldNames();
        int num = level == null ? -1 : getLastLevelWithRoles(level);
        while (keys.hasNext()) {
            String key = keys.next();
            if (key.equals(num + "")) continue;
            JsonNode value = levelingRewardsRaw.get(key);
            if (!value.isObject()) continue;
            roles.addAll(getRoleIds((ObjectNode) value));
        }
        return roles;
    }

    private List<Role> getRoleIds(ObjectNode json) {
        List<Role> result = new ArrayList<>();
        if (json.has("roles")) {
            ArrayNode roles = (ArrayNode) json.get("roles");
            roles.forEach(r -> result.add(core.getPlatform().getDiscordSRV().getMainGuild().getRoleById(r.asLong())));
        }

        return result;
    }

}
