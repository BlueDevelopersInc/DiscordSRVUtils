/*
 * LICENSE
 * DiscordSRVUtils
 * -------------
 * Copyright (C) 2020 - 2022 BlueTree242
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

package tk.bluetree242.discordsrvutils.config;


import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.List;

@ConfHeader("# General config for the plugin, wiki for some stuff here https://wiki.discordsrvutils.xyz/\n# You should read this topic https://wiki.discordsrvutils.xyz/messages/\n")
public interface Config {


    @AnnotationBasedSorter.Order(10)
    @ConfComments("#Admins who can use the bot without limits. This can be either user or role IDs.")
    @ConfDefault.DefaultLongs({})
    List<Long> admins();


    @AnnotationBasedSorter.Order(30)
    @ConfComments("#OnlineStatus for the bot. can be ONLINE, DND, or IDLE")
    @ConfDefault.DefaultString("ONLINE")
    String onlinestatus();

    @AnnotationBasedSorter.Order(34)
    @ConfKey("disabled-commands")
    @ConfComments("# Commands that must be disabled, will also hide from help command, Don't use aliases here")
    @ConfDefault.DefaultStrings({})
    List<String> disabled_commands();

    @AnnotationBasedSorter.Order(35)
    @ConfKey("register-slash-commands")
    @ConfComments("# Should We register slash commands into your discord server?")
    @ConfDefault.DefaultBoolean(true)
    boolean register_slash();

    @AnnotationBasedSorter.Order(40)
    @ConfKey("welcomer.enabled")
    @ConfComments("#Should we do Welcomer?")
    @ConfDefault.DefaultBoolean(false)
    boolean welcomer_enabled();

    @AnnotationBasedSorter.Order(50)
    @ConfKey("welcomer.channel")
    @ConfComments("#Channel to send welcomer message, Use ID, No need to change this if dms are enabled")
    @ConfDefault.DefaultLong(0)
    long welcomer_channel();

    @AnnotationBasedSorter.Order(60)
    @ConfKey("welcomer.dm_user")
    @ConfComments("#Should we DM the User?")
    @ConfDefault.DefaultBoolean(false)
    boolean welcomer_dm_user();

    @AnnotationBasedSorter.Order(70)
    @ConfKey("welcomer.message")
    @ConfComments("#Welcomer message")
    @ConfDefault.DefaultString("message:welcome")
    String welcomer_message();

    @AnnotationBasedSorter.Order(71)
    @ConfKey("welcomer.role")
    @ConfComments("#Role to add when they join the server")
    @ConfDefault.DefaultLong(0)
    Long welcomer_role();

    @AnnotationBasedSorter.Order(80)
    @ConfKey("goodbye.enabled")
    @ConfComments("#Should we send goodbye messages?")
    @ConfDefault.DefaultBoolean(false)
    boolean goodbye_enabled();

    @AnnotationBasedSorter.Order(90)
    @ConfKey("goodbye.channel")
    @ConfDefault.DefaultLong(0)
    @ConfComments("#Channel to send message in, DM unavailable because when they leave there is a big chance of no mutual servers")
    long goodbye_channel();

    @AnnotationBasedSorter.Order(100)
    @ConfKey("goodbye.message")
    @ConfComments("#Message to send")
    @ConfDefault.DefaultString("GoodBye **[user.asTag]**. Hope you come back later")
    String goodbye_message();

    @AnnotationBasedSorter.Order(101)
    @ConfKey("invite-tracking")
    @ConfComments("#Should we track invites? This enables a whole invite tracking system. https://wiki.discordsrvutils.xyz/invite-tracking")
    @ConfDefault.DefaultBoolean(true)
    boolean track_invites();


    @AnnotationBasedSorter.Order(110)
    @ConfKey("afk.enabled")
    @ConfComments("#Should we send afk (and no longer afk) messages?")
    @ConfDefault.DefaultBoolean(true)
    boolean afk_message_enabled();

    @AnnotationBasedSorter.Order(120)
    @ConfKey("afk.channel")
    @ConfComments("#AFK Channel. Leave 0 for default channel for discordsrv")
    @ConfDefault.DefaultLong(0)
    long afk_channel();

    @AnnotationBasedSorter.Order(130)
    @ConfKey("afk.message")
    @ConfComments("#AFK Message")
    @ConfDefault.DefaultString("message:afk")
    String afk_message();

    @AnnotationBasedSorter.Order(140)
    @ConfKey("afk.no-longer-afk-message")
    @ConfDefault.DefaultString("message:no-longer-afk")
    String no_longer_afk_message();

    @AnnotationBasedSorter.Order(141)
    @ConfKey("help-response")
    @ConfComments("# Response of /help command, leave blank to generate. You can use message:msgfile where msgfile is the name of the message file in your messages folder (without json, and you can make a new file)")
    @ConfDefault.DefaultString("")
    String help_response();

    @AnnotationBasedSorter.Order(150)
    @ConfKey("bungee-mode")
    @ConfComments("# Bungee Mode. This will make bot not respond to commands, and nothing will happen as if plugin not installed (only mc leveling is active). This option should be enabled on all servers except lobby if you use bungee")
    @ConfDefault.DefaultBoolean(false)
    Boolean bungee_mode();

    @AnnotationBasedSorter.Order(160)
    @ConfKey("minimize-errors")
    @ConfComments("# Replace errors with small error note. Please note that this is a bad practice. If your console is spammed with errors (by this plugin) Please report at https://discordsrvutils.xyz/support")
    @ConfDefault.DefaultBoolean(false)
    Boolean minimize_errors();

    @AnnotationBasedSorter.Order(170)
    @ConfKey("pool-size")
    @ConfComments("# Thread Pool Size. Simply, how many tasks the plugin can do as the same time, increase if needed, may use more CPU.")
    @ConfDefault.DefaultInteger(2)
    int pool_size();

    @AnnotationBasedSorter.Order(180)
    @ConfKey("dev-updatechecker")
    @ConfComments("# Should we tell you about updates if the updates that remain are dev builds?")
    @ConfDefault.DefaultBoolean(true)
    boolean dev_updatechecker();

}
