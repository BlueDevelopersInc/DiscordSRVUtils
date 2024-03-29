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

package dev.bluetree242.discordsrvutils.interfaces;

import dev.bluetree242.discordsrvutils.DiscordSRVUtils;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import dev.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import dev.bluetree242.discordsrvutils.utils.Utils;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;

import javax.annotation.Nullable;
import java.util.UUID;

public interface Punishment<O> {

    static void handlePunishment(Punishment punishment, DiscordSRVUtils core) {
        if (punishment.getPunishmentType() == PunishmentType.UNKNOWN) return;
        announcePunishment(punishment, core);
        syncPunishment(punishment, core);
    }

    static void announcePunishment(Punishment punishment, DiscordSRVUtils core) {
        Message msg = null;
        PlaceholdObjectList placeholder = PlaceholdObjectList.ofArray(core, new PlaceholdObject(core, punishment, "punishment"));
        if (punishment.isGrant()) {
            switch (punishment.getPunishmentType()) {
                case BAN:
                    if (punishment.isPermanent()) {
                        if (punishment.isIp())
                            msg = punishmentMsg(core, core.getBansConfig().IPBannedMessage(), placeholder);
                        else
                            msg = punishmentMsg(core, core.getBansConfig().bannedMessage(), placeholder);
                    } else {
                        if (punishment.isIp())
                            msg = punishmentMsg(core, core.getBansConfig().tempIPBannedMessage(), placeholder);
                        else
                            msg = punishmentMsg(core, core.getBansConfig().tempBannedMessage(), placeholder);
                    }
                    break;
                case MUTE:
                    if (punishment.isPermanent()) {
                        msg = punishmentMsg(core, core.getBansConfig().mutedMessage(), placeholder);
                    } else {
                        // There is no temp ip mute message for now
                        msg = punishmentMsg(core, core.getBansConfig().tempMutedMessage(), placeholder);
                    }
                    break;
                case WARN:
                    msg = core.getMessageManager().getMessage(core.getBansConfig().warnedMessage(), placeholder, null).build();
                    break;
                case KICK:
                    msg = core.getMessageManager().getMessage(core.getBansConfig().kick_message(), placeholder, null).build();
                    break;
                default:
                    break;
            }
        } else {
            switch (punishment.getPunishmentType()) {
                case BAN:
                    if (punishment.isIp())
                        msg = punishmentMsg(core, core.getBansConfig().unIPBannedMessage(), placeholder);
                    else
                        msg = punishmentMsg(core, core.getBansConfig().unbannedMessage(), placeholder);
                    break;
                case MUTE:
                    msg = punishmentMsg(core, core.getBansConfig().unmuteMessage(), placeholder);
                    break;
                default:
                    break;
            }
        }
        if (msg != null) {
            if (core.getBansConfig().isSendPunishmentMsgsToDiscord()) {
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
    }

    static Message punishmentMsg(DiscordSRVUtils core, String s, PlaceholdObjectList p) {
        if (s.isEmpty()) return null; // Not send it
        return core.getMessageManager().getMessage(s, p, null).build();
    }

    static void syncPunishment(Punishment punishment, DiscordSRVUtils core) {
        if (!punishment.isPermanent() && !core.getBansConfig().isSyncTempPunishments()) return;
        String id = core.getDiscordSRV().getDiscordId(punishment.getTargetUUID());
        if (id == null) return;
        User discordUser = core.getJDA().retrieveUserById(id).complete();
        if (punishment.isGrant()) {
            Member discordMember = Utils.retrieveMember(core.getDiscordSRV().getMainGuild(), discordUser.getIdLong());
            if (discordMember == null) return;
            if (!core.getPlatform().getDiscordSRV().getMainGuild().getSelfMember().canInteract(discordMember)) return;
            if (!core.getBansConfig().isSyncPunishmentsWithDiscord()) return;
            switch (punishment.getPunishmentType()) {
                case BAN:
                    Role bannedRole = core.getPlatform().getDiscordSRV().getMainGuild().getRoleById(core.getBansConfig().bannedRole());
                    if (bannedRole == null)
                        core.getPlatform().getDiscordSRV().getMainGuild().ban(discordUser, 0, "Minecraft Synced Ban").queue();
                    else if (core.getPlatform().getDiscordSRV().getMainGuild().getSelfMember().canInteract(bannedRole))
                        core.getPlatform().getDiscordSRV().getMainGuild().addRoleToMember(discordMember, bannedRole).reason("Minecraft synced ban").queue();
                    else {
                        core.severe("Could not add banned role to " + discordUser.getName() + ". Please make sure the bot's role is higher than the banned role");
                    }
                    break;
                case MUTE:
                    Role role = core.getPlatform().getDiscordSRV().getMainGuild().getRoleById(core.getBansConfig().mutedRole());
                    if (role == null) {
                        if (core.getBansConfig().mutedRole() != 0)
                            core.severe("No role was found with id " + core.getBansConfig().mutedRole());
                        return;
                    }
                    core.getPlatform().getDiscordSRV().getMainGuild().addRoleToMember(discordUser.getIdLong(), role).reason("Mute synced with minecraft").queue();
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
                        core.getPlatform().getDiscordSRV().getMainGuild().unban(discordUser).reason("Minecraft synced unban").queue();
                    else if (core.getPlatform().getDiscordSRV().getMainGuild().getSelfMember().canInteract(bannedRole))
                        core.getPlatform().getDiscordSRV().getMainGuild().removeRoleFromMember(discordUser.getIdLong(), bannedRole).reason("Minecraft synced unban").queue();
                    else {
                        core.severe("Could not remove banned role from " + discordUser.getName() + ". Please make sure the bot's role is higher than the banned role");
                    }
                    break;
                case MUTE:
                    Role role = core.getPlatform().getDiscordSRV().getMainGuild().getRoleById(core.getBansConfig().mutedRole());
                    if (role == null) {
                        if (core.getBansConfig().mutedRole() != 0)
                            core.severe("No role was found with id " + core.getBansConfig().mutedRole());
                        return;
                    }
                    core.getPlatform().getDiscordSRV().getMainGuild().removeRoleFromMember(discordUser.getIdLong(), role).reason("unmute synced with minecraft").queue();
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

    boolean isGrant();

    boolean isIp();

    UUID getTargetUUID();

    default @Nullable String getRevoker() {
        return null;
    }

    enum PunishmentProvider {
        ADVANCEDBAN, LITEBANS, LIBERTYBANS
    }

    enum PunishmentType {
        BAN, MUTE, WARN, KICK, UNKNOWN;

        public static PunishmentType get(String name) {
            try {
                return valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                return UNKNOWN;
            }
        }
    }

}
