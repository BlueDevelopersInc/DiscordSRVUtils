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

package dev.bluetree242.discordsrvutils.config;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.List;

@ConfHeader("# https://wiki.discordsrvutils.xyz/leveling/\n\n")
public interface LevelingConfig {

    @AnnotationBasedSorter.Order(9)
    @ConfComments("# Is leveling enabled?")
    @ConfDefault.DefaultBoolean(false)
    boolean enabled();

    @ConfKey("minecraft-levelup-message")
    @ConfComments("# Message when a minecraft player levelup.")
    @AnnotationBasedSorter.Order(10)
    @ConfDefault.DefaultStrings({
            "&e-----------------------------------------------------&r",
            "          &cCongratulations! &eYou leveled up to level [stats.level]!",
            "&e-----------------------------------------------------&r"
    })
    List<String> minecraft_levelup_message();


    @ConfKey("antispam-messages")
    @AnnotationBasedSorter.Order(20)
    @ConfDefault.DefaultBoolean(true)
    boolean antispam_messages();

    @ConfKey("require-link")
    @AnnotationBasedSorter.Order(25)
    @ConfDefault.DefaultBoolean(false)
    @ConfComments("# Require minecraft player to be linked, to gain xp?")
    boolean require_link();

    @ConfKey("discord-message")
    @ConfComments("# Message when a Discord user levelup.")
    @AnnotationBasedSorter.Order(30)
    @ConfDefault.DefaultString("Congratulations [user.asMention]! You leveled up to level [stats.level]")
    String discord_message();

    @ConfKey("discord-channel")
    @ConfComments("# ID of channel for discord leveling messages, 0 for discordsrv default and -1 for the channel where user leveled up.")
    @AnnotationBasedSorter.Order(30)
    @ConfDefault.DefaultLong(-1)
    long discord_channel();


    @ConfKey("level-command-message")
    @ConfComments("# Message when user uses the level (aka rank) command.")
    @AnnotationBasedSorter.Order(40)
    @ConfDefault.DefaultString("message:level")
    String level_command_message();

    @ConfKey("level-command-invalid-player")
    @ConfComments("# Message when player not found when you run /rank <player_name> command.")
    @AnnotationBasedSorter.Order(50)
    @ConfDefault.DefaultString("Player never joined before")
    String level_command_invalid_player();

    @ConfKey("level-command-not-linked")
    @ConfComments("# Message when user not linked when using /rank.")
    @AnnotationBasedSorter.Order(60)
    @ConfDefault.DefaultString("Your account is not linked with any Minecraft Account. Use `/discordsrv link` in game to link your account")
    String level_command_not_linked();

    @ConfKey("level-command-other-not-linked")
    @ConfComments("# Message when user not linked when using /rank <mention another user>.")
    @AnnotationBasedSorter.Order(70)
    @ConfDefault.DefaultString("[user.name]'s discord account is not linked with minecraft account")
    String level_command_other_not_linked();

}
