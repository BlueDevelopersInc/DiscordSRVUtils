package tech.bedev.discordsrvutils.events;

import com.vdurmont.emoji.EmojiParser;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.dependencies.jda.api.events.channel.text.TextChannelDeleteEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberJoinEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageDeleteEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.react.MessageReactionAddEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.manager.UUIDManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import tech.bedev.discordsrvutils.DiscordSRVUtils;
import tech.bedev.discordsrvutils.Managers.ConfOptionsManager;
import tech.bedev.discordsrvutils.Managers.Stopwatch;
import tech.bedev.discordsrvutils.Managers.Tickets;
import tech.bedev.discordsrvutils.Managers.TimerManager;
import tech.bedev.discordsrvutils.Person.MessageType;
import tech.bedev.discordsrvutils.Person.Person;
import tech.bedev.discordsrvutils.leaderboard.LeaderBoardManager;
import tech.bedev.discordsrvutils.utils.PlayerUtil;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.*;

public class JDAEvents extends ListenerAdapter {
    private static final Long EXPIRATION_NANOS = Duration.ofSeconds(60L).toNanos();
    private static final Random RANDOM = new Random();
    private DiscordSRVUtils core;
    private ConfOptionsManager conf;
    private Tickets tickets;

    public JDAEvents(DiscordSRVUtils core) {
        this.core = core;
        this.tickets = new Tickets(core);
        conf = new ConfOptionsManager(core);
    }

    public static boolean isNumberic(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        if (DiscordSRVUtils.BotSettingsconfig.isBungee()) return;
        if (e.getUser().isBot()) {
            if (DiscordSRVUtils.Config.isIgnoreBots()) {
                return;
            }
        }
        Bukkit.getScheduler().runTask(core, () -> {
            if (Bukkit.getPluginManager().isPluginEnabled("AdvancedBan")) {
                UUID puuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(e.getUser().getId());
                if (puuid != null) {
                    String pname = Bukkit.getOfflinePlayer(puuid).getName();

                    if (PunishmentManager.get().isBanned(UUIDManager.get().getUUID(pname))) {
                        if (DiscordSRVUtils.BansIntegrationconfig.isSyncPunishmentsWithDiscord()) {
                            e.getGuild().ban(e.getMember().getUser(), 0, "DiscordSRVUtils banned by Advancedban").queue();
                            return;
                        }
                    } else if (PunishmentManager.get().isMuted(UUIDManager.get().getUUID(pname))) {
                        if (DiscordSRVUtils.BansIntegrationconfig.isSyncPunishmentsWithDiscord()) {
                            e.getGuild().addRoleToMember(e.getMember(), e.getGuild().getRoleById(DiscordSRVUtils.Moderationconfig.MutedRole())).queue();
                        }
                    }
                }


            }

            EmbedBuilder embed = new EmbedBuilder().setDescription(String.join("\n",
                    DiscordSRVUtils.Config.WelcomerMessage())
                    .replace("[User_Name]", e.getMember().getUser().getName())
                    .replace("[User_Mention]", e.getMember().getAsMention())
                    .replace("[User_tag]", e.getMember().getUser().getAsTag())
            );


            String config = DiscordSRVUtils.Config.WelcomerEmbedColor();

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
                TextChannel ch = e.getGuild().getTextChannelById(DiscordSRVUtils.Config.WelcomerChannel());
                if (ch != null)
                    ch.sendMessage(embed.build()).queue();

            }
            if (DiscordSRVUtils.Config.isJoinMessageToOnlinePlayers()) {
                String message = DiscordSRVUtils.Config.McWelcomerMessage();
                if (message != null) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message)
                            .replace("[User_Tag]", e.getMember().getUser().getAsTag())
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
        String prefix = DiscordSRVUtils.BotSettingsconfig.BotPrefix();
        if (args[0].equalsIgnoreCase(prefix + "createticket")) {
            if (!DiscordSRVUtils.BotSettingsconfig.isBungee()) {

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
            }
        } else if (args[0].equalsIgnoreCase(prefix + "ticketlookup")) {
            if (!DiscordSRVUtils.BotSettingsconfig.isBungee()) {
                if (e.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                    if (!(args.length >= 2)) {
                        e.getChannel().sendMessage("**Usage:** " + prefix + "ticketlookup <ticket name>").queue();

                    } else {
                        try (Connection conn = core.getDatabaseFile()) {
                            String argss = "";
                            for (int i = 1; i < args.length; i++) {
                                argss = argss + args[i] + " ";
                            }
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
                } else {
                    e.getChannel().sendMessage("No permission (Required: **MANAGE SERVER**)").queue();
                }
            }
        } else if (args[0].equalsIgnoreCase(prefix + "close")) {
            if (!DiscordSRVUtils.BotSettingsconfig.isBungee()) {
                try (Connection conn = core.getDatabaseFile()) {

                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_Opened_Tickets WHERE Channel_id=?");
                    p1.setLong(1, e.getChannel().getIdLong());
                    p1.execute();
                    ResultSet r1 = p1.executeQuery();
                    if (r1.next()) {

                        PreparedStatement closed = conn.prepareStatement("SELECT * FROM discordsrvutils_Opened_Tickets WHERE MessageID=?");
                        closed.setLong(1, e.getMessageIdLong());
                        closed.execute();
                        ResultSet closed2 = closed.executeQuery();
                        closed2.next();
                        PreparedStatement tickets = conn.prepareStatement("SELECT * FROM discordsrvutils_tickets WHERE TicketID=?");
                        tickets.setLong(1, r1.getInt("TicketID"));
                        tickets.execute();
                        ResultSet ticketss = tickets.executeQuery();
                        ticketss.next();
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setTitle("Ticket Closed");
                        embed.setColor(Color.YELLOW);
                        embed.setDescription("Ticket Closed by " + e.getMember().getAsMention() + "");
                        e.getChannel().sendMessage(embed.build()).queue(msg -> {
                            try (Connection conn3 = core.getDatabaseFile()) {
                                ;
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
                        ResultSet rr = pp.executeQuery();
                        rr.next();
                        System.out.println(rr.getLong("Closed_Category"));
                        System.out.println(ticketss.getLong("Closed_Category"));
                        e.getChannel().getManager().setParent(e.getGuild().getCategoryById(ticketss.getLong("Closed_Category"))).queue();
                        e.getChannel().getManager().setName(e.getChannel().getName().replace("opened", "closed")).queue();


                    } else e.getChannel().sendMessage("You are not on an opened ticket").queue();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }

        } else if (args[0].equalsIgnoreCase(prefix + "editticket")) {
            if (!DiscordSRVUtils.BotSettingsconfig.isBungee()) {
                if (!(args.length >= 2)) {
                    e.getChannel().sendMessage("**Usage:** " + prefix + "editticket <Ticket ID>").queue();
                } else {
                    if (e.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                        try (Connection fconn = core.getDatabaseFile()) {
                            Connection conn = core.getMemoryConnection();
                            PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_Awaiting_Edits WHERE Channel_id=? AND UserID=?");
                            p1.setLong(1, e.getChannel().getIdLong());
                            p1.setLong(2, e.getMember().getIdLong());
                            p1.execute();
                            ResultSet r1 = p1.executeQuery();

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
                                    embed.setDescription("\nHaving troubles getting Ticket ID? use `" + prefix + "ticketlookup <TicketName>`.");
                                    e.getChannel().sendMessage(embed.build()).queue();

                                }
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        } catch (NumberFormatException ex) {
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setColor(Color.RED);
                            embed.setTitle("Invalid ticket ID");
                            embed.setDescription("\nHaving troubles getting Ticket ID? use `" + prefix + "ticketlookup <TicketName>`.");
                            e.getChannel().sendMessage(embed.build()).queue();
                        }
                    } else {
                        e.getChannel().sendMessage("No permission (Required: **MANAGE SERVER**)").queue();
                    }
                }
            }
        } else if (args[0].equalsIgnoreCase(prefix + "help")) {
            if (!DiscordSRVUtils.BotSettingsconfig.isBungee()) {
                Map<Integer, String> map = new HashMap<>();
                int pages = 0;
                pages++;
                map.put(pages, "Tickets");
                if (DiscordSRVUtils.Levelingconfig.Leveling_Enabled()) {
                    pages++;
                    map.put(pages, "Leveling");
                }
                if (DiscordSRVUtils.Moderationconfig.isModeratorCommandsEnabled()) {
                    pages++;
                    map.put(pages, "Moderation");
                }
                if (DiscordSRVUtils.SuggestionsConfig.isEnabled()) {
                    pages++;
                    map.put(pages, "Suggestions");
                }
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Page 1 | Tickets");
                embed.setDescription("`" + prefix + "createticket`, `" + prefix + "ticketlookup`, `" + prefix + "editticket`, `" + prefix + "close`, `" + prefix + "deleteticket`, `" + prefix + "editticket`");
                embed.setColor(Color.GREEN);
                e.getChannel().sendMessage(embed.build()).queue(msg -> {
                    try (Connection conn = core.getMemoryConnection()) {
                        PreparedStatement p1 = conn.prepareStatement("INSERT INTO helpmsges (userid, Channel, MessageID, lastOutput, Page) VALUES (?, ?, ?, ?, ?)");
                        p1.setLong(1, e.getMember().getIdLong());
                        p1.setLong(2, e.getChannel().getIdLong());
                        p1.setLong(3, msg.getIdLong());
                        p1.setLong(4, System.currentTimeMillis());
                        p1.setInt(5, 1);
                        p1.execute();
                        msg.addReaction("⬅️").queue();
                        msg.addReaction("➡️").queue();
                        msg.addReaction("\uD83D\uDDD1️").queue();
                    } catch (SQLException ex) {
                        e.getMessage().delete().queue();
                        ex.printStackTrace();
                    }
                });
            }
        } else if (args[0].equalsIgnoreCase(prefix + "deleteticket")) {
            if (!DiscordSRVUtils.BotSettingsconfig.isBungee()) {

                if (!(args.length >= 2)) {
                    e.getChannel().sendMessage("**Usage:** " + prefix + "deleteticketticket <Ticket ID>").queue();
                } else {
                    if (e.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                        try (Connection conn = core.getDatabaseFile()) {
                            ;
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
                                embed.setDescription("\nHaving troubles getting Ticket ID? use `" + prefix + "ticketlookup <TicketName>`.");
                                e.getChannel().sendMessage(embed.build()).queue();
                            }

                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        } catch (NumberFormatException ex) {
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setColor(Color.RED);
                            embed.setTitle("Invalid ticket ID");
                            embed.setDescription("\nHaving troubles getting Ticket ID? use `" + prefix + "ticketlookup <TicketName>`.");
                            e.getChannel().sendMessage(embed.build()).queue();
                        }


                    } else {
                        e.getChannel().sendMessage("No permission (Required: **MANAGE SERVER**)").queue();
                    }
                }
            }
        } else if (args[0].equalsIgnoreCase(prefix + "level") || args[0].equalsIgnoreCase(prefix + "rank")) {
            if (!DiscordSRVUtils.BotSettingsconfig.isBungee()) {
                if (DiscordSRVUtils.Levelingconfig.Leveling_Enabled()) {
                    if (!(args.length >= 2)) {
                        Person p = core.getPersonByDiscordID(e.getMember().getIdLong());
                        if (!p.isLinked()) {
                            e.getChannel().sendMessage("You are not linked. Use `/discord link` to link your account.").queue();
                        } else if (!p.isBukkitCached()) {
                            e.getChannel().sendMessage("You didnt join for a long time").queue();
                            return;
                        } else {
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setDescription("**Level:** " + p.getLevel() + "\n\n**XP:** " + p.getXP() + "\n\n**Rank:** #" + p.getRank());
                            embed.setTitle("Level for " + Bukkit.getOfflinePlayer(p.getMinecraftUUID()).getName());
                            embed.setColor(Color.CYAN);
                            embed.setThumbnail("https://crafatar.com/avatars/" + p.getMinecraftUUID());
                            e.getChannel().sendMessage(embed.build()).queue();

                        }
                    } else {
                        if (e.getMessage().getMentionedMembers().isEmpty()) {
                            Person p = core.getPersonByUUID(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
                            if (p == null) {
                                e.getChannel().sendMessage("Player has never joined before.").queue();
                            } else {
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setDescription("**Level:** " + p.getLevel() + "\n\n**XP:** " + p.getXP() + "\n\n**Rank:** #" + p.getRank());
                                embed.setTitle("Level for " + Bukkit.getOfflinePlayer(p.getMinecraftUUID()).getName());
                                embed.setColor(Color.CYAN);
                                embed.setThumbnail("https://crafatar.com/avatars/" + p.getMinecraftUUID());
                                e.getChannel().sendMessage(embed.build()).queue();
                            }
                        } else {
                            Member mm = e.getMessage().getMentionedMembers().get(0);
                            Person p = core.getPersonByDiscordID(mm.getIdLong());
                            if (p.isLinked()) {
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setDescription("**Level:** " + p.getLevel() + "\n\n**XP:** " + p.getXP() + "\n\n**Rank:** #" + p.getRank());
                                embed.setTitle("Level for " + Bukkit.getOfflinePlayer(p.getMinecraftUUID()).getName());
                                embed.setColor(Color.CYAN);
                                embed.setThumbnail("https://crafatar.com/avatars/" + p.getMinecraftUUID());
                                e.getChannel().sendMessage(embed.build()).queue();
                            } else if (p.isBukkitCached()) {
                                e.getChannel().sendMessage("This player has never joined before").queue();
                                return;
                            } else {
                                e.getChannel().sendMessage("This user is not linked.").queue();
                            }
                        }
                    }
                    return;
                } else {
                }
            }
        } else if (args[0].equalsIgnoreCase(prefix + "ban")) {
            if (!DiscordSRVUtils.Moderationconfig.isModeratorCommandsEnabled()) return;
            if (DiscordSRVUtils.BotSettingsconfig.isBungee()) return;
            boolean canuse = false;
            for (Role role : e.getMember().getRoles()) {
                if (DiscordSRVUtils.Moderationconfig.rolesAllowedToUseModeratorCommands().contains(role.getId())) {
                    canuse = true;
                }
                if (DiscordSRVUtils.Moderationconfig.rolesAllowedToUseModeratorCommands().contains(role.getName())) {
                    canuse = true;
                }
            }
            if (!canuse) {
                if (!e.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                    e.getChannel().sendMessage("You don't have permission to use this command.").queue();
                    return;
                }
            }
            if (!(args.length >= 2)) {
                e.getChannel().sendMessage("Who to ban? Usage: " + prefix + "ban <member> <reason>").queue();
                return;
            } else {
                if (!(args.length >= 3)) {
                    try {
                        Long.parseLong(args[1]);
                        Member membertoban = e.getGuild().getMemberById(args[1]);
                        if (membertoban == null) {
                            e.getChannel().sendMessage("Member not found. Usage " + prefix + "ban <member> <reason>").queue();
                            return;
                        } else {
                            if (!e.getGuild().getSelfMember().canInteract(membertoban)) {
                                e.getChannel().sendMessage("Unable to ban Member Because his role is higher than me.").queue();
                                return;
                            }
                            UUID mcUUID = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(membertoban.getId());
                            if (mcUUID != null) {
                            }
                            e.getGuild().ban(membertoban, 0, "Banned by " + e.getMember().getUser().getAsTag()).queue();
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setColor(Color.GREEN);
                            embed.setDescription("**_" + membertoban.getUser().getAsTag() + " Was banned._**");
                            e.getChannel().sendMessage(embed.build()).queue();
                        }

                    } catch (NumberFormatException ex) {
                        if (e.getMessage().getMentionedMembers().isEmpty()) {
                            e.getChannel().sendMessage("Who to ban? Usage:" + prefix + "ban <member> <reason>").queue();
                            return;
                        } else {

                            Member membertoban = e.getMessage().getMentionedMembers().get(0);
                            if (!e.getGuild().getSelfMember().canInteract(membertoban)) {
                                e.getChannel().sendMessage("Unable to ban Member Because his role is higher than me.").queue();
                                return;
                            }
                            e.getGuild().ban(membertoban, 0, "Banned by " + e.getMember().getUser().getAsTag()).queue();
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setColor(Color.GREEN);
                            embed.setDescription("**_" + membertoban.getUser().getAsTag() + " Was banned._**");
                            e.getChannel().sendMessage(embed.build()).queue();
                            return;
                        }

                    }
                } else {
                    String reason = "";
                    for (int i = 2; i < args.length; i++) {
                        reason = reason + args[i] + " ";
                    }
                    try {
                        Long.parseLong(args[1]);
                        Member membertoban = e.getGuild().getMemberById(args[1]);
                        if (membertoban == null) {
                            e.getChannel().sendMessage("Member not found. Usage " + prefix + "ban <member> <reason>").queue();
                            return;
                        } else {
                            if (!e.getGuild().getSelfMember().canInteract(membertoban)) {
                                e.getChannel().sendMessage("Unable to ban Member Because his role is higher than me.").queue();
                                return;
                            }
                            e.getGuild().ban(membertoban, 0, "Banned by " + e.getMember().getUser().getAsTag() + " (" + reason + ")").queue();
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setColor(Color.GREEN);
                            embed.setDescription("**_" + membertoban.getUser().getAsTag() + " Was banned._**");
                            e.getChannel().sendMessage(embed.build()).queue();
                        }

                    } catch (NumberFormatException ex) {
                        if (e.getMessage().getMentionedMembers().isEmpty()) {
                            e.getChannel().sendMessage("Who to ban? Usage:" + prefix + "ban <member> <reason>").queue();
                            return;
                        } else {
                            Member membertoban = e.getMessage().getMentionedMembers().get(0);
                            if (!e.getGuild().getSelfMember().canInteract(membertoban)) {
                                e.getChannel().sendMessage("Unable to ban Member Because his role is higher than me.").queue();
                                return;
                            }
                            e.getGuild().ban(membertoban, 0, "Banned by " + e.getMember().getUser().getAsTag() + " (" + reason + ")").queue();
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setColor(Color.GREEN);
                            embed.setDescription("**_" + membertoban.getUser().getAsTag() + " Was banned._**");
                            e.getChannel().sendMessage(embed.build()).queue();
                            return;
                        }

                    }

                }
            }
            return;
        } else if (args[0].equalsIgnoreCase(prefix + "unban")) {
            if (!DiscordSRVUtils.Moderationconfig.isModeratorCommandsEnabled()) return;
            if (DiscordSRVUtils.BotSettingsconfig.isBungee()) return;
            boolean canuse = false;
            for (Role role : e.getMember().getRoles()) {
                if (DiscordSRVUtils.Moderationconfig.rolesAllowedToUseModeratorCommands().contains(role.getId())) {
                    canuse = true;
                }
                if (DiscordSRVUtils.Moderationconfig.rolesAllowedToUseModeratorCommands().contains(role.getName())) {
                    canuse = true;
                }
            }
            if (!canuse) {
                if (!e.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                    e.getChannel().sendMessage("You don't have permission to use this command.").queue();
                    return;
                }
            }
            if (!(args.length >= 2)) {
                e.getChannel().sendMessage("Who to unban? Usage: " + prefix + "unban <member>").queue();
                return;
            } else {
                try {
                    Long.parseLong(args[1]);
                    e.getGuild().retrieveBanById(args[1]).queue(success -> {
                        Long membertounbanid = Long.parseLong(args[1]);
                        e.getGuild().unban(membertounbanid.toString()).queue();
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setColor(Color.GREEN);
                        embed.setDescription("**_" + success.getUser().getAsTag() + " Was Unbanned._**");
                        e.getChannel().sendMessage(embed.build()).queue();
                    }, failure -> {
                        e.getChannel().sendMessage("User is not banned.").queue();

                    });
                } catch (NumberFormatException ex) {
                    e.getChannel().sendMessage("member must be an id. Usage: " + prefix + "unban <member>").queue();
                    return;

                }
            }
            return;
        } else if (args[0].equalsIgnoreCase(prefix + "mute")) {
            if (DiscordSRVUtils.BotSettingsconfig.isBungee()) return;
            if (!DiscordSRVUtils.Moderationconfig.isModeratorCommandsEnabled()) return;
            if (isModerator(e.getMember())) {
                if (!(args.length >= 2)) {
                    e.getChannel().sendMessage("Who to mute? Usage: " + prefix + "mute <member>").queue();
                    return;
                } else if (args.length >= 2) {
                    try {
                        Long.parseLong(args[1]);
                        Member membertomute = e.getGuild().getMemberById(Long.parseLong(args[1]));
                        Role mutedrole = e.getGuild().getRoleById(DiscordSRVUtils.Moderationconfig.MutedRole());
                        if (membertomute == null) {
                            e.getChannel().sendMessage("Member not found.").queue();
                            return;
                        }
                        if (mutedrole == null) {
                            e.getChannel().sendMessage("We could not mute this Member for some reason. If you are the owner please check server console").queue();
                            core.getLogger().severe("Role not found on Guild \"" + e.getGuild().getName() + "\" Role ID: " + DiscordSRVUtils.Moderationconfig.MutedRole());
                            return;
                        } else if (e.getGuild().getSelfMember().canInteract(mutedrole)) {
                            if (membertomute.getRoles().contains(mutedrole)) {
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setColor(Color.RED);
                                embed.setDescription("**_Member is already muted._**");
                                e.getChannel().sendMessage(embed.build()).queue();
                            } else {
                                e.getGuild().addRoleToMember(membertomute, mutedrole).queue();
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setColor(Color.GREEN);
                                embed.setDescription("**_" + membertomute.getUser().getAsTag() + " Was muted._**");
                                e.getChannel().sendMessage(embed.build()).queue();
                            }
                        } else {
                            e.getChannel().sendMessage("I am unable to give the muted role. Please lower muted role. Or make mine higher").queue();
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        if (e.getMessage().getMentionedMembers().isEmpty()) {
                            e.getChannel().sendMessage("Who to mute? Usage: " + prefix + "mute <member>").queue();
                            return;
                        } else {
                            Member membertomute = e.getMessage().getMentionedMembers().get(0);
                            Role mutedrole = e.getGuild().getRoleById(DiscordSRVUtils.Moderationconfig.MutedRole());
                            if (mutedrole == null) {
                                e.getChannel().sendMessage("We could not mute this Member for some reason. If you are the owner please check server console").queue();
                                core.getLogger().severe("Role not found on Guild \"" + e.getGuild().getName() + "\" Role ID: " + DiscordSRVUtils.Moderationconfig.MutedRole());
                                return;
                            } else {
                                if (e.getGuild().getSelfMember().canInteract(mutedrole)) {
                                    if (membertomute.getRoles().contains(mutedrole)) {
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setColor(Color.RED);
                                        embed.setDescription("**_Member is already muted._**");
                                        e.getChannel().sendMessage(embed.build()).queue();
                                    } else {
                                        e.getGuild().addRoleToMember(membertomute, mutedrole).queue();
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setColor(Color.GREEN);
                                        embed.setDescription("**_" + membertomute.getUser().getAsTag() + " Was muted._**");
                                        e.getChannel().sendMessage(embed.build()).queue();
                                    }
                                } else {
                                    e.getChannel().sendMessage("I am unable to give the muted role. Please lower muted role. Or make mine higher").queue();
                                    return;
                                }
                            }
                        }
                    }

                }

            } else {
                e.getChannel().sendMessage("You don't have perms to use this command.").queue();
            }
            return;
        } else if (args[0].equalsIgnoreCase(prefix + "unmute")) {
            if (isModerator(e.getMember())) {
                if (!(args.length >= 2)) {
                    e.getChannel().sendMessage("Who to unmute? Usage: " + prefix + "mute <member>").queue();
                    return;
                } else if (args.length >= 2) {
                    try {
                        Long.parseLong(args[1]);
                        Member membertomute = e.getGuild().getMemberById(Long.parseLong(args[1]));
                        Role mutedrole = e.getGuild().getRoleById(DiscordSRVUtils.Moderationconfig.MutedRole());
                        if (membertomute == null) {
                            e.getChannel().sendMessage("Member not found.").queue();
                            return;
                        }
                        if (mutedrole == null) {
                            e.getChannel().sendMessage("We could not mute this Member for some reason. If you are the owner please check server console").queue();
                            core.getLogger().severe("Role not found on Guild \"" + e.getGuild().getName() + "\" Role ID: " + DiscordSRVUtils.Moderationconfig.MutedRole());
                            return;
                        } else if (e.getGuild().getSelfMember().canInteract(mutedrole)) {
                            if (!membertomute.getRoles().contains(mutedrole)) {
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setColor(Color.RED);
                                embed.setDescription("**_Member is not muted._**");
                                e.getChannel().sendMessage(embed.build()).queue();
                            } else {
                                e.getGuild().removeRoleFromMember(membertomute, mutedrole).queue();
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setColor(Color.GREEN);
                                embed.setDescription("**_" + membertomute.getUser().getAsTag() + " Was unmuted._**");
                                e.getChannel().sendMessage(embed.build()).queue();
                            }
                        } else {
                            e.getChannel().sendMessage("I am unable to remove the muted role. Please lower muted role. Or make mine higher").queue();
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        if (e.getMessage().getMentionedMembers().isEmpty()) {
                            e.getChannel().sendMessage("Who to unmute? Usage: " + prefix + "unmute <member>").queue();
                            return;
                        } else {
                            Member membertomute = e.getMessage().getMentionedMembers().get(0);
                            Role mutedrole = e.getGuild().getRoleById(DiscordSRVUtils.Moderationconfig.MutedRole());
                            if (mutedrole == null) {
                                e.getChannel().sendMessage("We could not unmute this Member for some reason. If you are the owner please check server console").queue();
                                core.getLogger().severe("Role not found on Guild \"" + e.getGuild().getName() + "\" Role ID: " + DiscordSRVUtils.Moderationconfig.MutedRole());
                                return;
                            } else {
                                if (e.getGuild().getSelfMember().canInteract(mutedrole)) {
                                    if (!membertomute.getRoles().contains(mutedrole)) {
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setColor(Color.RED);
                                        embed.setDescription("**_Member is not muted._**");
                                        e.getChannel().sendMessage(embed.build()).queue();
                                    } else {
                                        e.getGuild().removeRoleFromMember(membertomute, mutedrole).queue();
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setColor(Color.GREEN);
                                        embed.setDescription("**_" + membertomute.getUser().getAsTag() + " Was unmuted._**");
                                        e.getChannel().sendMessage(embed.build()).queue();
                                    }
                                } else {
                                    e.getChannel().sendMessage("I am unable to remove the muted role. Please lower muted role. Or make mine higher").queue();
                                    return;
                                }
                            }
                        }
                    }

                }

            } else {
                e.getChannel().sendMessage("You don't have perms to use this command.").queue();
            }
            return;
        } else if (args[0].equalsIgnoreCase(prefix + "suggest")) {
            System.out.println(e.getMember().getIdLong());
            System.out.println(e.getChannel().getIdLong());
            if (DiscordSRVUtils.BotSettingsconfig.isBungee()) return;
            if (!DiscordSRVUtils.SuggestionsConfig.isEnabled()) return;

            try (Connection conn = core.getMemoryConnection()) {
                PreparedStatement p1 = conn.prepareStatement("SELECT * FROM suggestions_Awaiting WHERE userid=? AND channel=?");
                p1.setLong(1, e.getMember().getIdLong());
                p1.setLong(2, e.getChannel().getIdLong());
                ResultSet r1 = p1.executeQuery();
                if (!r1.next()) {
                    PreparedStatement p2 = conn.prepareStatement("INSERT INTO suggestions_Awaiting (userid, channel, LastOutput) VALUES (?, ?, ?)");
                    p2.setLong(1, e.getMember().getIdLong());
                    p2.setLong(2, e.getChannel().getIdLong());
                    p2.setLong(3, System.currentTimeMillis());
                    p2.execute();
                    e.getChannel().sendMessage("Please enter your Suggestion.").queue();
                    return;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else if (args[0].equalsIgnoreCase(prefix + "leaderboard")) {
            Stopwatch stopwatch = new TimerManager().getStopwatch();
            stopwatch.start();
            LeaderBoardManager manager = core.getLeaderBoardManager();
            int count = 0;
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Leaderboard");
            String description = "";
            embed.setColor(Color.ORANGE);
            for (Person currentPerson : manager.getLeaderBoardFromTo(1, 10)) {
                count++;
                if (description.equals("")) {
                    description = "**" + count + ".**" + DiscordSRVUtils.Levelingconfig.leaderboard_format()
                            .replace("[Minecraft_Name]", Bukkit.getOfflinePlayer(currentPerson.getMinecraftUUID()).getName())
                            .replace("[Level]", Integer.toString(currentPerson.getLevel()))
                            .replace("[TotalMessages]", currentPerson.getTotalMessages().toString())
                            .replace("[DiscordMessages]", currentPerson.getDiscordMessages().toString())
                            .replace("[MinecraftMessages]", currentPerson.getMinecraftMessages().toString())
                            .replace("[XP]", Integer.toString(currentPerson.getXP()))

                    ;
                } else {
                    description = description + "\n" + "**" + count + ".**" + DiscordSRVUtils.Levelingconfig.leaderboard_format()
                            .replace("[Minecraft_Name]", Bukkit.getOfflinePlayer(currentPerson.getMinecraftUUID()).getName())
                            .replace("[Level]", Integer.toString(currentPerson.getLevel()))
                            .replace("[TotalMessages]", currentPerson.getTotalMessages().toString())
                            .replace("[DiscordMessages]", currentPerson.getDiscordMessages().toString())
                            .replace("[MinecraftMessages]", currentPerson.getMinecraftMessages().toString())
                            .replace("[XP]", Integer.toString(currentPerson.getXP()))
                    ;
                }
            }
            embed.setDescription(description);
            e.getChannel().sendMessage(embed.build()).queue();
            System.out.println(stopwatch.getElapsedTime() + "ms");
        } else if (args[0].equalsIgnoreCase(prefix + "suggestionreply") || args[0].equalsIgnoreCase(prefix + "sr")) {
            if (!DiscordSRVUtils.SuggestionsConfig.isEnabled()) return;
            if (DiscordSRVUtils.BotSettingsconfig.isBungee()) return;
            if (!isModerator(e.getMember())) {
                e.getChannel().sendMessage("You don't have permission to use this command").queue();
                return;
            }
            if (!(args.length >= 2)) {
                e.getChannel().sendMessage("**Usage: **" + prefix + "sr <Suggestion Message ID>").queue();
            } else {
                try (Connection conn = core.getMemoryConnection(); Connection conn2 = core.getDatabaseFile()) {
                    PreparedStatement pcheck1 = conn.prepareStatement("SELECT * FROM srmsgesreply WHERE userid=? AND Channel=?");
                    pcheck1.setLong(1, e.getMember().getIdLong());
                    pcheck1.setLong(2, e.getChannel().getIdLong());
                    ResultSet rcheck1 = pcheck1.executeQuery();
                    if (rcheck1.next()) return;
                    if (!isNumberic(args[1])) {
                        e.getChannel().sendMessage("Invalid ID, Please try Again").queue();
                        return;
                    }
                    PreparedStatement pcheck2 = conn2.prepareStatement("SELECT * FROM discordsrvutils_suggestions WHERE Message=?");
                    pcheck2.setLong(1, Long.parseLong(args[1]));
                    ResultSet rcheck2 = pcheck2.executeQuery();
                    if (rcheck2.next()) {
                        if (!(rcheck2.getString("isAccepted") == null)) {
                            e.getChannel().sendMessage("This suggestion has been already Accepted/denied").queue();
                            return;
                        }
                    } else {
                        e.getChannel().sendMessage("Suggestion not found.").queue();
                        return;
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.CYAN);
                embed.setDescription("Is this suggestion accepted?\n:regional_indicator_y: YES\n\n :regional_indicator_n: NO");
                embed.setTitle("Reply a suggestion");
                e.getChannel().sendMessage(embed.build()).queue(msg -> {
                    try (Connection conn = core.getMemoryConnection()) {
                        PreparedStatement p1 = conn.prepareStatement("INSERT INTO srmsgesreply (userid, Channel, SuggestionID, step, Awaiting_isAccepted, LastOutput) VALUES (?, ?, ?, 1, ?, ?)");
                        p1.setLong(1, e.getMember().getIdLong());
                        p1.setLong(2, e.getChannel().getIdLong());
                        p1.setLong(3, Long.parseLong(args[1]));
                        p1.setLong(4, msg.getIdLong());
                        p1.setLong(5, System.currentTimeMillis());
                        p1.execute();
                        msg.addReaction("\uD83C\uDDFE").queue();
                        msg.addReaction("\uD83C\uDDF3").queue();

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
            }
            return;
        }

        try (Connection conn = core.getMemoryConnection(); Connection fconn = core.getDatabaseFile()) {
            PreparedStatement pp1 = conn.prepareStatement("SELECT * FROM srmsgesreply WHERE userid=? AND Channel=?");
            pp1.setLong(1, e.getMember().getIdLong());
            pp1.setLong(2, e.getChannel().getIdLong());
            ResultSet rr1 = pp1.executeQuery();
            if (rr1.next()) {
                if (rr1.getInt("step") == 2) {
                    PreparedStatement pp2 = fconn.prepareStatement("UPDATE discordsrvutils_suggestions SET isAccepted=?, staffReply=?, staffReplier=? WHERE Message=?");
                    pp2.setString(1, rr1.getString("isAccepted"));
                    pp2.setString(2, e.getMessage().getContentRaw());
                    pp2.setLong(3, e.getMember().getIdLong());
                    pp2.setLong(4, rr1.getLong("SuggestionID"));
                    pp2.execute();
                    PreparedStatement pp3 = fconn.prepareStatement("SELECT * FROM discordsrvutils_suggestions WHERE Message=?");
                    pp3.setLong(1, rr1.getLong("SuggestionID"));
                    ResultSet rr2 = pp3.executeQuery();
                    rr2.next();
                    String isAccepted = rr1.getString("isAccepted");
                    int SID = rr2.getInt("Number");
                    String msgcontent = rr2.getString("Suggestion");
                    String msg = rr2.getLong("Message") + "";
                    String channel = rr2.getLong("Channel") + "";
                    String url = "https://discord.com/channels/" + e.getGuild().getId() + "/" + rr2.getLong("Channel") + "/" + rr2.getLong("Message");
                    e.getJDA().retrieveUserById(rr2.getLong("Userid")).queue(user -> {
                        if (DiscordSRVUtils.SuggestionsConfig.sendDMToUserWhenSuggestionReplied()) {
                            user.openPrivateChannel().queue(ch -> {
                                EmbedBuilder embed = new EmbedBuilder();
                                if (isAccepted.equals("true")) {
                                    embed.setTitle("Suggestion Accepted");
                                    embed.setColor(Color.GREEN);
                                } else {
                                    embed.setTitle("Suggestion denied");
                                    embed.setColor(Color.RED);
                                }
                                embed.addField("Suggestion", "[Jump!](" + url + ")", true);
                                embed.addField("Replied by", e.getMember().getUser().getAsTag(), true);
                                embed.addField("Reply", e.getMessage().getContentRaw(), false);

                                ch.sendMessage(embed.build()).queue();

                            });
                        }
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setThumbnail(user.getEffectiveAvatarUrl());
                        embed.setDescription("**Suggested by:** " + user.getAsTag() + "\n" +
                                "**Suggestion Number:** #" + SID);
                        embed.addField("Suggestion", msgcontent, false);
                        embed.setColor(Color.YELLOW);
                        embed.addField("Replied by", e.getMember().getUser().getAsTag(), false);
                        if (isAccepted.equals("true")) {
                            embed.addField("Accepted", "Yes", true);
                        } else {
                            embed.addField("Accepted", "No", true);
                        }
                        embed.addField("Reply", e.getMessage().getContentRaw(), false);
                        e.getGuild().getTextChannelById(channel).editMessageById(Long.parseLong(msg), embed.build()).queue();
                        e.getChannel().sendMessage("Successfully replied.").queue();
                        try (Connection conni = core.getMemoryConnection()) {
                            PreparedStatement pppppp1 = conni.prepareStatement("DELETE FROM srmsgesreply WHERE userid=? AND Channel=?");
                            pppppp1.setLong(1, e.getMember().getIdLong());
                            pppppp1.setLong(2, e.getChannel().getIdLong());
                            pppppp1.execute();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    });

                }
            }
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
                            last.execute();
                            return;

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
                                    embed.setDescription("**Step 4:** Please mention the roles that will be allowed to view all tickets.\n\n To cancel this process, reply with `cancel`\n\nReply with `none` for no roles.");
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
                                if (!e.getMessage().getContentRaw().equalsIgnoreCase("none")) {
                                    e.getChannel().sendMessage("No roles mentioned. Please try again").queue();
                                } else {
                                    PreparedStatement p2 = conn.prepareStatement("UPDATE tickets_creating SET step=4 WHERE channel_id=? AND UserID=?");
                                    p2.setLong(1, e.getChannel().getIdLong());
                                    p2.setLong(2, e.getMember().getIdLong());
                                    p2.execute();
                                    EmbedBuilder embed = new EmbedBuilder();
                                    embed.setColor(Color.RED);
                                    embed.setTitle("Create new ticket");
                                    embed.setDescription("**Step 5:** Please mention the channel which we should send the ticket creation message.\n\n To cancel this process, reply with `cancel`");
                                    e.getChannel().sendMessage(embed.build()).queue();

                                }
                            }
                        } else if (r1.getInt("step") == 4) {
                            if (e.getMessage().getMentionedChannels().isEmpty()) {
                                e.getChannel().sendMessage("No Channels mentioned. Please try again").queue();
                                return;
                            } else {
                                int number = 0;
                                for (TextChannel tx : e.getMessage().getMentionedChannels()) {
                                    number = number + 1;
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

                                    try (Connection mconn = core.getMemoryConnection();) {

                                        PreparedStatement mp1 = mconn.prepareStatement("SELECT * FROM tickets_creating WHERE UserID=? AND Channel_id=?");
                                        mp1.setLong(1, e.getMember().getIdLong());
                                        mp1.setLong(2, e.getChannel().getIdLong());
                                        mp1.execute();
                                        ResultSet mr1 = mp1.executeQuery();
                                        mr1.next();
                                        int TicketID = RANDOM.nextInt(9999);
                                        PreparedStatement mp2 = mconn.prepareStatement("SELECT * FROM discordsrvutils_ticket_allowed_roles WHERE Channel_id=? AND UserID=?");
                                        mp2.setLong(1, e.getChannel().getIdLong());
                                        mp2.setLong(2, e.getMember().getIdLong());
                                        mp2.execute();
                                        ResultSet mr2 = mp2.executeQuery();
                                        List<Long> roles = new ArrayList<>();
                                        while (mr2.next()) {
                                            roles.add(mr2.getLong("RoleID"));

                                        }
                                        tickets.createTicket(TicketID, e.getMessage().getMentionedChannels().get(0), mr1.getLong("Opened_Category"), mr1.getLong("Closed_Category"), mr1.getString("Name"), roles);
                                        tickets.deleteMemoryTicketCreation(e.getChannel().getIdLong(), e.getMember().getIdLong());

                                    } catch (SQLException exception) {
                                        exception.printStackTrace();
                                    }

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
                                    ResultSet rgetter = getter.executeQuery();
                                    rgetter.next();
                                    e.getGuild().getTextChannelById(rgetter.getLong("ChannelID")).editMessageById(rgetter.getLong("MessageID"), embed.build()).queue();
                                    PreparedStatement p4 = conn.prepareStatement("DELETE FROM discordsrvutils_Awaiting_Edits WHERE Channel_id=? AND UserID=?");
                                    p4.setLong(1, e.getChannel().getIdLong());
                                    p4.setLong(2, e.getMember().getIdLong());
                                    p4.execute();
                                    e.getChannel().sendMessage("Ticket renamed.").queue();
                                } else if (r2.getInt("Type") == 3) {
                                    try {
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
                                        if (e.getMessage().getContentRaw().equalsIgnoreCase("none")) {
                                            PreparedStatement p3 = fconn.prepareStatement("DELETE FROM discordsrvutils_ticket_allowed_roles WHERE TicketID=?");
                                            p3.setInt(1, r2.getInt("TicketID"));
                                            p3.execute();
                                            PreparedStatement p5 = conn.prepareStatement("DELETE FROM discordsrvutils_Awaiting_Edits WHERE Channel_id=? AND UserID=?");
                                            p5.setLong(1, e.getChannel().getIdLong());
                                            p5.setLong(2, e.getMember().getIdLong());
                                            p5.execute();
                                            e.getChannel().sendMessage("Changed ticket view allowed roles.").queue();
                                        } else {
                                            e.getChannel().sendMessage("No roles mentioned. Please try again").queue();
                                        }
                                    } else {
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
                                        PreparedStatement p3 = fconn.prepareStatement("SELECT * FROM discordsrvutils_ticket_allowed_roles WHERE TicketID=?");
                                        p3.setInt(1, r2.getInt("TicketID"));
                                        p3.execute();
                                        ResultSet r3 = p3.executeQuery();
                                        r3.next();
                                        PreparedStatement something = fconn.prepareStatement("SELECT * FROM discordsrvutils_tickets WHERE TicketID=?");
                                        something.setInt(1, r2.getInt("TicketID"));
                                        something.execute();
                                        ResultSet rsomething = something.executeQuery();
                                        rsomething.next();
                                        e.getGuild().getTextChannelById(rsomething.getLong("ChannelID")).deleteMessageById(rsomething.getLong("MessageID")).queue();
                                        EmbedBuilder embed = new EmbedBuilder();
                                        PreparedStatement p5 = fconn.prepareStatement("SELECT * FROM discordsrvutils_tickets WHERE TicketID=?");
                                        p5.setInt(1, r2.getInt("TicketID"));
                                        p5.execute();
                                        ResultSet r5 = p5.executeQuery();
                                        r5.next();
                                        embed.setTitle(r5.getString("Name"));
                                        embed.setDescription("React with \uD83D\uDCE9 to create a ticket.");
                                        embed.setColor(Color.CYAN);
                                        e.getGuild().getTextChannelById(e.getMessage().getMentionedChannels().get(0).getIdLong()).sendMessage(embed.build()).queue(msg -> {
                                            try (Connection fconn2 = core.getDatabaseFile()) {
                                                Connection conn3 = core.getMemoryConnection();
                                                PreparedStatement mp2 = conn3.prepareStatement("SELECT * FROM discordsrvutils_Awaiting_Edits WHERE Channel_id=? AND UserID=?");
                                                mp2.setLong(1, e.getChannel().getIdLong());
                                                mp2.setLong(2, e.getMember().getIdLong());
                                                mp2.execute();
                                                ResultSet mr2 = mp2.executeQuery();
                                                mr2.next();
                                                ;
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
                        } else {
                            PreparedStatement p3 = conn.prepareStatement("SELECT * FROM suggestions_Awaiting WHERE userid=? AND Channel=?");
                            p3.setLong(1, e.getMember().getIdLong());
                            p3.setLong(2, e.getChannel().getIdLong());
                            ResultSet r3 = p3.executeQuery();
                            if (r3.next()) {
                                TextChannel channel = e.getGuild().getTextChannelById(DiscordSRVUtils.SuggestionsConfig.channel());
                                if (channel == null) {
                                    e.getChannel().sendMessage("You are unable to suggest at the moment. If you are the owner, check console for details.").queue();
                                    core.getLogger().severe("Suggestions channel in the config was not found.");
                                    try (Connection c2 = core.getMemoryConnection()) {
                                        PreparedStatement p6 = c2.prepareStatement("DELETE FROM suggestions_Awaiting WHERE userid=? AND Channel=?");
                                        p6.setLong(1, e.getMember().getIdLong());
                                        p6.setLong(2, e.getChannel().getIdLong());
                                        p6.execute();
                                    } catch (SQLException ex) {
                                        ex.printStackTrace();
                                    }

                                    return;
                                } else {
                                    int ID;
                                    try (Connection cc = core.getDatabaseFile()) {
                                        PreparedStatement p4 = cc.prepareStatement("SELECT * FROM discordsrvutils_suggestions ORDER BY Number DESC");
                                        ResultSet r4 = p4.executeQuery();
                                        if (r4.next()) {
                                            ID = r4.getInt("Number") + 1;
                                        } else {
                                            ID = 1;
                                        }
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setColor(Color.ORANGE);
                                        embed.setThumbnail(e.getMember().getUser().getEffectiveAvatarUrl());
                                        embed.setDescription("**Suggested by:** " + e.getMember().getUser().getAsTag() + "\n" +
                                                "**Suggestion Number:** #" + ID);
                                        embed.addField("Suggestion", e.getMessage().getContentRaw(), false);
                                        channel.sendMessage(embed.build()).queue(msg -> {
                                            try (Connection c1 = core.getDatabaseFile(); Connection c2 = core.getMemoryConnection()) {
                                                String emote_yes = DiscordSRVUtils.SuggestionsConfig.emoji_yes();
                                                String emote_no = DiscordSRVUtils.SuggestionsConfig.emoji_no();
                                                String yesparsed = EmojiParser.parseToUnicode(":" + emote_yes + ":");
                                                if (!yesparsed.equals(":" + emote_yes + ":")) {
                                                    emote_yes = yesparsed;
                                                } else {
                                                    List<Emote> emotes = e.getGuild().getEmotesByName(emote_yes, true);
                                                    if (emotes.isEmpty()) {
                                                        emote_yes = "✅";
                                                    } else {
                                                        Emote emote = emotes.get(0);
                                                        emote_yes = "a:" + emote.getName() + ":" + emote.getId();
                                                    }
                                                }
                                                String noparesed = ":" + emote_no + ":";
                                                if (!noparesed.equals(":" + emote_no + ":")) {
                                                    emote_no = noparesed;
                                                } else {
                                                    List<Emote> emotes = e.getGuild().getEmotesByName(emote_no, true);
                                                    if (emotes.isEmpty()) {
                                                        emote_no = "❌";
                                                    } else {
                                                        Emote emote = emotes.get(0);
                                                        emote_no = "a:" + emote.getName() + ":" + emote.getId();
                                                    }
                                                }
                                                System.out.println(emote_yes);
                                                System.out.println(emote_no);
                                                PreparedStatement p5 = c1.prepareStatement("INSERT INTO discordsrvutils_suggestions (Userid, Channel, Message, Suggestion, Number) VALUES (?, ?, ?, ?, ?)");
                                                p5.setLong(1, e.getMember().getIdLong());
                                                p5.setLong(2, channel.getIdLong());
                                                p5.setLong(3, msg.getIdLong());
                                                p5.setString(4, e.getMessage().getContentRaw());
                                                p5.setInt(5, ID);
                                                p5.execute();
                                                PreparedStatement p6 = c2.prepareStatement("DELETE FROM  suggestions_Awaiting WHERE userid=? AND Channel=?");
                                                p6.setLong(1, e.getMember().getIdLong());
                                                p6.setLong(2, e.getChannel().getIdLong());
                                                p6.execute();
                                                msg.addReaction(emote_yes).queue();
                                                msg.addReaction(emote_no).queue();
                                                e.getChannel().sendMessage("Your Suggestion has been recorded.").queue();
                                            } catch (SQLException ex) {
                                                ex.printStackTrace();
                                            }
                                        });
                                    } catch (SQLException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                        if (!DiscordSRVUtils.BotSettingsconfig.isBungee()) {
                            if (e.getMessage().getMentionedMembers().contains(e.getGuild().getSelfMember())) {
                                e.getChannel().sendMessage("**My prefix is** `" + prefix + "`").queue();
                            }
                        }
                    }
                }
            }

        } catch (SQLException exception) {
            exception.printStackTrace();

        }
        Bukkit.getScheduler().runTask(core, () -> {
            if (!DiscordSRVUtils.BotSettingsconfig.isBungee()) {
                if (DiscordSRVUtils.Levelingconfig.Leveling_Enabled()) {
                    Person person = core.getPersonByDiscordID(e.getMember().getIdLong());
                    if (person.isLinked()) {
                        if (!person.isBukkitCached()) return;
                        person.addMessages(MessageType.Discord, 1);
                        Long val = core.lastchattime.get(person.getMinecraftUUID());
                        if (val == null) {
                            core.lastchattime.put(person.getMinecraftUUID(), System.nanoTime());
                        } else {
                            if (!(System.nanoTime() - val >= EXPIRATION_NANOS)) return;
                            core.lastchattime.remove(person.getMinecraftUUID());
                            core.lastchattime.put(person.getMinecraftUUID(), System.nanoTime());
                        }
                        person.insertLeveling();
                        person.addXP(BukkitEventListener.RANDOM.nextInt(25));
                        if (person.getXP() >= 300) {
                            person.clearXP();
                            DiscordLevelupEvent ev = new DiscordLevelupEvent(e, person);
                            Bukkit.getPluginManager().callEvent(ev);
                            if (!ev.isCancelled()) {
                                person.addLevels(1);
                                if (e.getGuild().getTextChannelById(DiscordSRVUtils.Levelingconfig.levelup_channel()) == null) {
                                    e.getChannel().sendMessage(conf.getConfigWithPapi(person.getMinecraftUUID(), String.join("\n", DiscordSRVUtils.Levelingconfig.levelup_Discord())).replace("[Level]", person.getLevel() + "").replace("[User_Mention]", e.getMember().getAsMention())).queue();
                                } else {
                                    e.getGuild().getTextChannelById(DiscordSRVUtils.Levelingconfig.levelup_channel()).sendMessage(conf.getConfigWithPapi(person.getMinecraftUUID(), String.join("\n", DiscordSRVUtils.Levelingconfig.levelup_Discord())).replace("[Level]", person.getLevel() + "").replace("[User_Mention]", e.getMember().getAsMention())).queue();
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        if (DiscordSRVUtils.BotSettingsconfig.isBungee()) return;
        if (e.getMember().getUser().isBot()) return;
        try (Connection conn = core.getDatabaseFile(); Connection conn2 = core.getDatabaseFile();) {
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
                                try (Connection conn3 = core.getDatabaseFile()) {
                                    PreparedStatement p2 = conn3.prepareStatement("SELECT * FROM discordsrvutils_ticket_allowed_roles WHERE TicketID=?");
                                    PreparedStatement p3 = conn3.prepareStatement("SELECT * FROM discordsrvutils_tickets WHERE MessageId=?");
                                    p3.setLong(1, e.getMessageIdLong());
                                    p3.execute();
                                    ResultSet r2 = p3.executeQuery();
                                    r2.next();

                                    p2.setInt(1, r2.getInt("TicketID"));
                                    p2.execute();
                                    ResultSet r3 = p2.executeQuery();
                                    if (!channel.getPermissionOverrides().contains(channel.getPermissionOverride(e.getGuild().getPublicRole()))) {
                                        channel.createPermissionOverride(e.getGuild().getPublicRole()).setDeny(Permission.VIEW_CHANNEL).queue();
                                    } else {
                                        channel.getPermissionOverride(e.getGuild().getPublicRole()).getManager().setDeny(Permission.VIEW_CHANNEL).queue();

                                    }
                                    while (r3.next()) {
                                        if (!channel.getPermissionOverrides().contains(channel.getPermissionOverride(e.getGuild().getRoleById(r3.getLong("RoleID"))))) {
                                            channel.createPermissionOverride(e.getGuild().getRoleById(r3.getLong("RoleID"))).grant(Permission.VIEW_CHANNEL).queue();
                                        } else {
                                            channel.getPermissionOverride(e.getGuild().getRoleById(r3.getLong("RoleID"))).getManager().setAllow(Permission.VIEW_CHANNEL).queue();
                                        }
                                    }
                                } catch (SQLException exception) {
                                    exception.printStackTrace();
                                }
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setTitle("Ticket");
                                embed.setColor(Color.GREEN);
                                try (Connection conn1 = core.getDatabaseFile()) {

                                    PreparedStatement cp1 = conn1.prepareStatement("SELECT * FROM discordsrvutils_tickets WHERE MessageId=?");
                                    cp1.setLong(1, e.getMessageIdLong());
                                    cp1.execute();
                                    ResultSet cr1 = cp1.executeQuery();
                                    cr1.next();
                                    embed.setDescription("here is your ticket.\nReact with \uD83D\uDD12 to close this ticket. or use `" + DiscordSRVUtils.BotSettingsconfig.BotPrefix() + "close`.\n\n**TicketName:** " + cr1.getString("Name"));
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                                channel.sendMessage(e.getMember().getAsMention() + " Welcome").queue();
                                channel.sendMessage(embed.build()).queue(message2 -> {
                                    message2.addReaction("\uD83D\uDD12").queue();
                                    try (Connection fconn2 = core.getDatabaseFile();) {

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

                        PreparedStatement p2 = conn2.prepareStatement("SELECT * FROM discordsrvutils_Opened_Tickets WHERE MessageID=?");
                        p2.setLong(1, e.getMessageIdLong());
                        p2.execute();
                        ResultSet r2 = p2.executeQuery();
                        if (r2.next()) {


                            PreparedStatement closed = conn2.prepareStatement("SELECT * FROM discordsrvutils_Opened_Tickets WHERE MessageID=?");
                            closed.setLong(1, e.getMessageIdLong());
                            closed.execute();
                            ResultSet closed2 = closed.executeQuery();
                            closed2.next();
                            PreparedStatement tickets = conn2.prepareStatement("SELECT * FROM discordsrvutils_tickets WHERE TicketID=?");
                            tickets.setLong(1, closed2.getInt("TicketID"));
                            tickets.execute();
                            ResultSet ticketss = tickets.executeQuery();
                            ticketss.next();
                            if (e.getReactionEmote().getName().equals("\uD83D\uDD12")) {
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setTitle("Ticket Closed");
                                embed.setDescription("Ticket Closed by " + e.getMember().getAsMention() + "");
                                embed.setColor(Color.YELLOW);
                                e.getChannel().sendMessage(embed.build()).queue(msg -> {
                                    try (Connection conn3 = core.getDatabaseFile()) {
                                        ;
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

                            try (Connection conn5 = core.getDatabaseFile();) {
                                PreparedStatement p3 = conn5.prepareStatement("SELECT * FROM discordsrvutils_Closed_Tickets WHERE Closed_Message=?");
                                p3.setLong(1, e.getMessageIdLong());
                                p3.execute();
                                ResultSet r3 = p3.executeQuery();
                                if (r3.next()) {
                                    if (e.getReactionEmote().getName().equals("\uD83D\uDDD1️")) {
                                        e.getTextChannel().delete().queue();
                                    }
                                } else {
                                    String emote_yes = DiscordSRVUtils.SuggestionsConfig.emoji_yes();
                                    String emote_no = DiscordSRVUtils.SuggestionsConfig.emoji_no();
                                    String remote_yes;
                                    String remote_no;
                                    String yesparsed = EmojiParser.parseToUnicode(":" + emote_yes + ":");
                                    if (!yesparsed.equals(":" + emote_yes + ":")) {
                                        remote_yes = yesparsed;
                                        emote_yes = yesparsed;
                                    } else {
                                        List<Emote> emotes = e.getGuild().getEmotesByName(emote_yes, true);
                                        if (emotes.isEmpty()) {
                                            remote_yes = "✅";
                                            emote_yes = "✅";
                                        } else {
                                            Emote emote = emotes.get(0);
                                            remote_yes = "a:" + emote.getName() + ":" + emote.getId();
                                        }
                                    }
                                    String noparesed = ":" + emote_no + ":";
                                    if (!noparesed.equals(":" + emote_no + ":")) {
                                        remote_no = noparesed;
                                        emote_no = noparesed;
                                    } else {
                                        List<Emote> emotes = e.getGuild().getEmotesByName(emote_no, true);
                                        if (emotes.isEmpty()) {
                                            remote_no = "❌";
                                            emote_no = "❌";
                                        } else {
                                            Emote emote = emotes.get(0);
                                            remote_no = "a:" + emote.getName() + ":" + emote.getId();
                                        }
                                    }
                                    PreparedStatement p4 = conn5.prepareStatement("SELECT * FROM discordsrvutils_suggestions WHERE Userid=? AND Channel=? AND Message=?");
                                    p4.setLong(1, e.getMember().getIdLong());
                                    p4.setLong(2, e.getChannel().getIdLong());
                                    p4.setLong(3, e.getMessageIdLong());
                                    ResultSet r4 = p4.executeQuery();
                                    if (r4.next()) {
                                        e.getReaction().removeReaction(e.getUser()).queue();
                                    } else {
                                        PreparedStatement p5 = conn5.prepareStatement("SELECT * FROM discordsrvutils_suggestions WHERE Channel=? AND Message=?");
                                        p5.setLong(1, e.getChannel().getIdLong());
                                        p5.setLong(2, e.getMessageIdLong());
                                        ResultSet r5 = p5.executeQuery();
                                        if (r5.next()) {
                                            if (e.getReactionEmote().getName().equals(emote_yes)) {
                                                e.getChannel().retrieveMessageById(e.getMessageId()).queue(msg -> {
                                                    msg.removeReaction(remote_no, e.getUser()).queue();
                                                });
                                            } else if (e.getReactionEmote().getName().equals(emote_no)) {
                                                e.getChannel().retrieveMessageById(e.getMessageId()).queue(msg -> {
                                                    msg.removeReaction(remote_yes, e.getUser()).queue();
                                                });

                                            }
                                        } else {
                                            try (Connection conni = core.getMemoryConnection()) {
                                                PreparedStatement p6 = conni.prepareStatement("SELECT * FROM helpmsges WHERE userid=? AND Channel=? AND MessageID=?");
                                                p6.setLong(1, e.getUserIdLong());
                                                p6.setLong(2, e.getChannel().getIdLong());
                                                p6.setLong(3, e.getMessageIdLong());
                                                ResultSet r6 = p6.executeQuery();
                                                if (r6.next()) {
                                                    String prefix = DiscordSRVUtils.BotSettingsconfig.BotPrefix();
                                                    if (e.getReactionEmote().getName().equals("➡️")) {
                                                        Map<Integer, String> map = new HashMap<>();
                                                        int pages = 0;
                                                        pages++;
                                                        map.put(pages, "Tickets");
                                                        if (DiscordSRVUtils.Levelingconfig.Leveling_Enabled()) {
                                                            pages++;
                                                            map.put(pages, "Leveling");
                                                        }
                                                        if (DiscordSRVUtils.Moderationconfig.isModeratorCommandsEnabled()) {
                                                            pages++;
                                                            map.put(pages, "Moderation");
                                                        }
                                                        if (DiscordSRVUtils.SuggestionsConfig.isEnabled()) {
                                                            pages++;
                                                            map.put(pages, "Suggestions");
                                                        }
                                                        e.getReaction().removeReaction(e.getUser()).queue();
                                                        int newpages = r6.getInt("Page") + 1;
                                                        if (pages >= newpages) {
                                                            EmbedBuilder embed = new EmbedBuilder();
                                                            String val = map.get(newpages);
                                                            embed.setTitle("Page " + newpages + " | " + val);
                                                            embed.setColor(Color.GREEN);
                                                            if (val.equalsIgnoreCase("Suggestions")) {
                                                                embed.setDescription("`" + prefix + "suggest`, `" + prefix + "suggestionreply`");
                                                            } else if (val.equalsIgnoreCase("Leveling")) {
                                                                embed.setDescription("`" + prefix + "level`, `" + prefix + "leaderboard`");
                                                            } else if (val.equalsIgnoreCase("Moderation")) {
                                                                embed.setDescription("`" + prefix + "ban`, `" + prefix + "unban`, `" + prefix + "mute`, `" + prefix + "unmute`");
                                                            }
                                                            e.getChannel().editMessageById(e.getMessageId(), embed.build()).queue();
                                                            PreparedStatement p7 = conni.prepareStatement("UPDATE helpmsges SET Page=? WHERE userid=? AND Channel=? AND MessageID=?");
                                                            p7.setLong(1, newpages);
                                                            p7.setLong(2, e.getUserIdLong());
                                                            p7.setLong(3, e.getChannel().getIdLong());
                                                            p7.setLong(4, e.getMessageIdLong());
                                                            p7.execute();
                                                        }


                                                    } else if (e.getReactionEmote().getName().equals("⬅️")) {
                                                        if (e.getReactionEmote().getName().equals("⬅️")) {
                                                            Map<Integer, String> map = new HashMap<>();
                                                            int pages = 0;
                                                            pages++;
                                                            map.put(pages, "Tickets");
                                                            if (DiscordSRVUtils.Levelingconfig.Leveling_Enabled()) {
                                                                pages++;
                                                                map.put(pages, "Leveling");
                                                            }
                                                            if (DiscordSRVUtils.Moderationconfig.isModeratorCommandsEnabled()) {
                                                                pages++;
                                                                map.put(pages, "Moderation");
                                                            }
                                                            if (DiscordSRVUtils.SuggestionsConfig.isEnabled()) {
                                                                pages++;
                                                                map.put(pages, "Suggestions");
                                                            }
                                                            e.getReaction().removeReaction(e.getUser()).queue();
                                                            int newpages = r6.getInt("Page") - 1;
                                                            if (pages != 0) {
                                                                EmbedBuilder embed = new EmbedBuilder();
                                                                String val = map.get(newpages);
                                                                embed.setTitle("Page " + newpages + " | " + val);
                                                                embed.setColor(Color.GREEN);
                                                                if (val.equalsIgnoreCase("Suggestions")) {
                                                                    embed.setDescription("`" + prefix + "suggest`, `" + prefix + "suggestionreply`");
                                                                } else if (val.equalsIgnoreCase("Leveling")) {
                                                                    embed.setDescription("`" + prefix + "level`, `" + prefix + "leaderboard`");
                                                                } else if (val.equalsIgnoreCase("Moderation")) {
                                                                    embed.setDescription("`" + prefix + "ban`, `" + prefix + "unban`, `" + prefix + "mute`, `" + prefix + "unmute`");
                                                                } else if (val.equalsIgnoreCase("Tickets")) {
                                                                    embed.setDescription("`" + prefix + "createticket`, `" + prefix + "ticketlookup`, `" + prefix + "editticket`, `" + prefix + "close`, `" + prefix + "deleteticket`, `" + prefix + "editticket`");
                                                                }
                                                                e.getChannel().editMessageById(e.getMessageId(), embed.build()).queue();
                                                                PreparedStatement p7 = conni.prepareStatement("UPDATE helpmsges SET Page=? WHERE userid=? AND Channel=? AND MessageID=?");
                                                                p7.setLong(1, newpages);
                                                                p7.setLong(2, e.getUserIdLong());
                                                                p7.setLong(3, e.getChannel().getIdLong());
                                                                p7.setLong(4, e.getMessageIdLong());
                                                                p7.execute();
                                                            }


                                                        }
                                                    } else if (e.getReactionEmote().getName().equals("\uD83D\uDDD1️")) {
                                                        e.getReaction().removeReaction(e.getUser()).queue();
                                                        e.getChannel().editMessageById(e.getMessageId(), "Deleted by User").embed(null).override(true).queue(msg -> {
                                                            for (MessageReaction reaction : msg.getReactions()) {
                                                                reaction.removeReaction().queue();
                                                            }
                                                        });
                                                        PreparedStatement p7 = conni.prepareStatement("DELETE FROM helpmsges WHERE MessageID=?");
                                                        p7.setLong(1, e.getMessageIdLong());
                                                        p7.execute();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                            try (Connection conni = core.getMemoryConnection()) {
                                PreparedStatement p3 = conni.prepareStatement("SELECT * FROM srmsgesreply WHERE userid=? AND Channel=? AND Awaiting_isAccepted=?");
                                p3.setLong(1, e.getUserIdLong());
                                p3.setLong(2, e.getChannel().getIdLong());
                                p3.setLong(3, e.getMessageIdLong());
                                ResultSet r3 = p3.executeQuery();
                                if (r3.next()) {
                                    if (r3.getInt("step") == 1) {
                                        e.getReaction().removeReaction(e.getUser()).queue();
                                        if (e.getReactionEmote().getName().equals("\uD83C\uDDF3")) {
                                            PreparedStatement p4 = conni.prepareStatement("UPDATE srmsgesreply SET step=2, isAccepted='false', LastOutput=? WHERE userid=? AND Channel=?");
                                            p4.setLong(1, System.currentTimeMillis());
                                            p4.setLong(2, e.getUserIdLong());
                                            p4.setLong(3, e.getChannel().getIdLong());
                                            p4.execute();
                                            e.getChannel().sendMessage("Suggestion was denied, Please enter your note below").queue();
                                        } else if (e.getReactionEmote().getName().equals("\uD83C\uDDFE")) {
                                            PreparedStatement p4 = conni.prepareStatement("UPDATE srmsgesreply SET step=2, isAccepted='true', LastOutput WHERE userid=? AND Channel=?");
                                            p4.setLong(1, System.currentTimeMillis());
                                            p4.setLong(2, e.getUserIdLong());
                                            p4.setLong(3, e.getChannel().getIdLong());
                                            p4.execute();
                                            e.getChannel().sendMessage("Suggestion was accepted, Please enter your note below").queue();
                                        }
                                    }
                                }
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }

                            try (Connection conn5 = core.getDatabaseFile();) {
                                PreparedStatement p3 = conn5.prepareStatement("SELECT * FROM discordsrvutils_Closed_Tickets WHERE Closed_Message=?");
                                p3.setLong(1, e.getMessageIdLong());
                                p3.execute();
                                ResultSet r3 = p3.executeQuery();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }


                            try (Connection con = core.getMemoryConnection()) {
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
                                        } else if (e.getReactionEmote().getName().equals("2️⃣")) {
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
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent e) {
        try (Connection conn = core.getDatabaseFile(); Connection mconn = core.getMemoryConnection()) {
            PreparedStatement p1 = conn.prepareStatement("DELETE FROM discordsrvutils_Opened_Tickets WHERE Channel_id=?");
            p1.setLong(1, e.getChannel().getIdLong());
            p1.execute();
            PreparedStatement p2 = conn.prepareStatement("DELETE FROM discordsrvutils_Closed_Tickets WHERE Channel_id=?");
            p2.setLong(1, e.getChannel().getIdLong());
            p2.execute();
            PreparedStatement p3 = conn.prepareStatement("DELETE FROM helpmsges WHERE Channel=?");
            p3.setLong(1, e.getChannel().getIdLong());
            p3.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public boolean isModerator(Member member) {
        boolean canuse = false;
        for (Role role : member.getRoles()) {
            if (DiscordSRVUtils.Moderationconfig.rolesAllowedToUseModeratorCommands().contains(role.getId())) {
                canuse = true;
            }
            if (DiscordSRVUtils.Moderationconfig.rolesAllowedToUseModeratorCommands().contains(role.getName())) {
                canuse = true;
            }
        }
        if (!canuse) {
            if (member.hasPermission(Permission.MANAGE_SERVER)) {
                return true;
            }
        } else return true;
        return false;
    }

    public void onMessageDelete(MessageDeleteEvent e) {
        try (Connection conn = core.getMemoryConnection()) {
            PreparedStatement p1 = conn.prepareStatement("DELETE FROM helpmsges WHERE MessageID=?");
            p1.setLong(1, e.getMessageIdLong());
            p1.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


}