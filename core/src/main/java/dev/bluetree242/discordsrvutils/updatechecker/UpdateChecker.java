/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2023 BlueTree242
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

package dev.bluetree242.discordsrvutils.updatechecker;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.platform.PlatformPlayer;
import dev.bluetree242.discordsrvutils.utils.Utils;
import github.scarsz.discordsrv.dependencies.okhttp3.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

@RequiredArgsConstructor
public class UpdateChecker {
    private final DiscordSRVUtils core;

    public void updateCheck() {
        core.getAsyncManager().executeAsync(() -> {
            //do updatechecker
            try {
                if (!core.isEnabled()) return;
                OkHttpClient client = new OkHttpClient();
                JSONObject versionConfig = core.getVersionConfig();
                MultipartBody form = new MultipartBody.Builder().setType(MediaType.get("multipart/form-data"))
                        .addFormDataPart("version", core.getPlatform().getDescription().getVersion())
                        .addFormDataPart("buildNumber", versionConfig.getString("buildNumber"))
                        .addFormDataPart("commit", versionConfig.getString("commit"))
                        .addFormDataPart("buildDate", versionConfig.getString("buildDate"))
                        .build();

                Request req = new Request.Builder().url("https://discordsrvutils.xyz/updatecheck").post(form).build();
                Response response = client.newCall(req).execute();
                JSONObject res = new JSONObject(response.body().string());
                response.close();
                int versions_behind = res.getInt("versions_behind");
                String logger = res.getString("type") != null ? res.getString("type") : "INFO";
                String msg = null;
                if (res.isNull("message")) {
                    if (versions_behind != 0) {
                        if (logger.equalsIgnoreCase("INFO")) {

                        }
                        msg = (Utils.colors("&cPlugin is " + versions_behind + " versions behind. Please Update. Download from " + res.getString("downloadUrl")));
                    } else {
                        msg = (Utils.colors("&aPlugin is up to date!"));
                    }
                } else {
                    //the updatechecker wants its own message
                    String message = res.getString("message");
                    if (message.contains(res.getString("downloadUrl"))) {
                        msg = message;
                    } else {
                        msg = message + " Download from " + res.getString("downloadUrl");
                    }
                }
                switch (logger) {
                    case "INFO":
                        core.getLogger().info(Utils.colors(msg));
                        break;
                    case "WARNING":
                        core.getLogger().warning(Utils.colors(msg));
                        break;
                    case "ERROR":
                        core.getLogger().warning(Utils.colors(msg));
                        break;
                }
            } catch (Exception e) {
                //We could not check for updates.
                core.getLogger().severe("Could not check for updates: " + e.getMessage());
            }

        });
    }

    public void updateCheck(PlatformPlayer p) {
        core.getAsyncManager().executeAsync(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject versionConfig = core.getVersionConfig();
                MultipartBody form = new MultipartBody.Builder().setType(MediaType.get("multipart/form-data"))
                        .addFormDataPart("version", core.getPlatform().getDescription().getVersion())
                        .addFormDataPart("buildNumber", versionConfig.getString("buildNumber"))
                        .addFormDataPart("commit", versionConfig.getString("commit"))
                        .addFormDataPart("buildDate", versionConfig.getString("buildDate"))
                        .addFormDataPart("devUpdatechecker", core.getMainConfig().dev_updatechecker() + "")
                        .build();

                Request req = new Request.Builder().url("https://discordsrvutils.xyz/updatecheck").post(form).build();
                Response response = client.newCall(req).execute();
                JSONObject res = new JSONObject(response.body().string());
                response.close();
                int versions_behind = res.getInt("versions_behind");
                if (res.isNull("message")) {
                    if (versions_behind != 0) {
                        p.sendMessage("&7[&eDSU&7] &cPlugin is " + versions_behind + " versions behind. Please Update. Click to Download");
                    }
                } else {
                    p.sendMessage("&7[&eDSU&7] &c" + res.getString("message"));
                }
            } catch (Exception ignored) {
            }

        });
    }
}
