package tech.bedev.discordsrvutils.utils;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import me.clip.placeholderapi.PlaceholderAPI;
import tech.bedev.discordsrvutils.DiscordSRVUtils;

public class StringUtils {

    public String translateplaceholdersdiscorduser(Member member, String msg) {
        if (DiscordSRVUtils.PAPI) {
            return PlaceholderAPI.setPlaceholders(null, msg
            .replace("[User_Name]", member.getUser().getName())
            .replace("[User_Avatar_Url]", member.getUser().getEffectiveAvatarUrl())
            .replace("[User_Tag]", member.getUser().getAsTag())
            .replace("[User_ID]", member.getId())
            .replace("[User_OnlineStatus]", member.getOnlineStatus().toString())
            );
        } else {
            return msg
                    .replace("[User_Name]", member.getUser().getName())
                    .replace("[User_Avatar_Url]", member.getUser().getEffectiveAvatarUrl())
                    .replace("[User_Tag]", member.getUser().getAsTag())
                    .replace("[User_ID]", member.getId())
                    .replace("[User_OnlineStatus]", member.getOnlineStatus().toString());

        }
    }
    public String translateplaceholdersdiscorduser(User member, String msg) {
        if (DiscordSRVUtils.PAPI) {
            return PlaceholderAPI.setPlaceholders(null, msg
                    .replace("[User_Name]", member.getName())
                    .replace("[User_Avatar_Url]", member.getEffectiveAvatarUrl())
                    .replace("[User_Tag]", member.getAsTag())
                    .replace("[User_ID]", member.getId())
                    .replace("[User_OnlineStatus]", member.toString())
            );
        } else {
            return msg
                    .replace("[User_Name]", member.getName())
                    .replace("[User_Avatar_Url]", member.getEffectiveAvatarUrl())
                    .replace("[User_Tag]", member.getAsTag())
                    .replace("[User_ID]", member.getId());

        }
    }
}
