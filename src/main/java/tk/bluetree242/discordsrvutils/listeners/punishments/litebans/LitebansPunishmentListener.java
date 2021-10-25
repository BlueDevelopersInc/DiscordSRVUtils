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

package tk.bluetree242.discordsrvutils.listeners.punishments.litebans;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import litebans.api.*;
import org.bukkit.Bukkit;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.messages.MessageManager;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;


public class LitebansPunishmentListener extends Events.Listener{
    private DiscordSRVUtils core = DiscordSRVUtils.get();
    @Override
    public void entryAdded(Entry e) {
        core.executeAsync(() -> {
            if (!core.isReady()) return;
            LitebansPunishment punishment = new LitebansPunishment(e);

            Message msg = null;
            switch (e.getType().toUpperCase()) {
                case "BAN":
                    msg = MessageManager.get().getMessage(core.getBansConfig().bannedMessage(), PlaceholdObjectList.ofArray(new PlaceholdObject(punishment, "punishment")), null).build();
                    if (!e.isPermanent()) {
                        msg =MessageManager.get().getMessage(core.getBansConfig().tempBannedMessage(), PlaceholdObjectList.ofArray(new PlaceholdObject(punishment, "punishment")), null).build();
                    }
                    if (e.isIpban()) {
                        msg =MessageManager.get().getMessage(core.getBansConfig().IPBannedMessage(), PlaceholdObjectList.ofArray(new PlaceholdObject(punishment, "punishment")), null).build();
                    }
                    if (e.isPermanent() && e.isIpban()) {
                        msg =MessageManager.get().getMessage(core.getBansConfig().TempIPBannedMessage(), PlaceholdObjectList.ofArray(new PlaceholdObject(punishment, "punishment")), null).build();
                    }
                    break;
                case "MUTE" :
                    msg =MessageManager.get().getMessage(core.getBansConfig().MutedMessage(), PlaceholdObjectList.ofArray(new PlaceholdObject(punishment, "punishment")), null).build();
                    if (!e.isPermanent()) {
                        msg =MessageManager.get().getMessage(core.getBansConfig().tempBannedMessage(), PlaceholdObjectList.ofArray(new PlaceholdObject(punishment, "punishment")), null).build();
                    }
                    break;
                default:
                    break;
            }
            if (!e.isSilent())
            if (msg != null) {
                if (core.getBansConfig().isSendPunishmentmsgesToDiscord()) {
                    TextChannel channel = core.getChannel(core.getBansConfig().channel_id());
                    if (channel == null) {
                        core.severe("No channel was found with id " + core.getBansConfig().channel_id() + " For Punishment message");
                        return;
                    } else
                        core.queueMsg(msg, channel).queue();
                }
            }
            syncPunishment(e, false);
        });
    }

    public void entryRemoved(Entry e) {
        if (!core.isReady()) return;
        core.executeAsync(() -> {
            if (!core.isEnabled()) return;
            LitebansPunishment punishment = new LitebansPunishment(e);

            Message msg = null;
            switch (e.getType().toUpperCase()) {
                case "BAN":
                    msg = MessageManager.get().getMessage(core.getBansConfig().unbannedMessage(), PlaceholdObjectList.ofArray(new PlaceholdObject(punishment, "punishment")), null).build();
                    if (e.isIpban()) {
                        msg =MessageManager.get().getMessage(core.getBansConfig().unipbannedMessage(), PlaceholdObjectList.ofArray(new PlaceholdObject(punishment, "punishment")), null).build();
                    }
                    break;
                case "MUTE" :
                    msg =MessageManager.get().getMessage(core.getBansConfig().unmuteMessage(), PlaceholdObjectList.ofArray(new PlaceholdObject(punishment, "punishment")), null).build();
                    break;
            }
            if (!e.isSilent())
                if (msg != null) {
                if (core.getBansConfig().isSendPunishmentmsgesToDiscord()) {
                    TextChannel channel = core.getChannel(core.getBansConfig().channel_id());
                    if (channel == null) {
                        core.severe("No channel was found with id " + core.getBansConfig().channel_id() + " For Punishment message");
                        return;
                    } else
                        core.queueMsg(msg, channel).queue();
                }
            }
            syncPunishment(e, false);
        });
    }

    private void syncPunishment(Entry punishment, boolean un) {
        String id = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(Bukkit.getOfflinePlayer(LitebansPunishment.toOfflinePlayer(punishment.getUuid()).getUniqueId()).getUniqueId());
        if (id == null) return;
        User discordUser = core.getJDA().retrieveUserById(id).complete();
        if (!un) {
            if (!core.getBansConfig().isSyncPunishmentsWithDiscord()) return;
            switch (punishment.getType()) {
                case "BAN":
                    core.getGuild().ban(discordUser, 0, "Minecraft Synced Ban").queue();
                    break;
                case "MUTE":
                    Role role = core.getJDA().getRoleById(core.getBansConfig().mutedRole());
                    if (role == null) {
                        if (core.getBansConfig().mutedRole() != 0) core.severe("No Role was found with id " + core.getBansConfig().mutedRole() + ". Could not mute " + Bukkit.getOfflinePlayer(LitebansPunishment.toOfflinePlayer(punishment.getUuid()).getName()));
                        return;
                    }
                    core.getGuild().addRoleToMember(discordUser.getIdLong(), role).reason("Mute Synced with Minecraft").queue();
                    break;
                default:
                    break;
            }
        } else {
            if (!core.getBansConfig().isSyncUnpunishmentsWithDiscord()) return;
            switch (punishment.getType()) {
                case "BAN":
                    core.getGuild().unban(discordUser).reason("Minecraft Synced unban").queue();
                    break;
                case "MUTE":
                    Role role = core.getJDA().getRoleById(core.getBansConfig().mutedRole());
                    if (role == null) {
                        if (core.getBansConfig().mutedRole() != 0) core.severe("No Role was found with id " + core.getBansConfig().mutedRole() + ". Could not unmute " + Bukkit.getOfflinePlayer(LitebansPunishment.toOfflinePlayer(punishment.getUuid()).getName()));
                        return;
                    }
                    core.getGuild().removeRoleFromMember(discordUser.getIdLong(), role).reason("Unmute Synced with Minecraft").queue();
                default:break;
            }
        }
    }



}
