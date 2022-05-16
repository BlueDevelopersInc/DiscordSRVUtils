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

package tk.bluetree242.discordsrvutils.config;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.Set;

public interface PunishmentsIntegrationConfig {
    @ConfKey("sync_minecraft_punishments_to_discord")
    @ConfDefault.DefaultBoolean(true)
    @ConfComments("#Should we sync Bans plugin punishments to discord?\n" +
            "#Mutes are included.")
    @AnnotationBasedSorter.Order(10)
    boolean isSyncPunishmentsWithDiscord();

    @ConfKey("send_punishment_messages_to_discord")
    @ConfDefault.DefaultBoolean(true)
    @ConfComments("\n#Should we send the message of a person punished on discord? Disabling this disabling any messages (even the ones below)")
    @AnnotationBasedSorter.Order(20)
    boolean isSendPunishmentMsgsToDiscord();

    @ConfKey("send_mute_messages_to_discord")
    @ConfDefault.DefaultBoolean(true)
    @ConfComments("\n#Should we send the message of a person muted on discord?")
    @AnnotationBasedSorter.Order(21)
    boolean isSendMuteMsgsToDiscord();

    @ConfKey("punishment_messages_channels")
    @ConfDefault.DefaultLongs({0})
    @ConfComments("\n#The Channels to send punishment messages in, add 0 for discordsrv main chat channel")
    @AnnotationBasedSorter.Order(21)
    Set<Long> channel_ids();

    @ConfKey("muted_role")
    @ConfDefault.DefaultLong(0)
    @ConfComments("\n#The role to give when player is muted. Leave 0 to not sync mutes")
    @AnnotationBasedSorter.Order(22)
    long mutedRole();

    @ConfKey("banned_role")
    @ConfDefault.DefaultLong(0)
    @ConfComments("\n#The role to give when player is muted. Will ban if this role id wasn't found")
    @AnnotationBasedSorter.Order(23)
    long bannedRole();

    @ConfKey("banned_message")
    @ConfDefault.DefaultString("message:ban")
    @ConfComments("\n\n#Message to send when a person is banned.")
    @AnnotationBasedSorter.Order(30)
    String bannedMessage();

    @ConfKey("temp_banned_message")
    @ConfDefault.DefaultString("message:ban")
    @ConfComments("\n#Message to send when a person is Temporary banned.")
    @AnnotationBasedSorter.Order(40)
    String tempBannedMessage();

    @ConfKey("ip_banned_message")
    @ConfDefault.DefaultString("message:ban")
    @ConfComments("\n#Message to send when a person is IP banned.")
    @AnnotationBasedSorter.Order(50)
    String IPBannedMessage();

    @ConfKey("temp_ip_banned_message")
    @ConfDefault.DefaultString("message:ban")
    @ConfComments("\n#Message to send when a person is Temporary IP banned.")
    @AnnotationBasedSorter.Order(60)
    String TempIPBannedMessage();

    @ConfKey("muted_message")
    @ConfDefault.DefaultString("message:mute")
    @ConfComments("\n#Message to send when a person is Muted.")
    @AnnotationBasedSorter.Order(70)
    String MutedMessage();

    @ConfKey("temp_muted_message")
    @ConfDefault.DefaultString("message:mute")
    @ConfComments("\n#Message to send when a person is Temporary Muted.")
    @AnnotationBasedSorter.Order(70)
    String TempMutedMessage();

    @ConfKey("sync_minecraft_temp_punishments")
    @ConfDefault.DefaultBoolean(true)
    @ConfComments("\n#Should we sync the temp punishments to discord?")
    @AnnotationBasedSorter.Order(71)
    boolean isSyncTempPunishments();

    @ConfKey("sync_minecraft_unpunishments_to_discord")
    @ConfDefault.DefaultBoolean(true)
    @ConfComments("\n\n#Should we send the unpunishments (unban, unmute, unipban) to discord? (User will be unbanned/muted on discord)")
    @AnnotationBasedSorter.Order(80)
    boolean isSyncUnpunishmentsWithDiscord();

    @ConfKey("sync_minecraft_unpunishments_messages_to_discord")
    @ConfDefault.DefaultBoolean(true)
    @ConfComments("\n#Should we send the unpunishments messages on main chat channel?")
    @AnnotationBasedSorter.Order(90)
    boolean isSyncUnpunishmentsMsgWithDiscord();


    @ConfKey("unbanned_message")
    @ConfComments("\n#Message to send on discord when player is unbanned.")
    @ConfDefault.DefaultString("message:unban")
    @AnnotationBasedSorter.Order(100)
    String unbannedMessage();

    @ConfKey("unip_message")
    @ConfComments("\n#Message to send on discord when player is unip banned.")
    @ConfDefault.DefaultString("message:unban")
    @AnnotationBasedSorter.Order(110)
    String unipbannedMessage();

    @ConfKey("unmuted_message")
    @ConfComments("\n#Message to send on discord when player is unmuted.")
    @ConfDefault.DefaultString("message:unmute")
    @AnnotationBasedSorter.Order(120)
    String unmuteMessage();
}
