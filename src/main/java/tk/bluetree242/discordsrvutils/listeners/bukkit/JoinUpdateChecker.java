package tk.bluetree242.discordsrvutils.listeners.bukkit;

import okhttp3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.JSONObject;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.utils.Utils;

public class JoinUpdateChecker implements Listener {


    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().hasPermission("discordsrvutils.updatechecker"))
        Bukkit.getScheduler().runTaskAsynchronously(DiscordSRVUtils.get(), ()-> {
            try {
                OkHttpClient client = new OkHttpClient();
                MultipartBody form = new MultipartBody.Builder().setType(MediaType.get("multipart/form-data")).addFormDataPart("version", DiscordSRVUtils.get().getDescription().getVersion())
                        .build();

                Request req = new Request.Builder().url("https://discordsrvutils.ml/updatecheck").post(form).build();
                Response response = client.newCall(req).execute();
                JSONObject res = new JSONObject(response.body().string());
                response.close();
                int versions_behind = res.getInt("versions_behind");
                if (res.isNull("message")) {
                    if (versions_behind != 0) {
                        e.getPlayer().sendMessage(Utils.colors("&7[&eDSU&7] &c\"" + ChatColor.GREEN + "Plugin is " + versions_behind + " versions behind. Please Update. Download from " + res.getString("downloadUrl")));
                    }
                } else {
                    e.getPlayer().sendMessage(Utils.colors(res.getString("message")));
                }
            } catch (Exception ex) {
                DiscordSRVUtils.get().getLogger().severe("Could not check for updates: " + ex.getMessage());
            }

        });
    }
}
