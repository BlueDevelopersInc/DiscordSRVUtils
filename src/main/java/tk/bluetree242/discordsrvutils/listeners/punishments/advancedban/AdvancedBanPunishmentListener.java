package tk.bluetree242.discordsrvutils.listeners.punishments.advancedban;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import me.leoko.advancedban.bukkit.event.PunishmentEvent;
import me.leoko.advancedban.bukkit.event.RevokePunishmentEvent;
import me.leoko.advancedban.utils.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.messages.MessageManager;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;

import java.util.UUID;

public class AdvancedBanPunishmentListener implements Listener {
    private final DiscordSRVUtils core = DiscordSRVUtils.get();

    @EventHandler
    public void onPunish(PunishmentEvent e) {
        core.executeAsync(() -> {
                AdvancedBanPunishment punishment = new AdvancedBanPunishment(e.getPunishment());

                Message msg = null;
                switch (e.getPunishment().getType()) {
                    case BAN:
                        msg = MessageManager.get().getMessage(core.getBansConfig().bannedMessage(), PlaceholdObjectList.ofArray(new PlaceholdObject(punishment, "punishment")), null).build();
                        break;
                    case TEMP_BAN:
                        msg =MessageManager.get().getMessage(core.getBansConfig().tempBannedMessage(), PlaceholdObjectList.ofArray(new PlaceholdObject(punishment, "punishment")), null).build();
                        break;
                    case IP_BAN:
                        msg =MessageManager.get().getMessage(core.getBansConfig().IPBannedMessage(), PlaceholdObjectList.ofArray(new PlaceholdObject(punishment, "punishment")), null).build();
                        break;
                    case TEMP_IP_BAN:
                        msg =MessageManager.get().getMessage(core.getBansConfig().TempIPBannedMessage(), PlaceholdObjectList.ofArray(new PlaceholdObject(punishment, "punishment")), null).build();
                        break;
                    case MUTE :
                        msg =MessageManager.get().getMessage(core.getBansConfig().MutedMessage(), PlaceholdObjectList.ofArray(new PlaceholdObject(punishment, "punishment")), null).build();
                        break;
                    case TEMP_MUTE:
                        msg =MessageManager.get().getMessage(core.getBansConfig().TempMutedMessage(), PlaceholdObjectList.ofArray(new PlaceholdObject(punishment, "punishment")), null).build();
                        break;
                    default:
                        break;
                }
                if (msg != null) {
                    if (core.getBansConfig().isSendPunishmentmsgesToDiscord()) {
                        TextChannel channel = core.getChannel(core.getBansConfig().channel_id());
                        if (channel == null) {
                            core.severe("No channel was found with id " + core.getBansConfig().channel_id() + " For Punishment message");
                            return;
                        } else
                        channel.sendMessage(msg).queue();
                    }
                }
                syncPunishment(e.getPunishment(), false);
        });
    }
    @EventHandler public void onRevoke(RevokePunishmentEvent e) {
        core.executeAsync(() -> {
            AdvancedBanPunishment punishment = new AdvancedBanPunishment(e.getPunishment());

            Message msg = null;
            switch (e.getPunishment().getType()) {
                case BAN:
                case TEMP_BAN:
                    msg = MessageManager.get().getMessage(core.getBansConfig().unbannedMessage(), PlaceholdObjectList.ofArray(new PlaceholdObject(punishment, "punishment")), null).build();
                    break;
                case IP_BAN:
                case TEMP_IP_BAN:
                    msg =MessageManager.get().getMessage(core.getBansConfig().unipbannedMessage(), PlaceholdObjectList.ofArray(new PlaceholdObject(punishment, "punishment")), null).build();
                    break;
                case MUTE :
                case TEMP_MUTE:
                    msg =MessageManager.get().getMessage(core.getBansConfig().unmuteMessage(), PlaceholdObjectList.ofArray(new PlaceholdObject(punishment, "punishment")), null).build();
                    break;
                default:
                    break;
            }
            if (msg != null) {
                if (core.getBansConfig().isSyncUnpunishmentsmsgWithDiscord()) {
                    TextChannel channel = core.getChannel(core.getBansConfig().channel_id());
                    if (channel == null) {
                        core.severe("No channel was found with id " + core.getBansConfig().channel_id() + " For UnPunishment message");
                        return;
                    }
                    channel.sendMessage(msg).queue();
                }
            }
            syncPunishment(e.getPunishment(), true);
        });
    }

    private void syncPunishment(Punishment punishment, boolean un) {
        User discordUser = core.getJDA().getUserById(DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(Bukkit.getOfflinePlayer(punishment.getName()).getUniqueId()));
        if (discordUser == null) return;
        if (!un) {
            if (!core.getBansConfig().isSyncPunishmentsWithDiscord()) return;
                switch (punishment.getType()) {
                case BAN:
                case TEMP_BAN:
                case IP_BAN:
                case TEMP_IP_BAN:
                    core.getGuild().ban(discordUser, 0, "Minecraft Synced Ban").queue();
                    break;
                case MUTE:
                case TEMP_MUTE:
                    Role role = core.getJDA().getRoleById(core.getBansConfig().mutedRole());
                    if (role == null) {
                        if (core.getBansConfig().mutedRole() != 0) core.severe("No Role was found with id " + core.getBansConfig().mutedRole() + ". Could not mute " + punishment.getName());
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
                case BAN:
                case TEMP_BAN:
                case IP_BAN:
                case TEMP_IP_BAN:
                    core.getGuild().unban(discordUser).reason("Minecraft Synced unban").queue();
                    break;
                case MUTE:
                case TEMP_MUTE:
                    Role role = core.getJDA().getRoleById(core.getBansConfig().mutedRole());
                    if (role == null) {
                        if (core.getBansConfig().mutedRole() != 0) core.severe("No Role was found with id " + core.getBansConfig().mutedRole() + ". Could not unmute " + punishment.getName());
                        return;
                    }
                    core.getGuild().removeRoleFromMember(discordUser.getIdLong(), role).reason("Unmute Synced with Minecraft").queue();
                default:break;
            }
        }
    }
}
