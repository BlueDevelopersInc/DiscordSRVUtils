package tech.bedev.discordsrvutils.Configs;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfDefault.DefaultLong;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

import java.util.List;

@ConfHeader("#Moderation config. This config is for Moderation commands and their settings. And users allowed to use them.\n" +
        "#Bans plugins are not supported yet.")
public interface ModerationConfig {
    @ConfKey("discord-moderator-commands")
    @ConfDefault.DefaultBoolean(true)
    @ConfComments("#Should we allow mods to use Moderator commands to moderate?\n")
    @AnnotationBasedSorter.Order(10)
    boolean isModeratorCommandsEnabled();

    @ConfKey("roles_allowed_to_use_moderator_commands")
    @ConfDefault.DefaultStrings({"Admin", "Moderator", "Owner", "CO-Owner"})
    @ConfComments("\n#Role that will be allowed to use moderator commands. MANAGE_SERVER permission will auto give them perms.\n #This can contain IDs.")
    @Order(20)
    List<String> rolesAllowedToUseModeratorCommands();

    @ConfKey("muted_role")
    @DefaultLong(0)
    @ConfComments("\n#Role to give to muted members")
    @Order(20)
    Long MutedRole();


}
