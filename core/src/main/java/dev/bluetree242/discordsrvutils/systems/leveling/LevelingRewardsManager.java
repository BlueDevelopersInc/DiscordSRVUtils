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
import github.scarsz.discordsrv.dependencies.jackson.databind.ObjectMapper;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LevelingRewardsManager {
    private final DiscordSRVUtils core;
    @Getter
    private JSONObject levelingRewardsRaw;
    @Getter
    @Setter
    private JSONObject rewardCache;
    private File rewardCacheFile;

    public void reloadLevelingRewards() {
        rewardCacheFile = core.getPlatform().getDataFolder().toPath().resolve("data").resolve("leveling-reward-cache.json").toFile();
        try {
            File file = core.getPlatform().getDataFolder().toPath().resolve("leveling-roles.json").toFile();
            File filer = core.getPlatform().getDataFolder().toPath().resolve("leveling-rewards.json").toFile();
            JSONObject json;
            if (file.exists()) {
                if (filer.exists()) {
                    core.getLogger().warning("Found leveling-roles.json, and leveling-rewards.json. Not converting, using new leveling-rewards.json");
                    levelingRewardsRaw = new JSONObject(Utils.readFile(filer));
                    return;
                }
                json = new JSONObject(Utils.readFile(file));
                file.renameTo(filer);
                filer = file;
                levelingRewardsRaw = new JSONObject();
                if (json.isEmpty() || (json.length() == 1 && json.has("_wiki"))) return;
                json = convertToRewards(json);
            } else {
                if (filer.exists()) {
                    levelingRewardsRaw = new JSONObject(Utils.readFile(filer));
                    return;
                } else {
                    json = new JSONObject().put("_wiki", "https://wiki.discordsrvutils.xyz/leveling-rewards/"); //the time i wrote this, it's a 404
                }
            }
            FileWriter writer = new FileWriter(filer);
            writer.write(new ObjectMapper().readTree(json.toString()).toPrettyString());
            writer.close();
            levelingRewardsRaw = json;
        } catch (IOException | JSONException e) {
            levelingRewardsRaw = new JSONObject();
            core.severe("Failed to load leveling-rewards.json: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void reloadRewardCache() {
        try {
            if (rewardCacheFile.exists()) {
                rewardCache = new JSONObject(Utils.readFile(rewardCacheFile));
            } else {
                rewardCache = new JSONObject();
                if (needCache()) {
                    rewardCacheFile.getParentFile().mkdirs();
                    rewardCacheFile.createNewFile();
                    FileWriter writer = new FileWriter(rewardCacheFile);
                    writer.write(rewardCache.toString());
                    writer.close();
                }
            }
        } catch (IOException e) {
            rewardCache = new JSONObject();
            core.severe("Failed to load leveling reward cache: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private List<String> getCommands(JSONObject level, PlayerStats stats) {
        if (!level.has("commands")) return null;
        return level.getJSONArray("commands").toList().stream()
                .map(o -> (String) o)
                .map(c -> PlaceholdObjectList.ofArray(core, new PlaceholdObject(core, stats, "stats")).apply(c)) //placeholders
                .collect(Collectors.toList());
    }

    private List<String> getCommands(int level, int lastLevel, PlayerStats stats) {
        List<String> result = new ArrayList<>();
        for (int num = (lastLevel + 1); num <= level; num++) {
            Object o = getLevelObject(num);
            if (o == null) continue;
            if (o instanceof JSONObject) {
                JSONObject json = (JSONObject) o;
                List<String> resultLevel = getCommands(json, stats);
                if (resultLevel != null) result.addAll(resultLevel);
            }
        }
        return result;
    }

    private int getLastLevel(UUID uuid) {
        return rewardCache.has(uuid.toString()) ? rewardCache.getInt(uuid.toString()) : 0;
    }

    public void rewardIfOnline(PlayerStats stats) {
        int lastLevel = getLastLevel(stats.getUuid());
        if (lastLevel == stats.getLevel()) return; //they got all rewards
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
        for (String key : levelingRewardsRaw.toMap().keySet()) {
            try {
                int num = Integer.parseInt(key);
                Object v = getLevelObject(num);
                if (v == null) continue; //unreachable but just in case
                if (v instanceof JSONObject && ((JSONObject) v).has("commands")) return true;
            } catch (NumberFormatException ex) {
                //not a level
            }
        }
        return false;
    }

    private JSONObject convertToRewards(JSONObject roles) {
        JSONObject result = new JSONObject();
        for (String key : roles.toMap().keySet()) {
            Object value = roles.get(key);
            if (value instanceof Long) result.put(key, new JSONObject().put("roles", new JSONArray().put(value)));
            else result.put(key, value);
        }
        return result;
    }

    public List<Role> getRolesForLevel(int level) {
        List<Role> result = new ArrayList<>();
        boolean found;
        int num = getLastLevelWithRoles(level);
        if (num == -1) return result;
        JSONObject json = levelingRewardsRaw.getJSONObject(num + "");
        result.addAll(getRoleIds(json));
        return result;
    }

    private int getLastLevelWithRoles(int level) {
        int result = -1;
        int num = level;
        while (num >= 0 && result == -1) {
            Object o = getLevelObject(num);
            num--;
            if (o instanceof JSONObject && ((JSONObject) o).has("roles")) result = num + 1;
        }
        return result;
    }

    private Object getLevelObject(int level) {
        if (levelingRewardsRaw.has(level + "")) return levelingRewardsRaw.get(level + "");
        return null;
    }

    public List<Role> getRolesToRemove(Integer level) {

        List<Role> roles = new ArrayList<>();
        Map<String, Object> map = levelingRewardsRaw.toMap();
        List<String> keys = new ArrayList<>(map.keySet());
        int num = level == null ? -1 : getLastLevelWithRoles(level);
        for (String key : keys) {
            if (key.equals(num + "")) continue;
            Object value = levelingRewardsRaw.get(key);
            if (!(value instanceof JSONObject)) continue;
            roles.addAll(getRoleIds((JSONObject) value));
        }
        return roles;
    }

    private List<Role> getRoleIds(JSONObject json) {
        List<Role> result = new ArrayList<>();
        if (json.has("roles")) {
            JSONArray roles = json.getJSONArray("roles");
            roles.forEach(r -> result.add(core.getPlatform().getDiscordSRV().getMainGuild().getRoleById((Long) r)));
        }

        return result;
    }

}
