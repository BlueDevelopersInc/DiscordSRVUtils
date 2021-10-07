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
import github.scarsz.discordsrv.util.PlayerUtil;
import github.scarsz.discordsrv.util.PluginUtil;
import okhttp3.*;
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
import java.security.InvalidKeyException;
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
    private static OkHttpClient client = new OkHttpClient.Builder().build();
    public static String run() {
        try {
            DiscordSRVUtils core = DiscordSRVUtils.get();

            JSONArray data = new JSONArray();
            Map<String, String> information = new HashMap<>();
            information.put("name", "Information");
            information.put("DSU Version", core.getDescription().getVersion());
            information.put("Plugins Hooked", String.join(", " + core.hookedPlugins));
            information.put("DSU Command Executor", Bukkit.getServer().getPluginCommand("discordsrvutils").getPlugin() + "");
            information.put("DiscordSRV Version", DiscordSRV.getPlugin() + "");
            information.put("DiscordSRV Config Version", DiscordSRV.config().getString("ConfigVersion"));
            information.put("JDA Status", (DiscordUtil.getJda() != null && DiscordUtil.getJda().getGatewayPing() != -1 ? DiscordUtil.getJda().getStatus().name() + " / " + DiscordUtil.getJda().getGatewayPing() + "ms" : "build not finished"));
            information.put("Registered listeners", getRegisteredListeners());
            information.put("Channels",  DiscordSRV.getPlugin().getChannels() + "");
            information.put("Console Channel", DiscordSRV.getPlugin().getConsoleChannel() + "");
            information.put("Main Chat Channel", DiscordSRV.getPlugin().getMainChatChannel() + " -> " + DiscordSRV.getPlugin().getMainTextChannel());
            information.put("Discord Guild Roles", (DiscordSRV.getPlugin().getMainGuild() == null ? "invalid main guild" : DiscordSRV.getPlugin().getMainGuild().getRoles().stream().map(Role::toString).collect(Collectors.toList() ) + ""));
            information.put("Vault Groups", Arrays.toString(VaultHook.getGroups()));
            information.put("PlaceholderAPI expansions", getInstalledPlaceholderApiExpansions());
            information.put("/discord command executor", (Bukkit.getServer().getPluginCommand("discord") != null ? Bukkit.getServer().getPluginCommand("discord").getPlugin() + "" : ""));
            information.put("threads",
                    "\n    channel topic updater -> alive: " + (DiscordSRV.getPlugin().getChannelTopicUpdater() != null && DiscordSRV.getPlugin().getChannelTopicUpdater().isAlive()) +
                            "\n    console message queue worker -> alive: " + (DiscordSRV.getPlugin().getConsoleMessageQueueWorker() != null && DiscordSRV.getPlugin().getConsoleMessageQueueWorker().isAlive()) +
                            "\n    server watchdog -> alive: " + (DiscordSRV.getPlugin().getServerWatchdog() != null && DiscordSRV.getPlugin().getServerWatchdog().isAlive()) +
                            "\n    nickname updater -> alive: " + (DiscordSRV.getPlugin().getNicknameUpdater() != null && DiscordSRV.getPlugin().getNicknameUpdater().isAlive())
            );
            information.put("DiscordSRV Hooked Plugins", DiscordSRV.getPlugin().getPluginHooks().stream().map(PluginHook::getPlugin).filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(", ")));
            information.put("Scripts", String.join(", ", SkriptHook.getSkripts()));
            data.put(new JSONObject().put("type", "key_value").put("name", "Information").put("data", MapToKeyValue(information)));
            data.put(new JSONObject().put("type", "files").put("name", "Relevant Lines From Logs").put("data",
                    new JSONArray().put(new JSONObject().put("type", "log").put("name", "Logs").put("content", Utils.b64Encode(getRelevantLinesFromServerLog())))
                    ));
            data.put(new JSONObject().put("type", "key_value").put("name", "System Info").put("data", MapToKeyValue(getSystemInfo())));
            data.put(new JSONObject().put("type", "key_value").put("name", "Server Info").put("data", MapToKeyValue(getServerInfo())));
            data.put(new JSONObject().put("type", "files").put("name", "DiscordSRVUtils Conf Files").put("data", FilesToArray(getDSUFiles())));
            data.put(new JSONObject().put("type", "files").put("name", "DiscordSRV Conf Files").put("data", FilesToArray(getDiscordSRVFiles())));
            List<Map<String, String>> files = new ArrayList<>();
            for (File file : Paths.get(core.getDataFolder() + core.fileseparator + "messages").toFile().listFiles()) {
                if (file.getName().endsWith(".json")) {
                    files.add(fileMap(file.getName(), Utils.readFile(file.getPath())));
                }
            }
            data.put(new JSONObject().put("type", "files").put("name", "DSU Messages Files").put("data", FilesToArray(files)));
            int aesBits = 256;
            String key = RandomStringUtils.randomAlphanumeric(aesBits == 256 ? 32 : 16);
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("data", Utils.b64Encode(encrypt(key.getBytes(), data.toString()))).build();
            Request request = new Request.Builder().post(body).url("https://mcdebug.bluetree242.tk/api/v1/createDebug").build();
            try {
            Response response = client.newCall(request).execute();

                JSONObject bdy = new JSONObject(response.body().string());
                response.close();
                if (response.code() != 200) {
                    return "ERROR: INVALID RESPONSE CODE " + response.code();
                }
                return "https://mcdebug.bluetree242.tk" + "/" + bdy.getString("id") + "#" + key;
         } catch (IOException ex) {
                return "ERROR " + ex.getMessage();
            }


        } catch (Exception ex) {
            return "ERROR " + ex.getMessage();
        }

    }

    private static JSONArray MapToKeyValue(Map<String, String> map) {
        JSONArray output = new JSONArray();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            output.put(new JSONObject().put("key", entry.getKey()).put("value", entry.getValue()));
        }
        return output;
    }

    private static List<Map<String, String>> getDSUFiles() throws Exception{
        List<Map<String, String>> files = new ArrayList<>();
        DiscordSRVUtils core = DiscordSRVUtils.get();
        files.add(fileMap("config.yml", Utils.readFile(core.getDataFolder() + core.fileseparator + "config.yml")));
        files.add(fileMap("PunishmentsIntegration.yml", Utils.readFile(core.getDataFolder() + core.fileseparator + "PunishmentsIntegration.yml")));
        files.add(fileMap("tickets.yml", Utils.readFile(core.getDataFolder() + core.fileseparator + "tickets.yml")));
        files.add(fileMap("leveling.yml", Utils.readFile(core.getDataFolder() + core.fileseparator + "leveling.yml")));
        files.add(fileMap("suggestions.yml", Utils.readFile(core.getDataFolder() + core.fileseparator + "suggestions.yml")));
        return files;
    }



    private static Map<String, String> getServerInfo() {
        Map<String, String> output = new HashMap<>();

        List<String> plugins = Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(Object::toString).sorted().collect(Collectors.toList());

        output.put("server players", PlayerUtil.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
        output.put("server plugins: ", plugins + "");
        output.put("Minecraft version: ", Bukkit.getVersion());
        output.put("Bukkit API version: ", Bukkit.getBukkitVersion());
        output.put("Server online mode: ", Bukkit.getOnlineMode() + "");

        return output;
    }

    private static JSONObject fileMapToObject(Map<String, String> fileMap) {
        JSONObject output = new JSONObject();
        output.put("name", fileMap.get("name"));
        output.put("content", Utils.b64Encode(fileMap.get("content")));
        output.put("type", fileMap.get("type"));
        return output;
    }
    private static JSONArray FilesToArray(List<Map<String, String>> files) {
        JSONArray array = new JSONArray();
        for (Map<String, String> file : files) {
            array.put(fileMapToObject(file));
        }
        return array;
    }
    private static Map<String, String> fileMap(String name, String content) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("content", content);
        map.put("type", name.split("\\.")[name.split("\\.").length -1]);
        return map;
    }

    private static List<Map<String, String>> getDiscordSRVFiles() throws IOException{
        List<Map<String, String>> files = new ArrayList<>();
        files.add(fileMap("config.yml",  FileUtils.readFileToString(DiscordSRV.getPlugin().getConfigFile(), StandardCharsets.UTF_8)));
        files.add(fileMap("config-active.yml",  getActiveConfig()));
        files.add(fileMap("messages.yml",  FileUtils.readFileToString(DiscordSRV.getPlugin().getMessagesFile(), StandardCharsets.UTF_8)));
        files.add(fileMap("voice.yml",  FileUtils.readFileToString(DiscordSRV.getPlugin().getVoiceFile(), StandardCharsets.UTF_8)));
        files.add(fileMap("linking.yml",  FileUtils.readFileToString(DiscordSRV.getPlugin().getLinkingFile(), StandardCharsets.UTF_8)));
        files.add(fileMap("synchronization.yml",  FileUtils.readFileToString(DiscordSRV.getPlugin().getSynchronizationFile(), StandardCharsets.UTF_8)));
        files.add(fileMap("alerts.yml",  FileUtils.readFileToString(DiscordSRV.getPlugin().getAlertsFile(), StandardCharsets.UTF_8)));
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
        return files;
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



    private static Map<String, String> getSystemInfo() {
        Map<String, String> output = new HashMap<>();

        // total number of processors or cores available to the JVM
        output.put("Available processors (cores)", Runtime.getRuntime().availableProcessors() + "");
        // memory
        output.put("Free memory for JVM (MB)", Runtime.getRuntime().freeMemory() / 1024 / 1024 + "");
        output.put("Maximum memory for JVM (MB)", (Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "no limit" : Runtime.getRuntime().maxMemory() / 1024 / 1024) + "");
        output.put("Total memory available for JVM (MB)", Runtime.getRuntime().totalMemory() / 1024 / 1024 + "");

        // drive space
        File serverRoot = DiscordSRV.getPlugin().getDataFolder().getAbsoluteFile().getParentFile().getParentFile();
        output.put("Server storage", "");
        output.put("- total space (MB)", serverRoot.getTotalSpace() / 1024 / 1024 + "");
        output.put("- free space (MB)", serverRoot.getFreeSpace() / 1024 / 1024 + "");
        output.put("- usable space (MB)",  serverRoot.getUsableSpace() / 1024 / 1024 + "");

        // java version
        Map<String, String> systemProperties = ManagementFactory.getRuntimeMXBean().getSystemProperties();
        output.put("Java version", systemProperties.get("java.version"));
        output.put("Java vendor", systemProperties.get("java.vendor") + " " + systemProperties.get("java.vendor.url"));
        output.put("Java home", systemProperties.get("java.home"));
        output.put("Command line", systemProperties.get("sun.java.command"));
        output.put("Time zone", systemProperties.get("user.timezone"));

        return output;
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


    private static String getInstalledPlaceholderApiExpansions() {
        if (!PluginUtil.pluginHookIsEnabled("placeholderapi")) return "PlaceholderAPI not hooked/no expansions installed";
        File[] extensionFiles = new File(DiscordSRV.getPlugin().getDataFolder().getParentFile(), "PlaceholderAPI/expansions").listFiles();
        if (extensionFiles == null) return "PlaceholderAPI/expansions is not directory/IO error";
        return Arrays.stream(extensionFiles).map(File::getName).collect(Collectors.joining(", "));
    }

}
