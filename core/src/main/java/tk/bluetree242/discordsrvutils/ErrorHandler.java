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
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import tk.bluetree242.discordsrvutils.embeds.Embed;
import tk.bluetree242.discordsrvutils.platform.PlatformPlayer;
import tk.bluetree242.discordsrvutils.utils.DebugUtil;
import tk.bluetree242.discordsrvutils.utils.Utils;

import java.util.logging.Logger;

public class ErrorHandler {
    private DiscordSRVUtils core = DiscordSRVUtils.get();
    private Logger logger = core.getLogger();
    @Getter
    private String finalError = null;
    @Getter
    private long lastErrorTime = 0;
    protected void startupError(Throwable ex, @NotNull String msg) {
        DiscordSRVUtils.getPlatform().disable();
        logger.warning(msg);
        try {
            //create a debug report, we know commands don't work after plugin is disabled
            logger.severe(DebugUtil.run(Utils.exceptionToStackTrack(ex)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //tell them where to report
        logger.severe("Send this to support at https://discordsrvutils.xyz/support");
        ex.printStackTrace();
    }

    public void defaultHandle(Throwable ex) {
        //handle error on thread pool
        if (!core.getMainConfig().minimize_errors()) {
            logger.warning("The following error have a high chance to be caused by DiscordSRVUtils. Report at https://discordsrvutils.xyz/support and not discordsrv's Discord.");

            ex.printStackTrace();
            logger.warning("Read the note above the error Please.");
            //don't spam errors
            if ((System.currentTimeMillis() - lastErrorTime) >= 180000)
                for (PlatformPlayer p : DiscordSRVUtils.getServer().getOnlinePlayers()) {
                    if (p.hasPermission("discordsrvutils.errornotifications")) {
                        //tell admins that something was wrong
                        p.sendMessage("&7[&eDSU&7] Plugin had an error. Check console for details. Support at https://discordsrvutils.xyz/support");
                    }
                }
            lastErrorTime = System.currentTimeMillis();

        } else {
            logger.severe("DiscordSRVUtils had an error. Error minimization enabled.");
        }
        finalError = Utils.exceptionToStackTrack(ex);
    }

    public void defaultHandle(Throwable ex, MessageChannel channel) {
        //send message for errors
        channel.sendMessage(Embed.error("An error happened. Check Console for details")).queue();
        logger.severe("The following error have a high chance to be caused by DiscordSRVUtils. Report at https://discordsrvutils.xyz/support and not discordsrv's Discord.");
        ex.printStackTrace();
    }

    public void severe(String sv) {
        core.getLogger().severe(sv);
        for (PlatformPlayer p : DiscordSRVUtils.getPlatform().getServer().getOnlinePlayers()) {
            if (p.hasPermission("discordsrvutils.errornotifications"))
                //tell admins that something was wrong
                p.sendMessage("&7[&eDSU&7] &c" + sv);
        }
    }
}
