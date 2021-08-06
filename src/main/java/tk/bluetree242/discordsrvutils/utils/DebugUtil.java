package tk.bluetree242.discordsrvutils.utils;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.alexh.weak.Dynamic;
import github.scarsz.discordsrv.dependencies.commons.io.FileUtils;
import github.scarsz.discordsrv.dependencies.commons.lang3.ArrayUtils;
import github.scarsz.discordsrv.dependencies.commons.lang3.RandomStringUtils;
import github.scarsz.discordsrv.dependencies.commons.lang3.StringUtils;
import github.scarsz.discordsrv.dependencies.commons.lang3.exception.ExceptionUtils;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.hooks.PluginHook;
import github.scarsz.discordsrv.hooks.SkriptHook;
import github.scarsz.discordsrv.hooks.VaultHook;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.scarsz.discordsrv.util.ManifestUtil;
import github.scarsz.discordsrv.util.PlayerUtil;
import github.scarsz.discordsrv.util.PluginUtil;
import okhttp3.*;
import okhttp3.OkHttpClient.Builder;
import org.bukkit.Bukkit;
import org.json.JSONArray;
import org.json.JSONObject;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static github.scarsz.discordsrv.util.DebugUtil.b64;

// This idea is taken from discordsrv, and i do not own the bin
// I Copied some of the original discordsrv code for some reason. This code isn't 100% mine
public class DebugUtil {
    private static OkHttpClient client = new OkHttpClient.Builder().callTimeout(1, TimeUnit.MINUTES).build();
    public static String run() {
        try {
            DiscordSRVUtils core = DiscordSRVUtils.get();
            List<Map<String, String>> files = new LinkedList<>();
            files.add(fileMap("info.txt", "Plugin Information", String.join("\n", new String[]{
                    "Plugin Version: " + core.getDescription().getVersion(),
                    "Plugins Hooked: " + String.join(", " + core.hookedPlugins),
                    "DiscordSRVUtils Command Executor: " + Bukkit.getServer().getPluginCommand("discordsrvutils").getPlugin(),
                    "discordsrv version: " + DiscordSRV.getPlugin(),
                    "discordsrv config version: " + DiscordSRV.config().getString("ConfigVersion"),
                    "jda status: " + (DiscordUtil.getJda() != null && DiscordUtil.getJda().getGatewayPing() != -1 ? DiscordUtil.getJda().getStatus().name() + " / " + DiscordUtil.getJda().getGatewayPing() + "ms" : "build not finished"),
                    "Registered listeners: " + getRegisteredListeners(),
                    "channels: " + DiscordSRV.getPlugin().getChannels(),
                    "console channel: " + DiscordSRV.getPlugin().getConsoleChannel(),
                    "main chat channel: " + DiscordSRV.getPlugin().getMainChatChannel() + " -> " + DiscordSRV.getPlugin().getMainTextChannel(),
                    "discord guild roles: " + (DiscordSRV.getPlugin().getMainGuild() == null ? "invalid main guild" : DiscordSRV.getPlugin().getMainGuild().getRoles().stream().map(Role::toString).collect(Collectors.toList())),
                    "vault groups: " + Arrays.toString(VaultHook.getGroups()),
                    "PlaceholderAPI expansions: " + getInstalledPlaceholderApiExpansions(),
                    "/discord command executor: " + (Bukkit.getServer().getPluginCommand("discord") != null ? Bukkit.getServer().getPluginCommand("discord").getPlugin() : ""),
                    "threads:",
                    "    channel topic updater -> alive: " + (DiscordSRV.getPlugin().getChannelTopicUpdater() != null && DiscordSRV.getPlugin().getChannelTopicUpdater().isAlive()),
                    "    console message queue worker -> alive: " + (DiscordSRV.getPlugin().getConsoleMessageQueueWorker() != null && DiscordSRV.getPlugin().getConsoleMessageQueueWorker().isAlive()),
                    "    server watchdog -> alive: " + (DiscordSRV.getPlugin().getServerWatchdog() != null && DiscordSRV.getPlugin().getServerWatchdog().isAlive()),
                    "    nickname updater -> alive: " + (DiscordSRV.getPlugin().getNicknameUpdater() != null && DiscordSRV.getPlugin().getNicknameUpdater().isAlive()),
                    "hooked plugins: " + DiscordSRV.getPlugin().getPluginHooks().stream().map(PluginHook::getPlugin).filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(", ")),
                    "skripts: " + String.join(", ", SkriptHook.getSkripts())
            })));

            files.add(fileMap("relevant-lines-from-server.log", "Lines from Server logs that contain \"discordsrv\"", getRelevantLinesFromServerLog()));
            files.add(fileMap("system-info.txt", null, getSystemInfo()));
            files.add(fileMap("server-info.txt", null, getServerInfo()));
            files.add(fileMap("config.yml", "Plugin Configuration", Utils.readFile(core.getDataFolder() + core.fileseparator + "config.yml")));
            files.add(fileMap("PunishmentsIntegration.yml", "Plugin Punishment Integration Configuration", Utils.readFile(core.getDataFolder() + core.fileseparator + "PunishmentsIntegration.yml")));
            files.add(fileMap("tickets.yml", "Plugin Tickets Configuration", Utils.readFile(core.getDataFolder() + core.fileseparator + "tickets.yml")));
            addDiscordSRVConfigs(files);
            for (File file : Paths.get(core.getDataFolder() + core.fileseparator + "messages").toFile().listFiles()) {
                if (file.getName().endsWith(".json")) {
                    files.add(fileMap(file.getName(), "Message in messages folder", Utils.readFile(file.getPath())));
                }
            }
            files.forEach(map -> {
                String content = map.get("content");
                if (StringUtils.isNotBlank(content)) {
                    // remove sensitive options from files
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
            int aesBits = 256;
            String key = RandomStringUtils.randomAlphanumeric(aesBits == 256 ? 32 : 16);
            byte[] keyBytes = key.getBytes();

            List<Map<String, String>> encryptedFiles = new ArrayList<>();
            for (Map<String, String> file : files) {
                Map<String, String> encryptedFile = new HashMap<>(file);
                encryptedFile.entrySet().removeIf(entry -> StringUtils.isBlank(entry.getValue()));
                encryptedFile.replaceAll((k, v) -> b64(encrypt(keyBytes, file.get(k))));
                encryptedFiles.add(encryptedFile);
            }
            JSONObject req = new JSONObject();
            req.put("description", b64(encrypt(keyBytes, "DiscordSRVUtils Debug Report")));
            JSONArray filesJson = new JSONArray();
            encryptedFiles.forEach(f -> {
                JSONObject toPut = new JSONObject();
                toPut.put("name", f.get("name"));
                toPut.put("content", f.get("content"));
                toPut.put("description", f.get("description"));
                toPut.put("type", f.get("type"));
                filesJson.put(toPut);
            });
            req.put("files", filesJson);
            RequestBody body = RequestBody.create(MediaType.get("application/json"), req.toString());
            Request request = new Request.Builder().post(body).url("https://bin.bluetree242.tk/v1/post").build();
            try {
            Response response = client.newCall(request).execute();
                if (response.code() != 200) {
                    return "ERROR: INVALID RESPONSE CODE";
                }
                JSONObject bdy = new JSONObject(response.body().string());
                response.close();
                return "https://bin.bluetree242.tk" + "/" + bdy.getString("bin") + "#" + key;
         } catch (IOException ex) {
                File debugFolder = new File(core.getDataFolder() + core.fileseparator + "debug");
                if (!debugFolder.exists())
                    debugFolder.mkdir();
                String debugName = "debug-" + System.currentTimeMillis() + ".zip";
                File zipFile = new File(debugFolder, debugName);
                try {
                    ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
                    for (Map<String, String> file : files) {
                        zipOutputStream.putNextEntry(new ZipEntry(file.get("name")));
                        byte[] data = ((String) file.get("content")).getBytes();
                        zipOutputStream.write(data, 0, data.length);
                        zipOutputStream.closeEntry();
                    }
                    zipOutputStream.close();
                    return "Generated To File plugins/DiscordSRVUtils/debug/" + debugName + ". Error uploading:" + ex.getMessage();
                } catch (IOException e) {
                    return "Error putting debug on bin, and saving it on storage, unable to store anywhere. ERROR:" + e.getMessage();
                }
          }


        } catch (Exception e) {
            return "ERROR " + e.getMessage();
        }

    }

    private static String getServerInfo() {
        List<String> output = new LinkedList<>();

        List<String> plugins = Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(Object::toString).sorted().collect(Collectors.toList());

        output.add("server players: " + PlayerUtil.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
        output.add("server plugins: " + plugins);
        output.add("");
        output.add("Minecraft version: " + Bukkit.getVersion());
        output.add("Bukkit API version: " + Bukkit.getBukkitVersion());
        output.add("Server online mode: " + Bukkit.getOnlineMode());

        return String.join("\n", output);
    }

    private static Map<String, String> fileMap(String name, String description, String content) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("description", description);
        map.put("content", content);
        map.put("type", "text/plain");
        return map;
    }

    private static void addDiscordSRVConfigs(List files) throws IOException{
        files.add(fileMap("DiscordSRVs-config.yml", "raw plugins/DiscordSRV/config.yml", FileUtils.readFileToString(DiscordSRV.getPlugin().getConfigFile(), StandardCharsets.UTF_8)));
        files.add(fileMap("config-active.yml", "active plugins/DiscordSRV/config.yml", getActiveConfig()));
        files.add(fileMap("messages.yml", "raw plugins/DiscordSRV/messages.yml", FileUtils.readFileToString(DiscordSRV.getPlugin().getMessagesFile(), StandardCharsets.UTF_8)));
        files.add(fileMap("voice.yml", "raw plugins/DiscordSRV/voice.yml", FileUtils.readFileToString(DiscordSRV.getPlugin().getVoiceFile(), StandardCharsets.UTF_8)));
        files.add(fileMap("linking.yml", "raw plugins/DiscordSRV/linking.yml", FileUtils.readFileToString(DiscordSRV.getPlugin().getLinkingFile(), StandardCharsets.UTF_8)));
        files.add(fileMap("synchronization.yml", "raw plugins/DiscordSRV/synchronization.yml", FileUtils.readFileToString(DiscordSRV.getPlugin().getSynchronizationFile(), StandardCharsets.UTF_8)));
        files.add(fileMap("alerts.yml", "raw plugins/DiscordSRV/alerts.yml", FileUtils.readFileToString(DiscordSRV.getPlugin().getAlertsFile(), StandardCharsets.UTF_8)));
    }

    private static String getActiveConfig() {
        try {
            Dynamic activeConfig = DiscordSRV.config().getProvider("config").getValues();
            StringBuilder stringBuilder = new StringBuilder(500);
            Iterator<Dynamic> iterator = activeConfig.allChildren().iterator();
            while (iterator.hasNext()) {
                Dynamic child = iterator.next();
                if (child.allChildren().count() == 0) {
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

    private static String getDescriptionHtml(String discordsrv) {
        //language=html
        return "DiscordSRVUtils Debug" +
                "<br>\n" +
                "<h3>DiscordSRV</h3> \n" +
                "<code><a href='" + discordsrv + "' style=\"color:white\" target='_blank'>Click Here</a></code>";
    }

    private static String getSystemInfo() {
        List<String> output = new LinkedList<>();

        // total number of processors or cores available to the JVM
        output.add("Available processors (cores): " + Runtime.getRuntime().availableProcessors());
        output.add("");

        // memory
        output.add("Free memory for JVM (MB): " + Runtime.getRuntime().freeMemory() / 1024 / 1024);
        output.add("Maximum memory for JVM (MB): " + (Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "no limit" : Runtime.getRuntime().maxMemory() / 1024 / 1024));
        output.add("Total memory available for JVM (MB): " + Runtime.getRuntime().totalMemory() / 1024 / 1024);
        output.add("");

        // drive space
        File serverRoot = DiscordSRV.getPlugin().getDataFolder().getAbsoluteFile().getParentFile().getParentFile();
        output.add("Server storage:");
        output.add("- total space (MB): " + serverRoot.getTotalSpace() / 1024 / 1024);
        output.add("- free space (MB): " + serverRoot.getFreeSpace() / 1024 / 1024);
        output.add("- usable space (MB): " + serverRoot.getUsableSpace() / 1024 / 1024);
        output.add("");

        // java version
        Map<String, String> systemProperties = ManagementFactory.getRuntimeMXBean().getSystemProperties();
        output.add("Java version: " + systemProperties.get("java.version"));
        output.add("Java vendor: " + systemProperties.get("java.vendor") + " " + systemProperties.get("java.vendor.url"));
        output.add("Java home: " + systemProperties.get("java.home"));
        output.add("Command line: " + systemProperties.get("sun.java.command"));
        output.add("Time zone: " + systemProperties.get("user.timezone"));

        return String.join("\n", output);
    }

    private static String getRegisteredListeners() {
        if (!DiscordSRVUtils.get().isReady()) return "DSU not ready";
        StringJoiner joiner = new StringJoiner(", ");
        for (Object listener : DiscordSRVUtils.get().getJDA().getEventManager().getRegisteredListeners()) {
            joiner.add(listener.getClass().getSimpleName());
        }
        return joiner.toString();
    }


    private static String getRelevantLinesFromServerLog() {
            List<String> output = new LinkedList<>();
            try {
                FileReader fr = new FileReader(new File("logs/latest.log"));
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


    public static byte[] encrypt(byte[] key, String data){
        try {
            return encrypt(key, data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static final SecureRandom RANDOM = new SecureRandom();
    public static byte[] encrypt(byte[] key, byte[] data) throws Exception{
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            byte[] iv = new byte[cipher.getBlockSize()];
            RANDOM.nextBytes(iv);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(data);
            return ArrayUtils.addAll(iv, encrypted);
    }

    private static String getInstalledPlaceholderApiExpansions() {
        if (!PluginUtil.pluginHookIsEnabled("placeholderapi")) return "PlaceholderAPI not hooked/no expansions installed";
        File[] extensionFiles = new File(DiscordSRV.getPlugin().getDataFolder().getParentFile(), "PlaceholderAPI/expansions").listFiles();
        if (extensionFiles == null) return "PlaceholderAPI/expansions is not directory/IO error";
        return Arrays.stream(extensionFiles).map(File::getName).collect(Collectors.joining(", "));
    }

}
