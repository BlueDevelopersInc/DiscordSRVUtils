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

package dev.bluetree242.discordsrvutils.listeners.game;


import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.platform.events.PlatformJoinEvent;
import dev.bluetree242.discordsrvutils.platform.listener.PlatformListener;
import dev.bluetree242.discordsrvutils.updatechecker.UpdateChecker;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.event.ClickEvent;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.format.NamedTextColor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JoinUpdateChecker extends PlatformListener {
    private final DiscordSRVUtils core;
    private final Component prefix = Component.empty()
            .append(Component.text("[").color(NamedTextColor.GRAY))
            .append(Component.text("DSU").color(NamedTextColor.YELLOW))
            .append(Component.text("]").color(NamedTextColor.GRAY))
            .appendSpace();

    @Override
    public void onJoin(PlatformJoinEvent e) {
        if (e.getPlayer().hasPermission("discordsrvutils.updatechecker")) {
            core.getAsyncManager().executeAsync(() -> {
                try {
                    UpdateChecker.UpdateCheckResult result = core.getUpdateChecker().updateCheck(false);
                    if (result == null) return;
                    if (result.getMessage() != null) e.getPlayer().sendMessage(prefix.append(result.getMessage()));
                    else if (result.getVersionsBehind() > 0) {
                        e.getPlayer().sendMessage(prefix.append(
                                Component.text("You are ").color(NamedTextColor.YELLOW)
                                        .append(Component.text(result.getVersionsBehind()).color(NamedTextColor.RED))
                                        .appendSpace().append(Component.text("versions behind. Update is available at "))
                                        .append(Component.text(result.getDownloadUrl()).color(NamedTextColor.AQUA).clickEvent(ClickEvent.openUrl(result.getDownloadUrl())))

                        ));
                    }
                } catch (Throwable ignored) {
                }
            });
        }
    }
}
