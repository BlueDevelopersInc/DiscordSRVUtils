package tk.bluetree242.discordsrvutils.config;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.List;

public interface PunishmentsIntegrationConfig {
    @ConfKey("sync_minecraft_punishments_to_discord")
    @ConfDefault.DefaultBoolean(true)
    @ConfComments("#Should we sync Bans plugin punishments to discord?\n" +
            "#Mutes are included.")
    @AnnotationBasedSorter.Order(10)
    boolean isSyncPunishmentsWithDiscord();

    @ConfKey("send_punishment_messages_to_discord")
    @ConfDefault.DefaultBoolean(true)
    @ConfComments("\n#Should we send the message of a person punished on discord?\n#Muted included")
    @AnnotationBasedSorter.Order(20)
    boolean isSendPunishmentmsgesToDiscord();

    @ConfKey("punishment_messages_channel")
    @ConfDefault.DefaultLong(0)
    @ConfComments("\n#The channel to send punishment messages. 0 for default discordsrv's channel")
    @AnnotationBasedSorter.Order(21)
    long channel_id();

    @ConfKey("muted_role")
    @ConfDefault.DefaultLong(0)
    @ConfComments("\n#The role to give when player is muted. Leave 0 to not sync mutes")
    @AnnotationBasedSorter.Order(22)
    long mutedRole();

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

    @ConfKey("sync_minecraft_unpunishments_to_discord")
    @ConfDefault.DefaultBoolean(true)
    @ConfComments("\n\n#Should we send the unpunishments (unban, unmute, unipban) to discord? (User will be unbanned/muted on discord)")
    @AnnotationBasedSorter.Order(80)
    boolean isSyncUnpunishmentsWithDiscord();

    @ConfKey("sync_minecraft_unpunishments_messages_to_discord")
    @ConfDefault.DefaultBoolean(true)
    @ConfComments("\n#Should we send the unpunishments messages on main chat channel?")
    @AnnotationBasedSorter.Order(90)
    boolean isSyncUnpunishmentsmsgWithDiscord();


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
