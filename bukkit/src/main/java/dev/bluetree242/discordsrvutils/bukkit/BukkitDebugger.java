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

package dev.bluetree242.discordsrvutils.bukkit;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.VersionInfo;
import dev.bluetree242.discordsrvutils.hooks.PluginHook;
import dev.bluetree242.discordsrvutils.platform.Debugger;
import dev.bluetree242.discordsrvutils.utils.Utils;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.alexh.weak.Dynamic;
import github.scarsz.discordsrv.dependencies.commons.io.FileUtils;
import github.scarsz.discordsrv.dependencies.commons.lang3.ArrayUtils;
import github.scarsz.discordsrv.dependencies.commons.lang3.RandomStringUtils;
import github.scarsz.discordsrv.dependencies.commons.lang3.StringUtils;
import github.scarsz.discordsrv.dependencies.commons.lang3.exception.ExceptionUtils;
import github.scarsz.discordsrv.dependencies.jackson.databind.JsonNode;
import github.scarsz.discordsrv.dependencies.jackson.databind.ObjectMapper;
import github.scarsz.discordsrv.dependencies.jackson.databind.node.ArrayNode;
import github.scarsz.discordsrv.dependencies.jackson.databind.node.ObjectNode;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.okhttp3.*;
import github.scarsz.discordsrv.hooks.SkriptHook;
import github.scarsz.discordsrv.hooks.VaultHook;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.scarsz.discordsrv.util.PlayerUtil;
import github.scarsz.discordsrv.util.PluginUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BukkitDebugger implements Debugger {
    private final SecureRandom RANDOM = new SecureRandom();
    private final OkHttpClient client = new OkHttpClient.Builder().build();
    private final DiscordSRVUtils core;
    ObjectMapper mapper = Utils.OBJECT_MAPPER;

    @Override
    public String run() throws Exception {
        return run(null);
    }

    @Override
    public String run(String stacktrace) throws Exception {
        if (stacktrace == null) stacktrace = core.getErrorHandler().getFinalError();
        ArrayNode data = mapper.createArrayNode();
        Map<String, String> information = new HashMap<>();
        information.put("DSU Version", core.getPlatform().getDescription().getVersion());
        information.put("DSU Command Executor", Bukkit.getServer().getPluginCommand("discordsrvutils").getPlugin() + "");
        information.put("DSU Hooked Plugins", core.getPluginHookManager().getHooks().stream().filter(PluginHook::isHooked).map(Object::toString).collect(Collectors.joining(", ")));
        information.put("DiscordSRV Version", DiscordSRV.getPlugin() + "");
        information.put("DiscordSRV Config Version", DiscordSRV.config().getString("ConfigVersion"));
        information.put("DSU Status", core.isEnabled() ? "Enabled" : "Disabled");
        information.put("JDA Status", (DiscordUtil.getJda() != null && DiscordUtil.getJda().getGatewayPing() != -1 ? DiscordUtil.getJda().getStatus().name() + " / " + DiscordUtil.getJda().getGatewayPing() + "ms" : "build not finished"));
        information.put("Registered listeners", getRegisteredListeners());
        information.put("Channels", DiscordSRV.getPlugin().getChannels() + "");
        information.put("Console Channel", DiscordSRV.getPlugin().getConsoleChannel() + "");
        information.put("Main Chat Channel", DiscordSRV.getPlugin().getMainChatChannel() + " -> " + DiscordSRV.getPlugin().getMainTextChannel());
        information.put("Discord Guild Roles", (DiscordSRV.getPlugin().getMainGuild() == null ? "invalid main guild" : DiscordSRV.getPlugin().getMainGuild().getRoles().stream().map(Role::toString).collect(Collectors.toList()) + ""));
        information.put("Vault Groups", Arrays.toString(VaultHook.getGroups()));
        information.put("PlaceholderAPI expansions", getInstalledPlaceholderApiExpansions());
        information.put("/discord command executor", (Bukkit.getServer().getPluginCommand("discord") != null ? Bukkit.getServer().getPluginCommand("discord").getPlugin() + "" : ""));
        information.put("threads",
                "\n    channel topic updater -> alive: " + (DiscordSRV.getPlugin().getChannelTopicUpdater() != null && DiscordSRV.getPlugin().getChannelTopicUpdater().isAlive()) +
                        "\n    server watchdog -> alive: " + (DiscordSRV.getPlugin().getServerWatchdog() != null && DiscordSRV.getPlugin().getServerWatchdog().isAlive()) +
                        "\n    nickname updater -> alive: " + (DiscordSRV.getPlugin().getNicknameUpdater() != null && DiscordSRV.getPlugin().getNicknameUpdater().isAlive())
        );
        information.put("ExecutorService Status", core.getAsyncManager().getPool() == null ? "null" : (core.getAsyncManager().getPool().isShutdown() ? "Shutdown" : "Q:" + core.getAsyncManager().getPool().getQueue().size() + ", R:" + core.getAsyncManager().getPool().getActiveCount() + ", AV:" + core.getAsyncManager().getPool().getPoolSize()));
        information.put("DiscordSRV Hooked Plugins", DiscordSRV.getPlugin().getPluginHooks().stream().map(github.scarsz.discordsrv.hooks.PluginHook::getPlugin).filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(", ")));
        information.put("Scripts", String.join(", ", SkriptHook.getSkripts()));
        data.add(mapper.createObjectNode().put("type", "key_value").put("name", "Information").set("data", mapToKeyValue(information)));
        Map<String, String> versionConfig = new HashMap<>();
        versionConfig.put("Version", VersionInfo.VERSION);
        versionConfig.put("Build Number", VersionInfo.BUILD_NUMBER);
        versionConfig.put("Commit Hash", VersionInfo.COMMIT);
        versionConfig.put("Build Date", new Date(Long.parseLong(VersionInfo.BUILD_DATE)) + " (" + (Utils.getDuration(System.currentTimeMillis() - Long.parseLong(VersionInfo.BUILD_DATE)) + " ago)"));
        data.add(mapper.createObjectNode().put("type", "key_value").put("name", "Version Config").set("data", mapToKeyValue(versionConfig)));
        ObjectNode logs = mapper.createObjectNode().put("type", "files").put("name", "Log Information");
        ArrayNode logsData = logs.putArray("data").add(mapper.createObjectNode().put("type", "log").put("name", "Logs").put("content", Utils.b64Encode(getRelevantLinesFromServerLog())));
        if (stacktrace != null) {
            logsData.add(mapper.createObjectNode().put("type", "log").put("name", "Last Error").put("content", Utils.b64Encode(stacktrace)));
        }
        data.add(logs);

        data.add(mapper.createObjectNode().put("type", "key_value").put("name", "System Info").set("data", mapToKeyValue(getSystemInfo())));
        data.add(mapper.createObjectNode().put("type", "key_value").put("name", "Server Info").set("data", mapToKeyValue(getServerInfo())));
        data.add(mapper.createObjectNode().put("type", "files").put("name", "DiscordSRVUtils Conf Files").set("data", FilesToArray(getDSUFiles())));
        data.add(mapper.createObjectNode().put("type", "files").put("name", "DiscordSRV Conf Files").set("data", FilesToArray(getDiscordSRVFiles())));
        List<Map<String, String>> files = getMessages();
        data.add(mapper.createObjectNode().put("type", "files").put("name", "DSU Messages Files").set("data", FilesToArray(files)));
        int aesBits = 256;
        String key = RandomStringUtils.randomAlphanumeric(aesBits == 256 ? 32 : 16);
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("data", Utils.b64Encode(encrypt(key.getBytes(), data.toString()))).build();
        Request request = new Request.Builder().post(body).url("https://mcdebug.bluetree242.dev/api/v1/createDebug").build();
        Response response = client.newCall(request).execute();

        JsonNode bdy = mapper.readTree(response.body().string());
        response.close();
        if (response.code() != 200) {
            return "ERROR: INVALID RESPONSE CODE " + response.code();
        }
        return "https://mcdebug.bluetree242.dev" + "/" + bdy.get("id").asText() + "#" + key;


    }

    private ArrayNode mapToKeyValue(Map<String, String> map) {
        ArrayNode output = mapper.createArrayNode();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            output.add(mapper.createObjectNode().put("key", entry.getKey()).put("value", entry.getValue()));
        }
        return output;
    }

    private List<Map<String, String>> getDSUFiles() throws Exception {
        List<Map<String, String>> files = new ArrayList<>();
        Path dataFolderPath = core.getPlatform().getDataFolder().toPath();
        files.add(fileMap("config.yml", Utils.readFile(dataFolderPath.resolve("config.yml").toFile())));
        files.add(fileMap("PunishmentsIntegration.yml", Utils.readFile(dataFolderPath.resolve("PunishmentsIntegration.yml").toFile())));
        files.add(fileMap("tickets.yml", Utils.readFile(dataFolderPath.resolve("tickets.yml").toFile())));
        files.add(fileMap("leveling.yml", Utils.readFile(dataFolderPath.resolve("leveling.yml").toFile())));
        files.add(fileMap("status.yml", Utils.readFile(dataFolderPath.resolve("status.yml").toFile())));
        files.add(fileMap("suggestions.yml", Utils.readFile(dataFolderPath.resolve("suggestions.yml").toFile())));
        files.add(fileMap("leveling-rewards.json", Utils.readFile(dataFolderPath.resolve("leveling-rewards.json").toFile())));
        return files;
    }

    private Map<String, String> getServerInfo() {
        Map<String, String> output = new HashMap<>();

        List<String> plugins = Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(Object::toString).sorted().collect(Collectors.toList());

        output.put("server players", PlayerUtil.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
        output.put("server plugins: ", plugins + "");
        output.put("Minecraft version: ", Bukkit.getVersion());
        output.put("Bukkit API version: ", Bukkit.getBukkitVersion());
        output.put("Server online mode: ", Bukkit.getOnlineMode() + "");

        return output;
    }

    private ObjectNode fileMapToObject(Map<String, String> fileMap) {
        ObjectNode output = mapper.createObjectNode();
        output.put("name", fileMap.get("name"));
        output.put("content", Utils.b64Encode(fileMap.get("content")));
        output.put("type", fileMap.get("type"));
        return output;
    }

    private ArrayNode FilesToArray(List<Map<String, String>> files) {
        ArrayNode array = mapper.createArrayNode();
        for (Map<String, String> file : files) {
            array.add(fileMapToObject(file));
        }
        return array;
    }

    private Map<String, String> fileMap(String name, String content) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("content", content);
        map.put("type", name.split("\\.")[name.split("\\.").length - 1]);
        return map;
    }

    private List<Map<String, String>> getDiscordSRVFiles() throws IOException {
        List<Map<String, String>> files = new ArrayList<>();
        files.add(fileMap("config.yml", FileUtils.readFileToString(DiscordSRV.getPlugin().getConfigFile(), StandardCharsets.UTF_8)));
        files.add(fileMap("config-active.yml", getActiveConfig()));
        files.add(fileMap("messages.yml", FileUtils.readFileToString(DiscordSRV.getPlugin().getMessagesFile(), StandardCharsets.UTF_8)));
        files.add(fileMap("voice.yml", FileUtils.readFileToString(DiscordSRV.getPlugin().getVoiceFile(), StandardCharsets.UTF_8)));
        files.add(fileMap("linking.yml", FileUtils.readFileToString(DiscordSRV.getPlugin().getLinkingFile(), StandardCharsets.UTF_8)));
        files.add(fileMap("synchronization.yml", FileUtils.readFileToString(DiscordSRV.getPlugin().getSynchronizationFile(), StandardCharsets.UTF_8)));
        files.add(fileMap("alerts.yml", FileUtils.readFileToString(DiscordSRV.getPlugin().getAlertsFile(), StandardCharsets.UTF_8)));
        files.forEach(map -> {
            String content = map.get("content");
            if (StringUtils.isNotBlank(content)) {
                // Remove sensitive options from files
                for (String option : github.scarsz.discordsrv.util.DebugUtil.SENSITIVE_OPTIONS) {
                    String value = DiscordSRV.config().getString(option);
                    if (StringUtils.isNotBlank(value) && !value.equalsIgnoreCase("username")) {
                        content = content.replace(value, "REDACTED");
                    }
                }

                content = content.replaceAll("[A-Za-z\\d]{24}\\.[\\w-]{6}\\.[\\w-]{27}", "TOKEN REDACTED");
            } else {
                content = "blank";
            }
            map.put("content", content);
        });
        return files;
    }

    private String getActiveConfig() {
        try {
            Dynamic activeConfig = DiscordSRV.config().getProvider("config").getValues();
            StringBuilder stringBuilder = new StringBuilder(500);
            Iterator<Dynamic> iterator = activeConfig.allChildren().iterator();
            while (iterator.hasNext()) {
                Dynamic child = iterator.next();
                if (!child.allChildren().findAny().isPresent()) {
                    stringBuilder.append(child.key().asObject()).append(": ").append(child.asObject());
                } else {
                    StringJoiner childJoiner = new StringJoiner(", ");

                    Iterator<Dynamic> childIterator = child.allChildren().iterator();
                    while (childIterator.hasNext()) {
                        Dynamic grandchild = childIterator.next();
                        childJoiner.add("- " + grandchild.asObject());
                    }

                    stringBuilder.append(child.key().asString()).append(": ").append(childJoiner);
                }
                stringBuilder.append("\n");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            return "Failed to get parsed config: " + e.getMessage() + "\n" + ExceptionUtils.getStackTrace(e);
        }
    }

    private Map<String, String> getSystemInfo() {
        Map<String, String> output = new HashMap<>();

        // Total number of processors or cores available to the JVM
        output.put("Available processors (cores)", Runtime.getRuntime().availableProcessors() + "");
        // Memory
        output.put("Free memory for JVM (MB)", Runtime.getRuntime().freeMemory() / 1024 / 1024 + "");
        output.put("Maximum memory for JVM (MB)", (Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "no limit" : Runtime.getRuntime().maxMemory() / 1024 / 1024) + "");
        output.put("Total memory available for JVM (MB)", Runtime.getRuntime().totalMemory() / 1024 / 1024 + "");

        // Drive space
        File serverRoot = DiscordSRV.getPlugin().getDataFolder().getAbsoluteFile().getParentFile().getParentFile();
        output.put("Server storage", "");
        output.put("- total space (MB)", serverRoot.getTotalSpace() / 1024 / 1024 + "");
        output.put("- free space (MB)", serverRoot.getFreeSpace() / 1024 / 1024 + "");
        output.put("- usable space (MB)", serverRoot.getUsableSpace() / 1024 / 1024 + "");

        // Java version
        Map<String, String> systemProperties = ManagementFactory.getRuntimeMXBean().getSystemProperties();
        output.put("Java version", systemProperties.get("java.version"));
        output.put("Java vendor", systemProperties.get("java.vendor") + " " + systemProperties.get("java.vendor.url"));
        output.put("Java home", systemProperties.get("java.home"));
        output.put("Command line", systemProperties.get("sun.java.command"));
        output.put("Time zone", systemProperties.get("user.timezone"));

        return output;
    }

    private String getRegisteredListeners() {
        if (core.getJDA() == null) return "JDA is null";
        StringJoiner joiner = new StringJoiner(", ");
        for (Object listener : core.getJDA().getEventManager().getRegisteredListeners()) {
            joiner.add(listener.getClass().getSimpleName());
        }
        return joiner.toString();
    }

    private String getRelevantLinesFromServerLog() {
        List<String> output = new LinkedList<>();
        try {
            FileReader fr = new FileReader("logs/latest.log");
            BufferedReader br = new BufferedReader(fr);
            boolean done = false;
            while (!done) {
                String line = br.readLine();
                if (line == null) done = true;
                if (line != null
                        && line.toLowerCase().contains("discordsrv")
                        && !line.toLowerCase().contains("[discordsrv] chat:")) {
                    output.add(DiscordUtil.aggressiveStrip(line));
                }
            }
        } catch (IOException e) {
            DiscordSRV.error(e);
        }

        return String.join("\n", output);
    }

    public byte[] encrypt(byte[] key, String data) {
        try {
            return encrypt(key, data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] encrypt(byte[] key, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            byte[] iv = new byte[cipher.getBlockSize()];
            RANDOM.nextBytes(iv);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(data);
            return ArrayUtils.addAll(iv, encrypted);
        } catch (InvalidKeyException e) {
            if (e.getMessage().toLowerCase().contains("illegal key size")) {
                throw new RuntimeException(e.getMessage(), e);
            } else {
                DiscordSRV.error(e);
            }
            return null;
        } catch (Exception ex) {
            DiscordSRV.error(ex);
            return null;
        }
    }


    private String getInstalledPlaceholderApiExpansions() {
        if (!PluginUtil.pluginHookIsEnabled("placeholderapi"))
            return "PlaceholderAPI not hooked/no expansions installed";
        File[] extensionFiles = new File(DiscordSRV.getPlugin().getDataFolder().getParentFile(), "PlaceholderAPI/expansions").listFiles();
        if (extensionFiles == null) return "PlaceholderAPI/expansions is not directory/IO error";
        return Arrays.stream(extensionFiles).map(File::getName).collect(Collectors.joining(", "));
    }

    private List<Map<String, String>> getMessages(String prefix, File file) throws IOException {
        List<Map<String, String>> result = new ArrayList<>();
        File[] files = Arrays.stream(file.listFiles()).sorted((file1, file2) -> {
            if (file1.isDirectory() && !file2.isDirectory()) return 1;
            else if (!file1.isDirectory() && file2.isDirectory()) return -1;
            else return file1.getName().compareTo(file2.getName());
        }).toArray(File[]::new);
        for (File listFile : files) {
            if (listFile.getName().endsWith(".json")) {
                result.add(fileMap(prefix + listFile.getName(), Utils.readFile(listFile.getPath())));
            } else if (listFile.isDirectory()) {
                result.addAll(getMessages(prefix + listFile.getName() + "/", listFile));
            }
        }
        return result;
    }

    private List<Map<String, String>> getMessages() throws IOException {
        return getMessages("", core.getMessageManager().getMessagesDirectory().toFile());
    }

}
