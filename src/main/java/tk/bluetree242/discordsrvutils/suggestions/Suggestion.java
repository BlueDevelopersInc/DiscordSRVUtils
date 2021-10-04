package tk.bluetree242.discordsrvutils.suggestions;

import tk.bluetree242.discordsrvutils.DiscordSRVUtils;

import java.util.Set;

public class Suggestion {

    protected DiscordSRVUtils core = DiscordSRVUtils.get();
    protected final String text;
    protected final int number;
    protected final Long submitter;
    protected final Long ChannelID;
    protected final Long creationTime;
    protected final Set<SuggestionNote> notes;
    protected final Long MessageID;

    public Suggestion(String text, int number, Long submitter, Long channelID, Long creationTime, Set<SuggestionNote> notes, Long MessageID) {
        this.text = text;
        this.number = number;
        this.submitter = submitter;
        ChannelID = channelID;
        this.creationTime = creationTime;
        this.notes = notes;
        this.MessageID = MessageID;

    }





    public Long getMessageID() {
        return MessageID;
    }

    public DiscordSRVUtils getCore() {
        return core;
    }

    public String getText() {
        return text;
    }

    public Long getCreationTime() {
        return creationTime;
    }
    public Set<SuggestionNote> getNotes() {
        return notes;
    }
    public int getNumber() {
        return number;
    }

    public Long getSubmitter() {
        return submitter;
    }

    public Long getChannelID() {
        return ChannelID;
    }
}
