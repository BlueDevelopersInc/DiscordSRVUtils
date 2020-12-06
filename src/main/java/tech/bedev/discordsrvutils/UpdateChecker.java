package tech.bedev.discordsrvutils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {

    private DiscordSRVUtils plugin;

    public UpdateChecker(DiscordSRVUtils plugin) {
        this.plugin = plugin;
    }


    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (Scanner scanner = new Scanner(new URL("https://api.spigotmc.org/legacy/update.php?resource=85958").openStream())) {
                String argss = "";
                    while (scanner.hasNext()) {
                        argss = argss + scanner.next() + " ";
                    }
                consumer.accept(argss.replaceAll("\\s+$", ""));
            } catch (IOException exception) {
                this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }
}
