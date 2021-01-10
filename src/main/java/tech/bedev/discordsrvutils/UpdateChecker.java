package tech.bedev.discordsrvutils;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker
{
	public static String getLatestVersion()
	{
		try(Scanner scanner = new Scanner(new URL("https://api.spigotmc.org/legacy/update.php?resource=85958").openStream()))
		{
			StringBuilder version = new StringBuilder();
			while(scanner.hasNext())
			{
				version.append(scanner.next()).append(" ");
			}
			return version.toString().replaceAll("\\s+$", "");
		}
		catch(IOException exception)
		{
			System.out.println("[DiscordSRVUtils] Could not look for updates.");
		}
		return null;
	}
}
