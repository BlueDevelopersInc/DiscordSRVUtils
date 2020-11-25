package com.bluetree.discordsrvutils.events;

import com.bluetree.discordsrvutils.DiscordSRVUtils;
import com.bluetree.discordsrvutils.utils.PlayerUtil;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.PermissionOverride;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberJoinEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.react.MessageReactionAddEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.manager.UUIDManager;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.springframework.core.ParameterizedTypeReference;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class JDAEvents extends ListenerAdapter {

    private DiscordSRVUtils core;
    public JDAEvents(DiscordSRVUtils core) {
        this.core = core;
    }


    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        Bukkit.getScheduler().runTask(core, () -> {
            UUID puuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(e.getUser().getId());
            String pname = Bukkit.getOfflinePlayer(puuid).getName();
            if (puuid != null) {
                if (PunishmentManager.get().isBanned(UUIDManager.get().getUUID(pname))) {
                    if (core.getConfig().getBoolean("advancedban_punishments_to_discord")) {
                        e.getGuild().ban(e.getMember().getUser(), 0, "DiscordSRVUtils banned by Advancedban").queue();
                        return;
                    }
                } else if (PunishmentManager.get().isMuted(UUIDManager.get().getUUID(pname))) {
                    if (core.getConfig().getBoolean("advancedban_punishments_to_discord")) {
                        if (e.getGuild().getRoleById(core.getConfig().getString("muted_role")) != null) {
                            e.getGuild().addRoleToMember(e.getMember(), e.getGuild().getRoleById(core.getConfig().getLong("muted_role"))).queue();
                        } else {
                            PlayerUtil.sendToAuthorizedPlayers("&cError: &eCould not give role to muted member because role is not found.");
                        }
                    }
                }

            }
            if (core.getConfig().getLong("welcomer_channel") == 000000000000000000) {
                core.getLogger().info(e.getMember().getUser().getName() + " Joined server" + " " + '"' + e.getGuild().getName() + '"' + ", Could not send message because the welcomer_channel wasn't set in the config");
                PlayerUtil.sendToAuthorizedPlayers("&cError: &e" + e.getMember().getUser().getName() + " Joined server" + '"' + e.getGuild().getName() + '"' + ", Could not send message because the welcomer_message wasn't set in the config");
            } else {
                if (e.getGuild().getTextChannelById(core.getConfig().getLong("welcomer_channel")) == null) {
                    core.getLogger().warning("welcomer_channel channel was not found on the guild. Please make sure you entered the right channel id.");
                    PlayerUtil.sendToAuthorizedPlayers("&cError: &ewelcomer_channel channel was not found on the guild. Please make sure that you entered the right channel id.");
                } else {
                    EmbedBuilder embed = new EmbedBuilder().setDescription(String.join("\n",
                            core.getConfig().getStringList("welcomer_message"))
                            .replace("[User_Name]", e.getMember().getUser().getName())
                            .replace("[User_Mention]", e.getMember().getAsMention())
                            .replace("[User_tag]", e.getMember().getUser().getAsTag())
                    );
                    if (core.getConfig().getStringList("welcomer_message") == null) {
                        core.getLogger().info("Could not send message to welcomer channel because welcomer_message is not set in the config.");
                        PlayerUtil.sendToAuthorizedPlayers("&cError: &eCould not send message to welcomer channel because welcomer_message is not set.");

                    }

                    String config = core.getConfig().getString("welcomer_message_embed_color");

                    if (config != null) {
                        switch (config.toUpperCase()) {
                            case "AQUA":
                                embed.setColor(1752220);
                                break;
                            case "GREEN":
                                embed.setColor(3066993);
                                break;
                            case "BLUE":
                                embed.setColor(3447003);
                                break;
                            case "PURPLE":
                                embed.setColor(10181046);
                                break;
                            case "GOLD":
                                embed.setColor(15844367);
                                break;
                            case "ORANGE":
                                embed.setColor(15105570);
                                break;
                            case "RED":
                                embed.setColor(15158332);
                                break;
                            case "GREY":
                                embed.setColor(9807270);
                                break;
                            case "DARKER_GREY":
                                embed.setColor(8359053);
                                break;
                            case "NAVY":
                                embed.setColor(3426654);
                                break;
                            case "DARK_AQUA":
                                embed.setColor(1146986);
                                break;
                            case "DARK_GREEN":
                                embed.setColor(2067276);
                                break;
                            case "DARK_BLUE":
                                embed.setColor(2123412);
                                break;
                            case "DARk_PURPLE":
                                embed.setColor(7419530);
                                break;
                            case "DARK_GOLD":
                                embed.setColor(12745742);
                                break;
                            case "DARK_ORANGE":
                                embed.setColor(11027200);
                                break;
                            case "DARK_RED":
                                embed.setColor(10038562);
                                break;
                            case "DARK_GREY":
                                embed.setColor(9936031);
                                break;
                            case "LIGHT_GREY":
                                embed.setColor(12370112);
                                break;
                            case "DARK_NAVY":
                                embed.setColor(2899536);
                                break;
                            case "LUMINOUS_VIVID_PINK":
                                embed.setColor(16580705);
                                break;
                            case "DARK_VIVID_PINK":
                                embed.setColor(12320855);
                                break;
                            default:
                                PlayerUtil.sendToAuthorizedPlayers("&cError: &eInvalid color in welcomer_message_embed_color");
                        }
                    }

                    try {
                        e.getGuild().getTextChannelById(core.getConfig().getLong("welcomer_channel")).sendMessage(embed.build()).queue();
                    } catch (NullPointerException ignored) {
                        core.getLogger().warning("Channel ID in config option \"welcomer_channel\" led to an unknown channel.");
                    }
                }

            }
            if (core.getConfig().getBoolean("join_message_to_online_players")) {
                String message = core.getConfig().getString("mc_welcomer_message");
                if (message != null) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message)
                            .replace("[User_tag]", e.getMember().getUser().getAsTag())
                            .replace("[User_Name]", e.getMember().getUser().getName())
                            .replace("[Guild_Name]", e.getGuild().getName()));
                } else {
                    core.getLogger().warning("Could not send welcomer message to online players, mc_welcomer_message is not set in the config.");
                    PlayerUtil.sendToAuthorizedPlayers("&cError: &eCould not send welcomer message to online players, mc_welcomer_message is not set in the config.");
                }
            }
        });

    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
            String[] args = e.getMessage().getContentRaw().split("\\s+");
            if (args[0].equalsIgnoreCase(core.getConfig().getString("BotPrefix") + "createticket")) {
                if (e.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                    try (Connection conn = core.getMemoryConnection()) {
                        try (PreparedStatement p1 = conn.prepareStatement("SELECT * FROM tickets_creating WHERE UserID=? AND Channel_id=?")) {
                            p1.setLong(1, e.getMember().getIdLong());
                            p1.setLong(2, e.getChannel().getIdLong());
                            p1.execute();
                            try (ResultSet r1 = p1.executeQuery()) {
                                if (!r1.next()) {
                                    try (PreparedStatement p2 = conn.prepareStatement("INSERT INTO tickets_creating (UserID, Channel_ID, step) VALUES (?,?, 0);")) {
                                        p2.setLong(1, e.getMember().getIdLong());
                                        p2.setLong(2, e.getChannel().getIdLong());
                                        p2.execute();
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setColor(Color.RED);
                                        embed.setTitle("Create new ticket");
                                        embed.setDescription("**Step 1:** What is the name of the ticket?\n\n To cancel this process, reply with `cancel`");
                                        e.getChannel().sendMessage(embed.build()).queue();
                                        return;
                                    }
                                }
                            }
                        }
                    } catch (SQLException ex) {
                        e.getChannel().sendMessage("Could not save data. Please try again later or restart your server.").queue();
                    }

                }
            }
            try (Connection conn = core.getMemoryConnection()) {
                try (PreparedStatement p1 = conn.prepareStatement("SELECT * FROM tickets_creating WHERE UserID=? AND Channel_id=?")) {
                    p1.setLong(1, e.getMember().getIdLong());
                    p1.setLong(2, e.getChannel().getIdLong());
                    p1.execute();
                    try (ResultSet r1 = p1.executeQuery()) {
                        if (r1.next()) {
                            if (e.getMessage().getContentRaw().equalsIgnoreCase("cancel")) {
                                e.getChannel().sendMessage("Cancelled").queue();
                                PreparedStatement last = conn.prepareStatement("DELETE FROM tickets_creating WHERE Channel_id=? AND UserID=?");
                                last.setLong(1, e.getChannel().getIdLong());
                                last.setLong(2, e.getMember().getIdLong());
                                last.execute(); return;

                            }
                            if (r1.getInt("step") == 0) {
                                try (PreparedStatement p2 = conn.prepareStatement("UPDATE tickets_creating SET step=1, Name=? WHERE UserID=? AND Channel_id=?")) {
                                    p2.setString(1, e.getMessage().getContentRaw());
                                    p2.setLong(2, e.getMember().getIdLong());
                                    p2.setLong(3, e.getChannel().getIdLong());
                                    p2.execute();
                                    EmbedBuilder embed = new EmbedBuilder();
                                    embed.setTitle("Create new ticket");
                                    embed.setDescription("**Step 2:** Please send the ID of the Category that opened tickets are created on\n\n To cancel this process, reply with `cancel`");
                                    embed.setColor(Color.RED);
                                    e.getChannel().sendMessage(embed.build()).queue();
                                }
                            } else if (r1.getInt("step") == 1) {
                                try {
                                    Long.parseLong(e.getMessage().getContentRaw());
                                    if (e.getJDA().getCategoryById(e.getMessage().getContentRaw()) == null) {
                                        e.getChannel().sendMessage("Category not found on any server. Please try again.").queue();
                                    } else {
                                        PreparedStatement p2 = conn.prepareStatement("UPDATE tickets_creating SET Opened_Category=?, STEP=2 WHERE Channel_Id=? AND UserID=?");
                                        p2.setLong(1, Long.parseLong(e.getMessage().getContentRaw()));
                                        p2.setLong(2, e.getChannel().getIdLong());
                                        p2.setLong(3, e.getMember().getIdLong());
                                        p2.execute();
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setTitle("Create new ticket");
                                        embed.setDescription("**Step 3:** Please send the ID of the category that Closed tickets should be moved to.\n\n To cancel this process, reply with `cancel`");
                                        embed.setColor(Color.RED);
                                        e.getChannel().sendMessage(embed.build()).queue();
                                    }
                                } catch (NumberFormatException ex) {
                                    e.getChannel().sendMessage("Not even a valid number. Please try again.").queue();
                                } catch (IllegalStateException ex) {
                                    e.getChannel().sendMessage("sadly").queue();
                                }

                            } else if (r1.getInt("step") == 2) {
                                try {
                                    Long.parseLong(e.getMessage().getContentRaw());
                                    if (e.getJDA().getCategoryById(e.getMessage().getContentRaw()) == null) {
                                        e.getChannel().sendMessage("Category not found on any server. Please try again.").queue();
                                    } else {
                                        PreparedStatement p2 = conn.prepareStatement("UPDATE tickets_creating SET Closed_Category=?, STEP=3 WHERE Channel_Id=? AND UserID=?");
                                        p2.setLong(1, Long.parseLong(e.getMessage().getContentRaw()));
                                        p2.setLong(2, e.getChannel().getIdLong());
                                        p2.setLong(3, e.getMember().getIdLong());
                                        p2.execute();
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setTitle("Create new ticket");
                                        embed.setDescription("**Step 4:** Please mention the roles that will be allowed to view all tickets.\n\n To cancel this process, reply with `cancel`");
                                        embed.setColor(Color.RED);
                                        e.getChannel().sendMessage(embed.build()).queue(message -> {
                                        });
                                    }
                                } catch (NumberFormatException ex) {
                                    e.getChannel().sendMessage("Not even a valid number. Please try again.").queue();
                                } catch (IllegalStateException ex) {
                                    e.getChannel().sendMessage("sadly").queue();
                                }


                            } else if (r1.getInt("step") == 3) {
                                if (!e.getMessage().getMentionedRoles().isEmpty()) {
                                    for (Role role : e.getMessage().getMentionedRoles()) {
                                        try (PreparedStatement p2 = conn.prepareStatement("SELECT * FROM ticket_allowed_roles WHERE Channel_id=? AND UserID=?")) {
                                            p2.setLong(1, e.getChannel().getIdLong());
                                            p2.setLong(2, e.getMember().getIdLong());
                                            p2.execute();
                                            try (ResultSet r2 = p2.executeQuery()) {
                                                    try (PreparedStatement p3 = conn.prepareStatement("INSERT INTO ticket_allowed_roles (Channel_id, UserID, RoleID) VALUES (?,?,?)")) {
                                                        p3.setLong(1, e.getChannel().getIdLong());
                                                        p3.setLong(2, e.getMember().getIdLong());
                                                        p3.setLong(3, role.getIdLong());
                                                        p3.execute();

                                                }
                                            }
                                        }
                                    }
                                    PreparedStatement p2 = conn.prepareStatement("UPDATE tickets_creating SET step=4 WHERE channel_id=? AND UserID=?");
                                    p2.setLong(1, e.getChannel().getIdLong());
                                    p2.setLong(2, e.getMember().getIdLong());
                                    p2.execute();
                                    EmbedBuilder embed = new EmbedBuilder();
                                    embed.setColor(Color.RED);
                                    embed.setTitle("Create new ticket");
                                    embed.setDescription("**Step 5:** Please mention the channel which we should send the ticket creation message.\n\n To cancel this process, reply with `cancel`");
                                    e.getChannel().sendMessage(embed.build()).queue();
                                } else {
                                    e.getChannel().sendMessage("No roles mentioned. Please try again").queue();
                                }
                            } else if (r1.getInt("step") == 4) {
                                if (e.getMessage().getMentionedChannels().isEmpty()) {
                                    e.getChannel().sendMessage("No Channels mentioned. Please try again").queue();
                                    return;
                                } else {
                                    int number = 0;
                                    for (TextChannel tx : e.getMessage().getMentionedChannels()) {
                                        number = number+1;
                                    }
                                    if (number >= 2) {
                                        e.getChannel().sendMessage("You mentioned more than 1 channel. Please try again").queue();
                                    } else {
                                        PreparedStatement p2 = conn.prepareStatement("SELECT * FROM tickets_creating WHERE UserID=? AND Channel_id=?");
                                        p2.setLong(1, e.getMember().getIdLong());
                                        p2.setLong(2, e.getChannel().getIdLong());
                                        p2.execute();
                                        ResultSet r2 = p2.executeQuery();
                                        r2.next();
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setTitle(r2.getString("Name"));
                                        embed.setDescription("React with \uD83D\uDCE9 to create a ticket.");
                                        embed.setColor(Color.CYAN);
                                        e.getGuild().getTextChannelById(e.getMessage().getMentionedChannels().get(0).getIdLong()).sendMessage(embed.build()).queue(message -> {

                                            message.addReaction("\uD83D\uDCE9").queue();
                                            try (Connection mconn = core.getMemoryConnection()) {
                                                Connection fconn = core.getDatabaseFile();
                                                PreparedStatement mp1 = mconn.prepareStatement("SELECT * FROM tickets_creating WHERE UserID=? AND Channel_id=?");
                                                mp1.setLong(1, e.getMember().getIdLong());
                                                mp1.setLong(2, e.getChannel().getIdLong());
                                                mp1.execute();
                                                ResultSet mr1 = mp1.executeQuery();
                                                mr1.next();
                                                PreparedStatement fp1 = fconn.prepareStatement("INSERT INTO discordsrvutils_tickets (TicketID, MessageId, Opened_Category, Closed_Category, Name) VALUES (?, ?, ?, ?, ?)");
                                                Random random = ThreadLocalRandom.current();
                                                int TicketID = random.nextInt();
                                                fp1.setLong(1, TicketID);
                                                fp1.setLong(2, message.getIdLong());
                                                fp1.setLong(3, mr1.getLong("Opened_Category"));
                                                fp1.setLong(4, mr1.getLong("Closed_Category"));
                                                fp1.setString(5, mr1.getString("Name"));
                                                fp1.execute();
                                                PreparedStatement mp2 = mconn.prepareStatement("SELECT * FROM ticket_allowed_roles WHERE Channel_id=? AND UserID=?");
                                                mp2.setLong(1, e.getChannel().getIdLong());
                                                mp2.setLong(2, e.getMember().getIdLong());
                                                mp2.execute();
                                                ResultSet mr2 = mp2.executeQuery();
                                                while (mr2.next()) {
                                                    PreparedStatement fm2 = fconn.prepareStatement("INSERT INTO ticket_allowed_roles (TicketID, RoleID) VALUES (?, ?)");
                                                    fm2.setInt(1, TicketID);
                                                    fm2.setLong(2, mr2.getLong("RoleID"));
                                                    fm2.execute();
                                                }
                                                PreparedStatement last = mconn.prepareStatement("DELETE FROM tickets_creating WHERE Channel_id=? AND UserID=?");
                                                last.setLong(1, e.getChannel().getIdLong());
                                                last.setLong(2, e.getMember().getIdLong());
                                                last.execute();

                                            }catch (SQLException exception) {
                                                exception.printStackTrace();
                                            }
                                        });
                                        e.getChannel().sendMessage("Ticket sent in " + e.getMessage().getMentionedChannels().get(0).getAsMention()).queue();
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (SQLException exception) {
                exception.printStackTrace();

            }


    }
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        if (e.getMember().getUser().isBot()) return;
        try (Connection conn = core.getDatabaseFile()) {
            try (PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_tickets WHERE MessageId=?")) {
                p1.setLong(1, e.getMessageIdLong());
                p1.execute();
                try (ResultSet r1 = p1.executeQuery()) {
                    if (r1.next()) {
                        e.getReaction().removeReaction(e.getMember().getUser()).queue();
                        e.getGuild().getCategoryById(r1.getLong("Opened_Category")).createTextChannel("opened-" + e.getMember().getEffectiveName()).queue(channel -> {
                            channel.getManager().setTopic("Ticket created by " + e.getMember().getUser().getName()).queue();
                            channel.createPermissionOverride(e.getMember()).grant(Permission.VIEW_CHANNEL).queue();
                            try {
                                Connection conn2 = core.getDatabaseFile();
                                PreparedStatement p2 = conn2.prepareStatement("SELECT * FROM ticket_allowed_roles WHERE TicketID=?");
                                PreparedStatement p3 = conn2.prepareStatement("SELECT * FROM discordsrvutils_tickets WHERE MessageId=?");
                                    p3.setLong(1, e.getMessageIdLong());
                                    p3.execute();
                                    ResultSet r2 = p3.executeQuery();
                                    r2.next();

                                    p2.setInt(1, r2.getInt("TicketID"));
                                p2.execute();
                                ResultSet r3 = p2.executeQuery();
                                while (r3.next()) {
                                    channel.createPermissionOverride(e.getGuild().getPublicRole()).setDeny(Permission.VIEW_CHANNEL).queue();
                                    channel.createPermissionOverride(e.getGuild().getRoleById(r3.getLong("RoleID"))).grant(Permission.VIEW_CHANNEL).queue();
                                }
                            } catch (SQLException exception) {
                                exception.printStackTrace();
                            }
                            channel.sendMessage(e.getMember().getAsMention() + " here is your ticket channel").queue(message2 -> {
                                message2.addReaction("\uD83D\uDD12").queue();                                try {
                                    Connection fconn2 = core.getDatabaseFile();
                                    PreparedStatement fp1 = fconn2.prepareStatement("INSERT INTO Opened_Tickets (UserID, MessageID, TicketID, Channel_id) VALUES (?, ?, ?, ?)");
                                    PreparedStatement fp2 = fconn2.prepareStatement("SELECT * FROM discordsrvutils_tickets WHERE MessageId=?");
                                    fp2.setLong(1, e.getMessageIdLong());
                                    fp2.execute();
                                    ResultSet fr1 = fp2.executeQuery();
                                    fr1.next();


                                        fp1.setLong(1, e.getMember().getIdLong());
                                    fp1.setLong(2, message2.getIdLong());
                                    fp1.setInt(3, fr1.getInt("TicketID"));
                                    fp1.setLong(4, channel.getIdLong());
                                    fp1.execute();
                                } catch (SQLException exception) {
                                    exception.printStackTrace();
                                }

                            });
                        });
                    } else {
                        Connection conn2 = core.getDatabaseFile();
                        PreparedStatement p2 = conn2.prepareStatement("SELECT * FROM Opened_Tickets WHERE MessageID=?");
                        p2.setLong(1, e.getMessageIdLong());
                        p2.execute();
                        ResultSet r2 = p2.executeQuery();
                        if (r2.next()) {

                            PreparedStatement closed = conn2.prepareStatement("SELECT * FROM Opened_Tickets WHERE MessageID=?");
                            closed.setLong(1, e.getMessageIdLong());
                            closed.execute(); ResultSet closed2 = closed.executeQuery(); closed2.next();
                            PreparedStatement tickets = conn2.prepareStatement("SELECT * FROM discordsrvutils_tickets WHERE TicketID=?");
                            tickets.setLong(1, closed2.getInt("TicketID"));
                            tickets.execute();
                            ResultSet ticketss = tickets.executeQuery(); ticketss.next();
                            if (e.getReactionEmote().getName().equals("\uD83D\uDD12")) {
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setTitle("Ticket Closed");
                                embed.setDescription("Ticket Closed by " + e.getMember().getAsMention() + "");
                                e.getChannel().sendMessage(embed.build()).queue();
                                e.getTextChannel().getPermissionOverride(e.getGuild().getMemberById(r2.getLong("UserID"))).getManager().setDeny(Permission.MESSAGE_WRITE).queue();
                                e.getReaction().removeReaction(e.getUser()).queue();
                                e.getTextChannel().getManager().setParent(e.getGuild().getCategoryById(ticketss.getLong("Closed_Category"))).queue();
                                e.getTextChannel().getManager().setName(e.getTextChannel().getName().replace("opened", "closed")).queue();
                                PreparedStatement prpstmt = conn2.prepareStatement("SELECT * FROM Opened_Tickets WHERE MessageID=?");
                                prpstmt.setLong(1, e.getMessageIdLong());
                                prpstmt.execute();
                                ResultSet rr = prpstmt.executeQuery();
                                rr.next();
                                PreparedStatement ppp = conn2.prepareStatement("INSERT INTO Closed_Tickets (UserID, MessageID, TicketID, Channel_id) VALUES (?, ?, ?, ?)");
                                ppp.setLong(1, rr.getLong("UserID"));
                                ppp.setLong(2, rr.getLong("MessageID"));
                                ppp.setLong(3, rr.getInt("TicketID"));
                                ppp.setLong(4, rr.getLong("Channel_id"));
                                ppp.execute();
                                PreparedStatement prp = conn2.prepareStatement("DELETE FROM Opened_Tickets WHERE MessageID=?");
                                prp.setLong(1, e.getMessageIdLong());
                                prp.execute();
                            }
                        }

                    }
                }
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        }

    }





}
