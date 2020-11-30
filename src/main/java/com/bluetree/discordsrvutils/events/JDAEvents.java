package com.bluetree.discordsrvutils.events;

import com.bluetree.discordsrvutils.DiscordSRVUtils;
import com.bluetree.discordsrvutils.utils.PlayerUtil;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.events.channel.text.TextChannelDeleteEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberJoinEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.react.MessageReactionAddEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.manager.UUIDManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class JDAEvents extends ListenerAdapter {

    private DiscordSRVUtils core;
    public JDAEvents(DiscordSRVUtils core) {
        this.core = core;
    }

    private static final Random RANDOM = new Random();
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        Bukkit.getScheduler().runTask(core, () -> {
            if (Bukkit.getPluginManager().isPluginEnabled("AdvancedBan")) {
                UUID puuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(e.getUser().getId());
                if (puuid != null) {
                    String pname = Bukkit.getOfflinePlayer(puuid).getName();

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
        if (e.getAuthor().isFake()) return;
        if (e.getAuthor().isBot()) return;
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

                } else {
                    e.getChannel().sendMessage("No permission (Required: **MANAGE SERVER**)").queue();
                }
            } else if (args[0].equalsIgnoreCase(core    .getConfig().get("BotPrefix") + "ticketlookup")) {
                if (e.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                    if (!(args.length >= 2)) {
                        e.getChannel().sendMessage("**Usage:** " + core.getConfig().getString("BotPrefix") + "ticketlookup <ticket name>").queue();

                    } else {
                        try {
                            String argss = "";
                            for (int i = 1; i < args.length; i++) {
                                argss = argss + args[i] + " ";
                            }

                            Connection conn = core.getDatabaseFile();
                            PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_tickets WHERE Name=?");
                            p1.setString(1, argss.replaceAll("\\s+$", ""));
                            p1.execute();
                            ResultSet r1 = p1.executeQuery();
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setTitle("Tickets with name \"" + argss.replaceAll("\\s+$", "") + "\"");
                            embed.setColor(Color.CYAN);
                            int number = 0;
                            while (r1.next()) {
                                number = number + 1;
                                PreparedStatement p2 = conn.prepareStatement("SELECT * FROM discordsrvutils_ticket_allowed_roles WHERE TicketID=?");
                                p2.setInt(1, r1.getInt("TicketID"));
                                p2.execute();
                                ResultSet r2 = p2.executeQuery();
                                String roles = "";
                                while (r2.next()) {
                                    roles = "<@&" + r2.getLong("RoleID") + "> " + roles;
                                }
                                embed.addField("ID: " + r1.getInt("TicketID") + "", "**Name:** " + r1.getString("Name") + "\n**Opened category:** " + r1.getLong("Opened_Category") + "\n**Closed category:** " + r1.getLong("Closed_Category") + "\n**Ticket view allowed roles:** " + roles + "\n**Channel:** " + "<#" + r1.getLong("ChannelID") + ">", false);
                            }
                            embed.setFooter(number + " Results found.");
                            e.getChannel().sendMessage(embed.build()).queue();
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                    }
                }
                 else {
                    e.getChannel().sendMessage("No permission (Required: **MANAGE SERVER**)").queue();
                }
            } else if (args[0].equalsIgnoreCase(core.getConfig().getString("BotPrefix") + "close")) {
                try {
                    Connection conn = core.getDatabaseFile();
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_Opened_Tickets WHERE Channel_id=?");
                    p1.setLong(1, e.getChannel().getIdLong());
                    p1.execute();
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {

                        PreparedStatement closed = conn.prepareStatement("SELECT * FROM discordsrvutils_Opened_Tickets WHERE MessageID=?");
                        closed.setLong(1, e.getMessageIdLong());
                        closed.execute(); ResultSet closed2 = closed.executeQuery(); closed2.next();
                        PreparedStatement tickets = conn.prepareStatement("SELECT * FROM discordsrvutils_tickets WHERE TicketID=?");
                        tickets.setLong(1, r1.getInt("TicketID"));
                        tickets.execute();
                        ResultSet ticketss = tickets.executeQuery(); ticketss.next();
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setTitle("Ticket Closed");
                            embed.setColor(Color.YELLOW);
                            embed.setDescription("Ticket Closed by " + e.getMember().getAsMention() + "");
                            e.getChannel().sendMessage(embed.build()).queue(msg -> {
                                try {
                                    Connection conn3 = core.getDatabaseFile();
                                    PreparedStatement prpstmt = conn3.prepareStatement("SELECT * FROM discordsrvutils_Opened_Tickets WHERE Channel_id=?");
                                    prpstmt.setLong(1, e.getChannel().getIdLong());
                                    prpstmt.execute();
                                    ResultSet rr = prpstmt.executeQuery();
                                    rr.next();
                                    PreparedStatement ppp = conn3.prepareStatement("INSERT INTO discordsrvutils_Closed_Tickets (UserID, MessageID, TicketID, Channel_id, Closed_Message) VALUES (?, ?, ?, ?, ?)");
                                    ppp.setLong(1, rr.getLong("UserID"));
                                    ppp.setLong(2, rr.getLong("MessageID"));
                                    ppp.setLong(3, rr.getInt("TicketID"));
                                    ppp.setLong(4, rr.getLong("Channel_id"));
                                    ppp.setLong(5, msg.getIdLong());
                                    ppp.execute();
                                    PreparedStatement prp = conn3.prepareStatement("DELETE FROM discordsrvutils_Opened_Tickets WHERE Channel_id=?");
                                    prp.setLong(1, e.getChannel().getIdLong());
                                    prp.execute();
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                                msg.addReaction("\uD83D\uDDD1️").queue();
                            });
                            e.getChannel().getPermissionOverride(e.getGuild().getMemberById(r1.getLong("UserID"))).getManager().setDeny(Permission.VIEW_CHANNEL).queue();
                            PreparedStatement pp = conn.prepareStatement("SELECt * FROM discordsrvutils_tickets WHERE TicketID=?");
                            pp.setInt(1, r1.getInt("TicketID"));
                            pp.execute();
                            ResultSet rr = pp.executeQuery(); rr.next();
                            System.out.println(rr.getLong("Closed_Category"));
                            System.out.println(ticketss.getLong("Closed_Category"));
                            e.getChannel().getManager().setParent(e.getGuild().getCategoryById(ticketss.getLong("Closed_Category"))).queue();
                            e.getChannel().getManager().setName(e.getChannel().getName().replace("opened", "closed")).queue();



                    } else e.getChannel().sendMessage("You are not on an opened ticket").queue();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

            } else if (args[0].equalsIgnoreCase(core.getConfig().getString("BotPrefix") + "editticket")) {
                if (!(args.length >= 2)) {
                    e.getChannel().sendMessage("**Usage:** " + core.getConfig().getString("BotPrefix") + "editticket <Ticket ID>").queue();
                } else {
                    if (e.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                        try {
                            Connection conn = core.getMemoryConnection();
                            PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_Awaiting_Edits WHERE Channel_id=? AND UserID=?");
                            p1.setLong(1, e.getChannel().getIdLong());
                            p1.setLong(2, e.getMember().getIdLong());
                            p1.execute();
                            ResultSet r1 = p1.executeQuery();
                            Connection fconn = core.getDatabaseFile();
                            PreparedStatement p2 = fconn.prepareStatement("SELECT * FROM discordsrvutils_tickets WHERE TicketID=?");
                            p2.setInt(1, Integer.parseInt(args[1]));
                            p2.execute();
                            ResultSet r2 = p2.executeQuery();
                            if (!r1.next()) {
                                if (r2.next()) {
                                    EmbedBuilder embed = new EmbedBuilder();
                                    embed.setDescription("**:one: Name\n\n:two: Opened category\n\n:three: Closed category\n\n:four: Ticket view allowed roles\n\n:five: Message Channel**");
                                    e.getChannel().sendMessage(embed.build()).queue(message -> {
                                        message.addReaction("1️⃣").queue();
                                        message.addReaction("2️⃣").queue();
                                        message.addReaction("3️⃣").queue();
                                        message.addReaction("4️⃣").queue();
                                        message.addReaction("5️⃣").queue();
                                        try {
                                            Connection conn2 = core.getMemoryConnection();
                                            PreparedStatement p3 = conn2.prepareStatement("INSERT INTO discordsrvutils_Awaiting_Edits (Channel_id, UserID, MessageID, TicketID, Type) VALUES (?, ?, ?, ?, 0)");
                                            p3.setLong(1, e.getChannel().getIdLong());
                                            p3.setLong(2, e.getMember().getIdLong());
                                            p3.setLong(3, message.getIdLong());
                                            p3.setInt(4, Integer.parseInt(args[1]));
                                            p3.execute();
                                        } catch (SQLException ex) {
                                            ex.printStackTrace();
                                        }
                                    });
                                } else {
                                    EmbedBuilder embed = new EmbedBuilder();
                                    embed.setColor(Color.RED);
                                    embed.setTitle("Invalid ticket ID");
                                    embed.setDescription("\nHaving troubles getting Ticket ID? use `" + core.getConfig().getString("BotPrefix") + "ticketlookup <TicketName>`.");
                                    e.getChannel().sendMessage(embed.build()).queue();

                                }
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        } catch (NumberFormatException ex) {
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setColor(Color.RED);
                            embed.setTitle("Invalid ticket ID");
                            embed.setDescription("\nHaving troubles getting Ticket ID? use `" + core.getConfig().getString("BotPrefix") + "ticketlookup <TicketName>`.");
                            e.getChannel().sendMessage(embed.build()).queue();
                        }
                    } else {
                        e.getChannel().sendMessage("No permission (Required: **MANAGE SERVER**)").queue();
                    }
                }
            }
            else if (args[0].equalsIgnoreCase(core.getConfig().getString("BotPrefix") + "help")) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(e.getJDA().getSelfUser().getName() + " Commands");
                embed.setDescription("Use `" + core.getConfig().getString("BotPrefix") + "<Command>` to execute a command.");
                embed.addField("Tickets", "`createticket`, `ticketlookup`, `editticket`, `close`, `deleteticket`, `editticket`", false);
                embed.setColor(Color.GREEN);
                e.getChannel().sendMessage(embed.build()).queue();
            }
            else if (args[0].equalsIgnoreCase(core.getConfig().getString("BotPrefix") + "deleteticket")) {
                if (!(args.length >= 2)) {
                    e.getChannel().sendMessage("**Usage:** " + core.getConfig().getString("BotPrefix") + "deleteticketticket <Ticket ID>").queue();
                } else {
                    if (e.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                        try {
                            Connection conn = core.getDatabaseFile();
                            PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_tickets WHERE TicketID=?");
                            p1.setInt(1, Integer.parseInt(args[1]));
                            p1.execute();
                            ResultSet r1 = p1.executeQuery();
                            if (r1.next()) {
                                PreparedStatement p2 = conn.prepareStatement("DELETE FROM discordsrvutils_tickets WHERE TicketID=?");
                                p2.setInt(1, Integer.parseInt(args[1]));
                                p2.execute();
                                PreparedStatement p3 = conn.prepareStatement("DELETE FROM discordsrvutils_ticket_allowed_roles WHERE TicketID=?");
                                p3.setInt(1, Integer.parseInt(args[1]));
                                p3.execute();
                                PreparedStatement p4 = conn.prepareStatement("SELECT * FROM discordsrvutils_opened_tickets WHERE TicketID=?");
                                p4.setInt(1, Integer.parseInt(args[1]));
                                ResultSet r2 = p4.executeQuery();
                                while (r2.next()) {
                                    e.getJDA().getGuildChannelById(r2.getLong("Channel_id")).delete().queue();
                                }
                                e.getGuild().getTextChannelById(r1.getLong("ChannelID")).deleteMessageById(r1.getLong("MessageID")).queue();
                                e.getChannel().sendMessage("Deleted ticket `" + r1.getString("Name") + "`").queue();
                            } else {
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setColor(Color.RED);
                                embed.setTitle("Invalid ticket ID");
                                embed.setDescription("\nHaving troubles getting Ticket ID? use `" + core.getConfig().getString("BotPrefix") + "ticketlookup <TicketName>`.");
                                e.getChannel().sendMessage(embed.build()).queue();
                            }

                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        } catch (NumberFormatException ex) {
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setColor(Color.RED);
                            embed.setTitle("Invalid ticket ID");
                            embed.setDescription("\nHaving troubles getting Ticket ID? use `" + core.getConfig().getString("BotPrefix") + "ticketlookup <TicketName>`.");
                            e.getChannel().sendMessage(embed.build()).queue();
                        }


                    } else {
                        e.getChannel().sendMessage("No permission (Required: **MANAGE SERVER**)").queue();
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
                                        try (PreparedStatement p2 = conn.prepareStatement("SELECT * FROM discordsrvutils_ticket_allowed_roles WHERE Channel_id=? AND UserID=?")) {
                                            p2.setLong(1, e.getChannel().getIdLong());
                                            p2.setLong(2, e.getMember().getIdLong());
                                            p2.execute();
                                            try (ResultSet r2 = p2.executeQuery()) {
                                                    try (PreparedStatement p3 = conn.prepareStatement("INSERT INTO discordsrvutils_ticket_allowed_roles (Channel_id, UserID, RoleID) VALUES (?,?,?)")) {
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
                                                PreparedStatement fp1 = fconn.prepareStatement("INSERT INTO discordsrvutils_tickets (TicketID, MessageId, Opened_Category, Closed_Category, Name, ChannelID) VALUES (?, ?, ?, ?, ?, ?)");
                                                int TicketID = RANDOM.nextInt(9999);
                                                fp1.setLong(1, TicketID);
                                                fp1.setLong(2, message.getIdLong());
                                                fp1.setLong(3, mr1.getLong("Opened_Category"));
                                                fp1.setLong(4, mr1.getLong("Closed_Category"));
                                                fp1.setString(5, mr1.getString("Name"));
                                                fp1.setLong(6, e.getMessage().getMentionedChannels().get(0).getIdLong());
                                                fp1.execute();
                                                PreparedStatement mp2 = mconn.prepareStatement("SELECT * FROM discordsrvutils_ticket_allowed_roles WHERE Channel_id=? AND UserID=?");
                                                mp2.setLong(1, e.getChannel().getIdLong());
                                                mp2.setLong(2, e.getMember().getIdLong());
                                                mp2.execute();
                                                ResultSet mr2 = mp2.executeQuery();
                                                while (mr2.next()) {
                                                    PreparedStatement fm2 = fconn.prepareStatement("INSERT INTO discordsrvutils_ticket_allowed_roles (TicketID, RoleID) VALUES (?, ?)");
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
                        } else {
                            PreparedStatement p2 = conn.prepareStatement("SELECT * FROM discordsrvutils_Awaiting_Edits WHERE Channel_id=? AND UserID=?");
                            p2.setLong(1, e.getChannel().getIdLong());
                            p2.setLong(2, e.getMember().getIdLong());
                            p2.execute();
                            ResultSet r2 = p2.executeQuery();
                            if (r2.next()) {
                                if (e.getMessage().getContentRaw().equalsIgnoreCase("cancel")) {
                                    PreparedStatement p3 = conn.prepareStatement("DELETE FROM discordsrvutils_Awaiting_Edits WHERE Channel_id=? AND UserID=?");
                                    p3.setLong(1, e.getChannel().getIdLong());
                                    p3.setLong(2, e.getMember().getIdLong());
                                    p3.execute();
                                    e.getChannel().sendMessage("Cancelled.").queue();
                                }

                                if (r2.getInt("Type") != 0) {
                                    if (r2.getInt("Type") == 1) {
                                        Connection fconn = core.getDatabaseFile();
                                        PreparedStatement p3 = fconn.prepareStatement("UPDATE discordsrvutils_tickets SET Name=? WHERE TicketID=?");
                                        p3.setString(1, e.getMessage().getContentRaw());
                                        p3.setInt(2, r2.getInt("TicketID"));
                                        p3.execute();
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setTitle(e.getMessage().getContentRaw());
                                        embed.setDescription("React with \uD83D\uDCE9 to create a ticket.");
                                        embed.setColor(Color.CYAN);
                                        PreparedStatement getter = fconn.prepareStatement("SELECT * FROM discordsrvutils_tickets WHERE TicketID=?");
                                        getter.setInt(1, r2.getInt("TicketID"));
                                        getter.execute();
                                        ResultSet rgetter = getter.executeQuery(); rgetter.next();
                                        e.getGuild().getTextChannelById(rgetter.getLong("ChannelID")).editMessageById(rgetter.getLong("MessageID"), embed.build()).queue();
                                        PreparedStatement p4 = conn.prepareStatement("DELETE FROM discordsrvutils_Awaiting_Edits WHERE Channel_id=? AND UserID=?");
                                        p4.setLong(1, e.getChannel().getIdLong());
                                        p4.setLong(2, e.getMember().getIdLong());
                                        p4.execute();
                                        e.getChannel().sendMessage("Ticket renamed.").queue();
                                    } else if (r2.getInt("Type") == 3) {
                                        try {
                                            Connection fconn = core.getDatabaseFile();
                                            if (e.getJDA().getCategoryById(Long.parseLong(e.getMessage().getContentRaw())) == null) {
                                               e.getChannel().sendMessage("Category was not found.").queue();
                                           }
                                           PreparedStatement p3 = fconn.prepareStatement("UPDATE discordsrvutils_tickets SET Closed_Category=? WHERE TicketID=?");
                                           p3.setLong(1, Long.parseLong(e.getMessage().getContentRaw()));
                                           p3.setInt(2, r2.getInt("TicketID"));
                                           p3.execute();
                                            PreparedStatement p4 = conn.prepareStatement("DELETE FROM discordsrvutils_Awaiting_Edits WHERE Channel_id=? AND UserID=?");
                                            p4.setLong(1, e.getChannel().getIdLong());
                                            p4.setLong(2, e.getMember().getIdLong());
                                            p4.execute();
                                            e.getChannel().sendMessage("Changed Closed category.").queue();

                                        } catch (NumberFormatException ex) {
                                            e.getChannel().sendMessage("Not even a valid ID. Try again").queue();
                                        }
                                    } else if (r2.getInt("Type") == 2) {
                                        try {
                                            Connection fconn = core.getDatabaseFile();
                                            if (e.getJDA().getCategoryById(Long.parseLong(e.getMessage().getContentRaw())) == null) {
                                                e.getChannel().sendMessage("Category was not found.").queue();
                                            }
                                            PreparedStatement p3 = fconn.prepareStatement("UPDATE discordsrvutils_tickets SET Opened_Category=? WHERE TicketID=?");
                                            p3.setLong(1, Long.parseLong(e.getMessage().getContentRaw()));
                                            p3.setInt(2, r2.getInt("TicketID"));
                                            p3.execute();
                                            PreparedStatement p4 = conn.prepareStatement("DELETE FROM discordsrvutils_Awaiting_Edits WHERE Channel_id=? AND UserID=?");
                                            p4.setLong(1, e.getChannel().getIdLong());
                                            p4.setLong(2, e.getMember().getIdLong());
                                            p4.execute();
                                            e.getChannel().sendMessage("Changed Opened category.").queue();

                                        } catch (NumberFormatException ex) {
                                            e.getChannel().sendMessage("Not even a valid ID. Try again").queue();
                                        }

                                    } else if (r2.getInt("Type") == 4) {
                                        if (e.getMessage().getMentionedRoles().isEmpty()) {
                                            e.getChannel().sendMessage("No roles were mentioned. Please try again").queue();
                                        } else {
                                            Connection fconn = core.getDatabaseFile();
                                            PreparedStatement p3 = fconn.prepareStatement("DELETE FROM discordsrvutils_ticket_allowed_roles WHERE TicketID=?");
                                            p3.setInt(1, r2.getInt("TicketID"));
                                            p3.execute();
                                            for (Role role : e.getMessage().getMentionedRoles()) {
                                                PreparedStatement p4 = fconn.prepareStatement("INSERT INTO discordsrvutils_ticket_allowed_roles (TicketID, RoleID) VALUES (?, ?)");
                                                p4.setInt(1, r2.getInt("TicketID"));
                                                p4.setLong(2, role.getIdLong());
                                                p4.execute();
                                                PreparedStatement p5 = conn.prepareStatement("DELETE FROM discordsrvutils_Awaiting_Edits WHERE Channel_id=? AND UserID=?");
                                                p5.setLong(1, e.getChannel().getIdLong());
                                                p5.setLong(2, e.getMember().getIdLong());
                                                p5.execute();
                                                e.getChannel().sendMessage("Changed ticket view allowed roles.").queue();
                                            }

                                        }
                                    } else if (r2.getInt("Type") == 5) {
                                        if (e.getMessage().getMentionedChannels().isEmpty()) {
                                            e.getChannel().sendMessage("No channels mentioned. Please try again").queue();
                                        } else {
                                            Connection fconn = core.getDatabaseFile();
                                            PreparedStatement p3 = fconn.prepareStatement("SELECT * FROM discordsrvutils_ticket_allowed_roles WHERE TicketID=?");
                                            p3.setInt(1, r2.getInt("TicketID"));
                                            p3.execute();
                                            ResultSet r3 = p3.executeQuery(); r3.next();
                                            PreparedStatement something = fconn.prepareStatement("SELECT * FROM discordsrvutils_tickets WHERE TicketID=?");
                                            something.setInt(1, r2.getInt("TicketID"));
                                            something.execute();
                                            ResultSet rsomething = something.executeQuery(); rsomething.next();
                                            e.getGuild().getTextChannelById(rsomething.getLong("ChannelID")).deleteMessageById(rsomething.getLong("MessageID")).queue();
                                            EmbedBuilder embed = new EmbedBuilder();
                                            PreparedStatement p5 = fconn.prepareStatement("SELECT * FROM discordsrvutils_tickets WHERE TicketID=?");
                                            p5.setInt(1, r2.getInt("TicketID"));
                                            p5.execute();
                                            ResultSet r5 = p5.executeQuery(); r5.next();
                                            embed.setTitle(r5.getString("Name"));
                                            embed.setDescription("React with \uD83D\uDCE9 to create a ticket.");
                                            embed.setColor(Color.CYAN);
                                            e.getGuild().getTextChannelById(e.getMessage().getMentionedChannels().get(0).getIdLong()).sendMessage(embed.build()).queue(msg -> {
                                                try{
                                                    Connection conn3 = core.getMemoryConnection();
                                                    PreparedStatement mp2 = conn3.prepareStatement("SELECT * FROM discordsrvutils_Awaiting_Edits WHERE Channel_id=? AND UserID=?");
                                                    mp2.setLong(1, e.getChannel().getIdLong());
                                                    mp2.setLong(2, e.getMember().getIdLong());
                                                    mp2.execute();
                                                    ResultSet mr2 = mp2.executeQuery(); mr2.next();
                                                    Connection fconn2 = core.getDatabaseFile();
                                                    PreparedStatement p6 = fconn2.prepareStatement("UPDATE discordsrvutils_tickets SET ChannelID=?, MessageID=? WHERE TicketID=?");
                                                    p6.setLong(1, e.getMessage().getMentionedChannels().get(0).getIdLong());
                                                    p6.setLong(2, msg.getIdLong());
                                                    p6.setInt(3, mr2.getInt("TicketID"));
                                                    p6.execute();
                                                    msg.addReaction("\uD83D\uDCE9").queue();
                                                    PreparedStatement p7 = conn3.prepareStatement("DELETE FROM discordsrvutils_Awaiting_Edits WHERE Channel_id=? AND UserID=?");
                                                    p7.setLong(1, e.getChannel().getIdLong());
                                                    p7.setLong(2, e.getMember().getIdLong());
                                                    p7.execute();
                                                    e.getChannel().sendMessage("Ticket channel changed.").queue();


                                                } catch (SQLException ex) {
                                                    ex.printStackTrace();
                                                }
                                            });

                                        }
                                    }
                                }
                            }
                            if (e.getMessage().getMentionedMembers().contains(e.getGuild().getSelfMember())) {
                                e.getChannel().sendMessage("**My prefix is** `" + core.getConfig().getString("BotPrefix") + "`").queue();
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
                        PreparedStatement pr = conn.prepareStatement("SELECT * FROM discordsrvutils_Opened_Tickets WHERE UserID=?");
                        pr.setLong(1, e.getMember().getIdLong());
                        pr.execute();
                        ResultSet rr1 = pr.executeQuery();
                        if (!rr1.next()) {
                            e.getReaction().removeReaction(e.getMember().getUser()).queue();
                            e.getGuild().getCategoryById(r1.getLong("Opened_Category")).createTextChannel("opened-" + e.getMember().getEffectiveName()).queue(channel -> {
                                channel.getManager().setTopic("Ticket created by " + e.getMember().getUser().getName()).queue();
                                channel.createPermissionOverride(e.getMember()).grant(Permission.VIEW_CHANNEL).queue();
                                try {
                                    Connection conn2 = core.getDatabaseFile();
                                    PreparedStatement p2 = conn2.prepareStatement("SELECT * FROM discordsrvutils_ticket_allowed_roles WHERE TicketID=?");
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
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setTitle("Ticket");
                                embed.setColor(Color.GREEN);
                                try {
                                    Connection conn1 = core.getDatabaseFile();
                                    PreparedStatement cp1 = conn1.prepareStatement("SELECT * FROM discordsrvutils_tickets WHERE MessageId=?");
                                    cp1.setLong(1, e.getMessageIdLong());
                                    cp1.execute();
                                    ResultSet cr1 = cp1.executeQuery(); cr1.next();
                                    embed.setDescription("here is your ticket.\nReact with \uD83D\uDD12 to close this ticket. or use `" + core.getConfig().getString("BotPrefix") + "close`.\n\n**TicketName:** " + cr1.getString("Name"));
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                                channel.sendMessage(e.getMember().getAsMention() + " Welcome").queue();
                                channel.sendMessage(embed.build()).queue(message2 -> {
                                    message2.addReaction("\uD83D\uDD12").queue();
                                    try {
                                        Connection fconn2 = core.getDatabaseFile();
                                        PreparedStatement fp1 = fconn2.prepareStatement("INSERT INTO discordsrvutils_Opened_Tickets (UserID, MessageID, TicketID, Channel_id) VALUES (?, ?, ?, ?)");
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
                        } else e.getReaction().removeReaction(e.getUser()).queue();
                    } else {
                        Connection conn2 = core.getDatabaseFile();
                        PreparedStatement p2 = conn2.prepareStatement("SELECT * FROM discordsrvutils_Opened_Tickets WHERE MessageID=?");
                        p2.setLong(1, e.getMessageIdLong());
                        p2.execute();
                        ResultSet r2 = p2.executeQuery();
                        if (r2.next()) {

                            PreparedStatement closed = conn2.prepareStatement("SELECT * FROM discordsrvutils_Opened_Tickets WHERE MessageID=?");
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
                                embed.setColor(Color.YELLOW);
                                e.getChannel().sendMessage(embed.build()).queue(msg -> {
                                    try {
                                        Connection conn3 = core.getDatabaseFile();
                                        PreparedStatement prpstmt = conn3.prepareStatement("SELECT * FROM discordsrvutils_Opened_Tickets WHERE MessageID=?");
                                        prpstmt.setLong(1, e.getMessageIdLong());
                                        prpstmt.execute();
                                        ResultSet rr = prpstmt.executeQuery();
                                        rr.next();
                                        PreparedStatement ppp = conn3.prepareStatement("INSERT INTO discordsrvutils_Closed_Tickets (UserID, MessageID, TicketID, Channel_id, Closed_Message) VALUES (?, ?, ?, ?, ?)");
                                        ppp.setLong(1, rr.getLong("UserID"));
                                        ppp.setLong(2, e.getMessageIdLong());
                                        ppp.setLong(3, rr.getInt("TicketID"));
                                        ppp.setLong(4, rr.getLong("Channel_id"));
                                        ppp.setLong(5, msg.getIdLong());
                                        ppp.execute();
                                        PreparedStatement prp = conn3.prepareStatement("DELETE FROM discordsrvutils_Opened_Tickets WHERE MessageID=?");
                                        prp.setLong(1, e.getMessageIdLong());
                                        prp.execute();
                                    } catch (SQLException ex) {
                                        ex.printStackTrace();
                                    }
                                    msg.addReaction("\uD83D\uDDD1️").queue();
                                });
                                e.getTextChannel().getPermissionOverride(e.getGuild().getMemberById(r2.getLong("UserID"))).getManager().setDeny(Permission.VIEW_CHANNEL).queue();
                                e.getReaction().removeReaction(e.getUser()).queue();
                                e.getTextChannel().getManager().setParent(e.getGuild().getCategoryById(ticketss.getLong("Closed_Category"))).queue();
                                e.getTextChannel().getManager().setName(e.getTextChannel().getName().replace("opened", "closed")).queue();

                            }
                        } else {
                            //Message must be nothing or a complete closure message
                            if (e.getReactionEmote().getName().equals("\uD83D\uDDD1️")) {
                                Connection conn5 = core.getDatabaseFile();
                                PreparedStatement p3 = conn5.prepareStatement("SELECT * FROM discordsrvutils_Closed_Tickets WHERE Closed_Message=?");
                                p3.setLong(1, e.getMessageIdLong());
                                p3.execute();
                                ResultSet r3 = p3.executeQuery();
                                if (r3.next()) {
                                    e.getTextChannel().delete().queue();
                                }
                            }
                            else if (e.getReactionEmote().getName().equals("\uD83D\uDD13")) {
                                Connection conn5 = core.getDatabaseFile();
                                PreparedStatement p3 = conn5.prepareStatement("SELECT * FROM discordsrvutils_Closed_Tickets WHERE Closed_Message=?");
                                p3.setLong(1, e.getMessageIdLong());
                                p3.execute();
                                ResultSet r3 = p3.executeQuery();

                            }
                            try  {
                                Connection con = core.getMemoryConnection();
                                PreparedStatement p3 = con.prepareStatement("SELECT * FROM discordsrvutils_Awaiting_Edits WHERE UserID=? AND Channel_id=? AND MessageID=?");
                                p3.setLong(1, e.getMember().getIdLong());
                                p3.setLong(2, e.getTextChannel().getIdLong());
                                p3.setLong(3, e.getMessageIdLong());
                                p3.execute();
                                ResultSet r3 = p3.executeQuery();
                                if (r3.next()) {
                                    e.getReaction().removeReaction(e.getUser()).queue();
                                    if (r3.getInt("Type") == 0) {
                                    if (e.getReactionEmote().getName().equals("1️⃣")) {
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setTitle("Edit ticket (Name)");
                                        embed.setColor(Color.RED);
                                        embed.setDescription("Please send the new name of the ticket.");
                                        e.getTextChannel().sendMessage(embed.build()).queue();
                                        PreparedStatement p4 = con.prepareStatement("UPDATE discordsrvutils_Awaiting_Edits SET Type=1 WHERE Channel_id=? AND UserID=? AND MessageID=?");
                                        p4.setLong(1, e.getChannel().getIdLong());
                                        p4.setLong(2, e.getMember().getIdLong());
                                        p4.setLong(3, e.getMessageIdLong());
                                        p4.execute();
                                    } else  if (e.getReactionEmote().getName().equals("2️⃣")) {
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setTitle("Edit ticket (Opened category)");
                                        embed.setColor(Color.RED);
                                        embed.setDescription("Please send the new Opened category ID.");
                                        e.getTextChannel().sendMessage(embed.build()).queue();
                                        PreparedStatement p4 = con.prepareStatement("UPDATE discordsrvutils_Awaiting_Edits SET Type=2 WHERE Channel_id=? AND UserID=? AND MessageID=?");
                                        p4.setLong(1, e.getChannel().getIdLong());
                                        p4.setLong(2, e.getMember().getIdLong());
                                        p4.setLong(3, e.getMessageIdLong());
                                        p4.execute();

                                    } else if (e.getReactionEmote().getName().equals("3️⃣")) {
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setTitle("Edit ticket (Closed category)");
                                        embed.setColor(Color.RED);
                                        embed.setDescription("Please send the new Closed category ID.");
                                        e.getTextChannel().sendMessage(embed.build()).queue();
                                        PreparedStatement p4 = con.prepareStatement("UPDATE discordsrvutils_Awaiting_Edits SET Type=3 WHERE Channel_id=? AND UserID=? AND MessageID=?");
                                        p4.setLong(1, e.getChannel().getIdLong());
                                        p4.setLong(2, e.getMember().getIdLong());
                                        p4.setLong(3, e.getMessageIdLong());
                                        p4.execute();

                                    } else if (e.getReactionEmote().getName().equals("4️⃣")) {
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setTitle("Edit ticket (Allowed roles)");
                                        embed.setColor(Color.RED);
                                        embed.setDescription("Please mention the new ticket view allowed roles.");
                                        e.getTextChannel().sendMessage(embed.build()).queue();
                                        PreparedStatement p4 = con.prepareStatement("UPDATE discordsrvutils_Awaiting_Edits SET Type=4 WHERE Channel_id=? AND UserID=? AND MessageID=?");
                                        p4.setLong(1, e.getChannel().getIdLong());
                                        p4.setLong(2, e.getMember().getIdLong());
                                        p4.setLong(3, e.getMessageIdLong());
                                        p4.execute();

                                    } else if (e.getReactionEmote().getName().equals("5️⃣")) {
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setTitle("Edit ticket (Message channel)");
                                        embed.setColor(Color.RED);
                                        embed.setDescription("Please mention the new message channel.\n\n**Warning:** Old message channel will no longer work.");
                                        e.getTextChannel().sendMessage(embed.build()).queue();
                                        PreparedStatement p4 = con.prepareStatement("UPDATE discordsrvutils_Awaiting_Edits SET Type=5 WHERE Channel_id=? AND UserID=? AND MessageID=?");
                                        p4.setLong(1, e.getChannel().getIdLong());
                                        p4.setLong(2, e.getMember().getIdLong());
                                        p4.setLong(3, e.getMessageIdLong());
                                        p4.execute();
                                    }
                                    }
                                }
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        }

                    }
                }
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent e) {
        try {
            Connection conn = core.getDatabaseFile();
            PreparedStatement p1 = conn.prepareStatement("DELETE FROM discordsrvutils_Opened_Tickets WHERE Channel_id=?");
            p1.setLong(1, e.getChannel().getIdLong());
            p1.execute();
            PreparedStatement p2 = conn.prepareStatement("DELETE FROM discordsrvutils_Closed_Tickets WHERE Channel_id=?");
            p2.setLong(1, e.getChannel().getIdLong());
            p2.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }






}
