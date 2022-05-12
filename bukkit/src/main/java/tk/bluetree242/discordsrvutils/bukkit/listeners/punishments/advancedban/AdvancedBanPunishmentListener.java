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

package tk.bluetree242.discordsrvutils.bukkit.listeners.punishments.advancedban;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import lombok.RequiredArgsConstructor;
import me.leoko.advancedban.bukkit.event.PunishmentEvent;
import me.leoko.advancedban.bukkit.event.RevokePunishmentEvent;
import me.leoko.advancedban.utils.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;

@RequiredArgsConstructor
public class AdvancedBanPunishmentListener implements Listener {
    private final DiscordSRVUtils core;

    @EventHandler
    public void onPunish(PunishmentEvent e) {
        core.getAsyncManager().executeAsync(() -> {
            AdvancedBanPunishment punishment = new AdvancedBanPunishment(e.getPunishment(), false);
            syncPunishment(e.getPunishment(), false);
            tk.bluetree242.discordsrvutils.interfaces.Punishment.handlePunishment(punishment, core);
        });
    }

    @EventHandler
    public void onRevoke(RevokePunishmentEvent e) {
        core.getAsyncManager().executeAsync(() -> {
            AdvancedBanPunishment punishment = new AdvancedBanPunishment(e.getPunishment(), true);
            syncPunishment(e.getPunishment(), true);
            tk.bluetree242.discordsrvutils.interfaces.Punishment.handlePunishment(punishment, core);
        });
    }

    private void syncPunishment(Punishment punishment, boolean un) {
        String id = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(Bukkit.getOfflinePlayer(punishment.getName()).getUniqueId());
        if (id == null) return;
        User discordUser = core.getJDA().retrieveUserById(id).complete();

        if (!un) {
            Member discordMember = core.getPlatform().getDiscordSRV().getMainGuild().retrieveMember(discordUser).complete();
            if (discordMember == null) return;
            if (!core.getPlatform().getDiscordSRV().getMainGuild().getSelfMember().canInteract(discordMember)) return;
            if (!core.getBansConfig().isSyncPunishmentsWithDiscord()) return;
            switch (punishment.getType()) {
                case BAN:
                case TEMP_BAN:
                case IP_BAN:
                case TEMP_IP_BAN:
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
                case TEMP_MUTE:
                    Role role = core.getPlatform().getDiscordSRV().getMainGuild().getRoleById(core.getBansConfig().mutedRole());
                    if (role == null) {
                        if (core.getBansConfig().mutedRole() != 0)
                            core.severe("No Role was found with id " + core.getBansConfig().mutedRole() + ". Could not mute " + punishment.getName());
                        return;
                    }
                    core.getPlatform().getDiscordSRV().getMainGuild().addRoleToMember(discordUser.getIdLong(), role).reason("Mute Synced with Minecraft").queue();
                    break;
                default:
                    break;
            }
        } else {
            if (!core.getBansConfig().isSyncUnpunishmentsWithDiscord()) return;
            switch (punishment.getType()) {
                case BAN:
                case TEMP_BAN:
                case IP_BAN:
                case TEMP_IP_BAN:
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
                case TEMP_MUTE:
                    Role role = core.getPlatform().getDiscordSRV().getMainGuild().getRoleById(core.getBansConfig().mutedRole());
                    if (role == null) {
                        if (core.getBansConfig().mutedRole() != 0)
                            core.severe("No Role was found with id " + core.getBansConfig().mutedRole() + ". Could not unmute " + punishment.getName());
                        return;
                    }
                    core.getPlatform().getDiscordSRV().getMainGuild().removeRoleFromMember(discordUser.getIdLong(), role).reason("Unmute Synced with Minecraft").queue();
                default:
                    break;
            }
        }
    }

    public void remove() {
        PunishmentEvent.getHandlerList().unregister((Plugin) core.getPlatform().getOriginal());
        RevokePunishmentEvent.getHandlerList().unregister((Plugin) core.getPlatform().getOriginal());
    }
}
