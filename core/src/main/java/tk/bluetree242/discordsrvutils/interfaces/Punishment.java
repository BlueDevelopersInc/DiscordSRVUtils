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

import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;

import java.util.UUID;

public interface Punishment<O> {

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
        // SYNC PUNISHMENT
        String id = core.getDiscordSRV().getDiscordId(punishment.getTargetUUID());
        if (id == null) return;
        User discordUser = core.getJDA().retrieveUserById(id).complete();
        if (punishment.isRevoke()) {
            Member discordMember = core.getPlatform().getDiscordSRV().getMainGuild().retrieveMember(discordUser).complete();
            if (discordMember == null) return;
            if (!core.getPlatform().getDiscordSRV().getMainGuild().getSelfMember().canInteract(discordMember)) return;
            if (!core.getBansConfig().isSyncPunishmentsWithDiscord()) return;
            switch (punishment.getPunishmentType()) {
                case BAN:
                    Role bannedRole = core.getPlatform().getDiscordSRV().getMainGuild().getRoleById(core.getBansConfig().bannedRole());
                    if (bannedRole == null)
                        core.getPlatform().getDiscordSRV().getMainGuild().ban(discordUser, 0, "Minecraft Synced Ban").queue();
                    else if (core.getPlatform().getDiscordSRV().getMainGuild().getSelfMember().canInteract(bannedRole))
                        core.getPlatform().getDiscordSRV().getMainGuild().addRoleToMember(discordMember, bannedRole).reason("Minecraft Synced Ban").queue();
                    else {
                        core.severe("Could not add Banned role to " + discordUser.getName() + ". Please make sure the bot's role is higher than the banned role");
                    }
                    break;
                case MUTE:
                    Role role = core.getPlatform().getDiscordSRV().getMainGuild().getRoleById(core.getBansConfig().mutedRole());
                    if (role == null) {
                        if (core.getBansConfig().mutedRole() != 0)
                            core.severe("No Role was found with id " + core.getBansConfig().mutedRole());
                        return;
                    }
                    core.getPlatform().getDiscordSRV().getMainGuild().addRoleToMember(discordUser.getIdLong(), role).reason("Mute Synced with Minecraft").queue();
                    break;
                default:
                    break;
            }
        } else {
            if (!core.getBansConfig().isSyncUnpunishmentsWithDiscord()) return;
            switch (punishment.getPunishmentType()) {
                case BAN:
                    Role bannedRole = core.getPlatform().getDiscordSRV().getMainGuild().getRoleById(core.getBansConfig().bannedRole());
                    if (bannedRole == null)
                        core.getPlatform().getDiscordSRV().getMainGuild().unban(discordUser).reason("Minecraft Synced UnBan").queue();
                    else if (core.getPlatform().getDiscordSRV().getMainGuild().getSelfMember().canInteract(bannedRole))
                        core.getPlatform().getDiscordSRV().getMainGuild().removeRoleFromMember(discordUser.getIdLong(), bannedRole).reason("Minecraft Synced UnBan").queue();
                    else {
                        core.severe("Could not remove Banned role from " + discordUser.getName() + ". Please make sure the bot's role is higher than the banned role");
                    }
                    break;
                case MUTE:
                    Role role = core.getPlatform().getDiscordSRV().getMainGuild().getRoleById(core.getBansConfig().mutedRole());
                    if (role == null) {
                        if (core.getBansConfig().mutedRole() != 0)
                            core.severe("No Role was found with id " + core.getBansConfig().mutedRole());
                        return;
                    }
                    core.getPlatform().getDiscordSRV().getMainGuild().removeRoleFromMember(discordUser.getIdLong(), role).reason("Unmute Synced with Minecraft").queue();
                default:
                    break;
            }
        }
    }

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

    UUID getTargetUUID();

    enum PunishmentProvider {
        ADVANCEDBAN, LITEBANS, LIBERTYBANS
    }

    enum PunishmentType {
        BAN, MUTE
    }

}
