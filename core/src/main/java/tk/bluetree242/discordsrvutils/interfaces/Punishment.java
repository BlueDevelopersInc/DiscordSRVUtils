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

package tk.bluetree242.discordsrvutils.interfaces;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;

public interface Punishment<O> {

    String getDuration();

    String getOperator();

    String getName();

    String getReason();

    boolean isPermanent();

    O getOrigin();

    PunishmentProvider getPunishmentProvider();

    PunishmentType getPunishmentType();

    boolean isRevoke();

    boolean isIp();

    enum PunishmentProvider {
        ADVANCEDBAN,LITEBANS,LIBERTYBANS
    }

    enum PunishmentType {
        BAN, MUTE
    }

    static void handlePunishment(Punishment punishment, DiscordSRVUtils core) {
        Message msg = null;
        PlaceholdObjectList placeholder = PlaceholdObjectList.ofArray(core, new PlaceholdObject(core, punishment, "punishment"));
        if (!punishment.isRevoke()) {
            switch (punishment.getPunishmentType()) {
                case BAN:
                    if (punishment.isPermanent()) {
                        if (punishment.isIp())
                            core.getMessageManager().getMessage(core.getBansConfig().IPBannedMessage(), placeholder, null).build();
                        else
                            core.getMessageManager().getMessage(core.getBansConfig().bannedMessage(), placeholder, null).build();
                    } else {
                        if (punishment.isIp())
                            core.getMessageManager().getMessage(core.getBansConfig().TempIPBannedMessage(), placeholder, null).build();
                        else
                            core.getMessageManager().getMessage(core.getBansConfig().tempBannedMessage(), placeholder, null).build();
                    }
                    break;
                case MUTE:
                    if (punishment.isPermanent()) {
                        core.getMessageManager().getMessage(core.getBansConfig().MutedMessage(), placeholder, null).build();
                    } else {
                        if (punishment.isIp())
                            core.getMessageManager().getMessage(core.getBansConfig().TempMutedMessage(), placeholder, null).build();
                        else
                            core.getMessageManager().getMessage(core.getBansConfig().tempBannedMessage(), placeholder, null).build();
                    }
                    break;
                default:
                    break;
            }
        } else {
            switch (punishment.getPunishmentType()) {
                case BAN:
                        if (punishment.isIp())
                            core.getMessageManager().getMessage(core.getBansConfig().unipbannedMessage(), placeholder, null).build();
                        else
                            core.getMessageManager().getMessage(core.getBansConfig().unbannedMessage(), placeholder, null).build();

                    break;
                case MUTE:
                            core.getMessageManager().getMessage(core.getBansConfig().unmuteMessage(), placeholder, null).build();
                    break;
                default:
                    break;
            }
        }
        if (msg != null) {
            if (core.getBansConfig().isSendPunishmentmsgesToDiscord()) {
                for (Long id : core.getBansConfig().channel_ids()) {
                    TextChannel channel = core.getJdaManager().getChannel(id);
                    if (channel == null) {
                        core.severe("No channel was found with id " + id + " For Punishment message");
                        return;
                    } else
                        core.queueMsg(msg, channel).queue();
                }
            }
        }
        //TODO: Sync Punishments
    }
    
}
