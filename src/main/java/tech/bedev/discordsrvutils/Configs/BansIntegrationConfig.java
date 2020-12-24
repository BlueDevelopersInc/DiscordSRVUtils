package tech.bedev.discordsrvutils.Configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.List;

@ConfHeader("#Bans Integration config.\n#Here you configure how Bans plugin integration works.\n#Currently supported bans plugins: (v.1.1.0)\n#AdvancedBan\n\n")
public interface BansIntegrationConfig {

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

    @ConfKey("banned_message")
    @ConfDefault.DefaultStrings({"**[Player] has been banned by [Operator]**", "**For the reason [Reason]**"})
    @ConfComments("\n\n#Message to send when a person is banned.")
    @AnnotationBasedSorter.Order(30)
    List<String> bannedMessage();

    @ConfKey("temp_banned_message")
    @ConfDefault.DefaultStrings({"**[Player] has been banned by [Operator]**", "**For the reason [Reason]**", "**This player got banned for [Duration]"})
    @ConfComments("\n#Message to send when a person is Temporary banned.")
    @AnnotationBasedSorter.Order(40)
    List<String> tempBannedMessage();

    @ConfKey("ip_banned_message")
    @ConfDefault.DefaultStrings({"**[Player] has been banned by [Operator]**", "**For the reason [Reason]**"})
    @ConfComments("\n#Message to send when a person is IP banned.")
    @AnnotationBasedSorter.Order(50)
    List<String> IPBannedMessage();

    @ConfKey("temp_ip_banned_message")
    @ConfDefault.DefaultStrings({"**[Player] has been banned by [Operator]**", "**For the reason [Reason]**", "**This player got banned for [Duration]"})
    @ConfComments("\n#Message to send when a person is Temporary IP banned.")
    @AnnotationBasedSorter.Order(60)
    List<String> TempIPBannedMessage();

    @ConfKey("muted_message")
    @ConfDefault.DefaultStrings({"**[Player] has been muted by [Operator]**", "**For the reason [Reason]**"})
    @ConfComments("\n#Message to send when a person is Muted.")
    @AnnotationBasedSorter.Order(70)
    List<String> MutedMessage();

    @ConfKey("temp_muted_message")
    @ConfDefault.DefaultStrings({"**[Player] has been muted by [Operator]**", "**For the reason [Reason]**", "**This player was muted for [Duration]**"})
    @ConfComments("\n#Message to send when a person is Temporary Muted.")
    @AnnotationBasedSorter.Order(70)
    List<String> TempMutedMessage();

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
    @ConfDefault.DefaultStrings({"**[Player] has been unbanned by [Operator]**"})
    @AnnotationBasedSorter.Order(100)
    List<String> unbannedMessage();

    @ConfKey("unip_message")
    @ConfComments("\n#Message to send on discord when player is unip banned.")
    @ConfDefault.DefaultStrings({"**[Player] has been unbanned by [Operator]**"})
    @AnnotationBasedSorter.Order(110)
    List<String> unipbannedMessage();

    @ConfKey("unmuted_message")
    @ConfComments("\n#Message to send on discord when player is unip banned.")
    @ConfDefault.DefaultStrings({"**[Player] has been unmuted by [Operator]**"})
    @AnnotationBasedSorter.Order(120)
    List<String> unmuteMessage();


}
