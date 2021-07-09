package tk.bluetree242.discordsrvutils.embeds;



import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;

import java.awt.*;

public class Embed {

    public static MessageEmbed error(String msg) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setDescription("❌ **" + msg + "**");
        return embed.build();
    }

    public static MessageEmbed error(String msg, String footer) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setFooter(footer);
        embed.setDescription("❌ **" + msg + "**");
        return embed.build();
    }

    public static MessageEmbed success(String msg) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setDescription("✅ **" + msg + "**");
        return embed.build();
    }
}
