package tech.bedev.discordsrvutils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {




    public static String getLatestVersion() {
        try (Scanner scanner = new Scanner(new URL("https://api.spigotmc.org/legacy/update.php?resource=85958").openStream())) {
            String argss = "";
            while (scanner.hasNext()) {
                argss = argss + scanner.next() + " ";
            }
            return argss.replaceAll("\\s+$", "");
        } catch (IOException exception) {
            System.out.println("[DiscordSRVUtils] Could not look for updates.");
        }
        return null;
    }
}
