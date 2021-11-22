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

package tk.bluetree242.discordsrvutils.listeners.bukkit;

import github.scarsz.discordsrv.dependencies.okhttp3.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
            Bukkit.getScheduler().runTaskAsynchronously(DiscordSRVUtils.get(), () -> {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject versionConfig = DiscordSRVUtils.get().getVersionConfig();
                    MultipartBody form = new MultipartBody.Builder().setType(MediaType.get("multipart/form-data"))
                            .addFormDataPart("version", DiscordSRVUtils.get().getDescription().getVersion())
                            .addFormDataPart("buildNumber", versionConfig.getString("buildNumber"))
                            .addFormDataPart("commit", versionConfig.getString("commit"))
                            .build();

                    Request req = new Request.Builder().url("https://discordsrvutils.xyz/updatecheck").post(form).build();
                    Response response = client.newCall(req).execute();
                    JSONObject res = new JSONObject(response.body().string());
                    response.close();
                    int versions_behind = res.getInt("versions_behind");
                    if (res.isNull("message")) {
                        if (versions_behind != 0) {
                            Player p = e.getPlayer();
                            TextComponent msg = new TextComponent(Utils.colors("&7[&eDSU&7] &cPlugin is " + versions_behind + " versions behind. Please Update. Click to Download"));
                            msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, res.getString("downloadUrl")));
                            msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(net.md_5.bungee.api.ChatColor.GREEN + "" + net.md_5.bungee.api.ChatColor.BOLD + "Click to download Update").create()));
                            p.spigot().sendMessage(msg);
                        }
                    } else {
                        e.getPlayer().sendMessage(Utils.colors("&7[&eDSU&7] &c" + res.getString("message")));
                    }
                } catch (Exception ex) {
                    DiscordSRVUtils.get().getLogger().severe("Could not check for updates: " + ex.getMessage());
                }

            });
    }
}
