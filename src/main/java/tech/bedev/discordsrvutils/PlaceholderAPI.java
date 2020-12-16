package tech.bedev.discordsrvutils;

import github.scarsz.discordsrv.DiscordSRV;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tech.bedev.discordsrvutils.Person.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;

public class PlaceholderAPI extends PlaceholderExpansion {
    public DiscordSRVUtils plugin = (DiscordSRVUtils) Bukkit.getPluginManager().getPlugin(getRequiredPlugin());
    public static Timer timer = new Timer();
    public static int TicketsOpened = -1;

    @Override
    public String getIdentifier() {
        return "DiscordSRVUtils";
    }
    @Override
    public String getRequiredPlugin() {
        return "DiscordSRVUtils";
    }
    public boolean canRegister() {
        return (Bukkit.getPluginManager().getPlugin(getRequiredPlugin()) != null);
    }
    @Override
    public boolean persist() {
        return true;
    }
    @Override
    public String getAuthor() {
        return "Blue Tree";
    }
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }




    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        if (!DiscordSRVUtils.isReady) return "...";
        if (identifier.equalsIgnoreCase("tickets_opened")) {
            if (p != null) {
                try (Connection conn = plugin.getDatabaseFile()) {
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_Opened_Tickets");
                    p1.execute();
                    ResultSet r1 = p1.executeQuery();
                    int currentcount = 0;
                    while (r1.next()) {
                        currentcount++;

                    }
                    return currentcount + "";

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return TicketsOpened + "";
            }
        }
        else if (identifier.startsWith("tickets_opened_")) {
            String ticketid = identifier.replace("tickets_opened_", "");
            try  {
                Integer.parseInt(ticketid);
            }catch (NumberFormatException ex) {
                return "Invalid ticket id.";
            }
                try (Connection conn = plugin.getDatabaseFile()) {
                        PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_opened_tickets WHERE TicketID=?");
                        p1.setLong(1, Long.parseLong(ticketid));
                        p1.execute();
                        ResultSet r1 = p1.executeQuery();
                        int currenttickets = 0;
                        while (r1.next()) {
                            currenttickets++;
                        }
                        return currenttickets + "";

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
        } else if (identifier.equalsIgnoreCase("tickets_closed")) {
            if (p != null) {
                try (Connection conn = plugin.getDatabaseFile()) {
                    PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_Closed_Tickets");
                    p1.execute();
                    ResultSet r1 = p1.executeQuery();
                    int currentcount = 0;
                    while (r1.next()) {
                        currentcount++;

                    }
                    return currentcount + "";

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return TicketsOpened + "";
            }
        }
        else if (identifier.startsWith("tickets_closed_")) {
            String ticketid = identifier.replace("tickets_closed_", "");
            try  {
                Integer.parseInt(ticketid);
            }catch (NumberFormatException ex) {
                return "Invalid ticket id.";
            }
            try (Connection conn = plugin.getDatabaseFile()) {
                PreparedStatement p1 = conn.prepareStatement("SELECT * FROM discordsrvutils_Closed_tickets WHERE TicketID=?");
                p1.setLong(1, Long.parseLong(ticketid));
                p1.execute();
                ResultSet r1 = p1.executeQuery();
                int currenttickets = 0;
                while (r1.next()) {
                    currenttickets++;
                }
                return currenttickets + "";

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else if (identifier.equalsIgnoreCase("level")) {
            if (p != null) {
                Person person = plugin.getPersonByUUID(Bukkit.getOfflinePlayer(p.getName()).getUniqueId());
                return person.getLevel() + "";
            } else return "Unknown player";
        }else if (identifier.equalsIgnoreCase("xp")) {
            if (p != null) {
                Person person = plugin.getPersonByUUID(Bukkit.getOfflinePlayer(p.getName()).getUniqueId());
                return person.getXP() + "";
            } else return "Unknown player";
        }
        return "Unknown Placeholder.";
    }
}
