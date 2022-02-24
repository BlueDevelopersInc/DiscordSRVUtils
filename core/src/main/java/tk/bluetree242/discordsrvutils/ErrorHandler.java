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

package tk.bluetree242.discordsrvutils;

import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageChannel;
import github.scarsz.discordsrv.util.DebugUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import tk.bluetree242.discordsrvutils.embeds.Embed;
import tk.bluetree242.discordsrvutils.platform.PlatformPlayer;
import tk.bluetree242.discordsrvutils.utils.Utils;


@RequiredArgsConstructor
public class ErrorHandler {
    private final DiscordSRVUtils core;
    @Getter
    private String finalError = null;
    @Getter
    private long lastErrorTime = 0;

    protected void startupError(Throwable ex, @NotNull String msg) {
        core.getPlatform().disable();
        core.getLogger().warning(msg);
        try {
            //create a debug report, we know commands don't work after plugin is disabled
            core.getLogger().severe(core.getPlatform().getServer().getDebugger().run(Utils.exceptionToStackTrack(ex)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //tell them where to report
        core.getLogger().severe("Send this to support at https://discordsrvutils.xyz/support");
        ex.printStackTrace();
    }

    public void defaultHandle(Throwable ex) {
        //handle error on thread pool
        if (!core.getMainConfig().minimize_errors()) {
            core.getLogger().warning("The following error have a high chance to be caused by DiscordSRVUtils. Report at https://discordsrvutils.xyz/support and not discordsrv's Discord.");

            ex.printStackTrace();
            core.getLogger().warning("Read the note above the error Please.");
            //don't spam errors
            if ((System.currentTimeMillis() - lastErrorTime) >= 180000)
                for (PlatformPlayer p : core.getServer().getOnlinePlayers()) {
                    if (p.hasPermission("discordsrvutils.errornotifications")) {
                        //tell admins that something was wrong
                        p.sendMessage("&7[&eDSU&7] Plugin had an error. Check console for details. Support at https://discordsrvutils.xyz/support");
                    }
                }
            lastErrorTime = System.currentTimeMillis();

        } else {
            core.getLogger().severe("DiscordSRVUtils had an error. Error minimization enabled.");
        }
        finalError = Utils.exceptionToStackTrack(ex);
    }

    public void defaultHandle(Throwable ex, MessageChannel channel) {
        //send message for errors
        channel.sendMessage(Embed.error("An error happened. Check Console for details")).queue();
        core.getLogger().severe("The following error have a high chance to be caused by DiscordSRVUtils. Report at https://discordsrvutils.xyz/support and not discordsrv's Discord.");
        ex.printStackTrace();
    }

    public void severe(String sv) {
        core.getLogger().severe(sv);
        for (PlatformPlayer p : core.getPlatform().getServer().getOnlinePlayers()) {
            if (p.hasPermission("discordsrvutils.errornotifications"))
                //tell admins that something was wrong
                p.sendMessage("&7[&eDSU&7] &c" + sv);
        }
    }
}
