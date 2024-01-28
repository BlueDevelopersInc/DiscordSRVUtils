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

package dev.bluetree242.discordsrvutils.updatechecker;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.VersionInfo;
import dev.bluetree242.discordsrvutils.exceptions.InvalidResponseException;
import dev.bluetree242.discordsrvutils.utils.Utils;
import github.scarsz.discordsrv.dependencies.jackson.annotation.JsonIgnoreProperties;
import github.scarsz.discordsrv.dependencies.jackson.annotation.JsonProperty;
import github.scarsz.discordsrv.dependencies.jackson.databind.ObjectMapper;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.format.NamedTextColor;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.minimessage.MiniMessage;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.serializer.ComponentSerializer;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import github.scarsz.discordsrv.dependencies.okhttp3.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

@RequiredArgsConstructor
public class UpdateChecker {
    private final DiscordSRVUtils core;
    private final ObjectMapper objectMapper = Utils.OBJECT_MAPPER;

    public @Nullable UpdateCheckResult updateCheck(boolean startup) {
        if (!System.getProperty("discordsrvutils.updatecheck", "true").equalsIgnoreCase("true")) return null;
        UpdateCheckResult result = runRequest(startup);
        if (startup || result.getLogLevel() != Level.INFO) {
            core.getLogger().log(result.getLogLevel(), result.getConsoleMessage());
        }
        return result;
    }

    private UpdateCheckResult runRequest(boolean startup) {
        OkHttpClient client = new OkHttpClient();
        MultipartBody form = new MultipartBody.Builder().setType(MediaType.get("multipart/form-data"))
                .addFormDataPart("updatecheckVersion", "1")
                .addFormDataPart("version", core.getPlatform().getDescription().getVersion())
                .addFormDataPart("buildNumber", VersionInfo.BUILD_NUMBER)
                .addFormDataPart("commit", VersionInfo.COMMIT)
                .addFormDataPart("buildDate", VersionInfo.BUILD_DATE)
                .addFormDataPart("devUpdatechecker", String.valueOf(core.getMainConfig().dev_updatechecker()))
                .build();

        Request req = new Request.Builder().url(System.getProperty("discordsrvutils.updatecheck.url", "https://discordsrvutils.xyz/updatecheck"))
                .post(form)
                .addHeader("Accept", "application/json")
                .build();
        try (Response response = client.newCall(req).execute()) {
            String body = response.body().string();
            if (response.code() != 200) throw new InvalidResponseException(body, null);
            UpdateCheckResult result = objectMapper.readValue(body, UpdateCheckResult.class);
            response.close();
            return result;
        } catch (Throwable e) {
            throw new InvalidResponseException(e.getMessage(), e);
        }
    }


    @RequiredArgsConstructor
    public enum MessageFormat {
        LEGACY(LegacyComponentSerializer.builder().character('&').extractUrls().build()),
        MINIMESSAGE(MiniMessage.miniMessage());
        private final ComponentSerializer<Component, ? extends Component, String> serializer;
    }

    @SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UpdateCheckResult {
        @Getter
        private String downloadUrl;

        @Getter
        @JsonProperty(required = true, value = "versions_behind")
        private int versionsBehind;

        @Getter
        private boolean shouldDisable = true;

        private String message;

        private String consoleMessage;

        private String type = "INFO";

        @Getter
        private MessageFormat messageFormat = MessageFormat.LEGACY; // Default is legacy because updatechecker may not return this value and therefore assumes an older version for some reason.

        public Component getMessage() {
            if (message == null) return null;
            return messageFormat.serializer.deserialize(message);
        }

        public Level getLogLevel() {
            if (type.equalsIgnoreCase("INFO")) return Level.INFO;
            return Level.SEVERE;
        }

        public String getConsoleMessage() {
            Component result;
            if (consoleMessage == null && message == null) {
                result = getVersionsBehind() <= 0 ?
                        Component.text("You are up to date.", NamedTextColor.GREEN) :
                        Component.text("You are ", NamedTextColor.RED)
                                .append(Component.text(getVersionsBehind(), NamedTextColor.DARK_RED))
                                .append(Component.text(" versions behind. Update is available at "))
                                .append(Component.text(getDownloadUrl(), NamedTextColor.AQUA));
            } else if (consoleMessage == null) result = getMessage();
            else result = messageFormat.serializer.deserialize(consoleMessage);
            return Utils.toAnsi(result);
        }
    }
}
