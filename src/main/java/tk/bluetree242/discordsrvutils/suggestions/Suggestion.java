package tk.bluetree242.discordsrvutils.suggestions;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import tk.bluetree242.discordsrvutils.DiscordSRVUtils;
import tk.bluetree242.discordsrvutils.exceptions.UnCheckedSQLException;
import tk.bluetree242.discordsrvutils.messages.MessageManager;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObject;
import tk.bluetree242.discordsrvutils.placeholder.PlaceholdObjectList;
import tk.bluetree242.discordsrvutils.utils.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class Suggestion {

    protected DiscordSRVUtils core = DiscordSRVUtils.get();
    protected final String text;
    protected final int number;
    protected final Long submitter;
    protected final Long ChannelID;
    protected final Long creationTime;
    protected final Set<SuggestionNote> notes;
    protected final Long MessageID;
    protected final Boolean Approved;

    public Suggestion(String text, int number, Long submitter, Long channelID, Long creationTime, Set<SuggestionNote> notes, Long MessageID, Boolean Approved) {
        this.text = text;
        this.number = number;
        this.submitter = submitter;
        ChannelID = channelID;
        this.creationTime = creationTime;
        this.notes = notes;
        this.MessageID = MessageID;
        this.Approved = Approved;

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


    //TODO:Approve and Decline

    public CompletableFuture<SuggestionNote> addNote(Long staff, String note) {
        return core.completableFuture(() -> {
            try (Connection conn = core.getDatabase()) {
                PreparedStatement p1 = conn.prepareStatement("INSERT INTO suggestion_notes(staffid, notetext, suggestionnumber, creationtime) VALUES (?,?,?,?)");
                p1.setLong(1, staff);
                p1.setString(2, Utils.b64Encode(note));
                p1.setInt(3, number);
                p1.setLong(4, System.currentTimeMillis());
                p1.execute();
                SuggestionNote suggestionNote = new SuggestionNote(staff, note, number, System.currentTimeMillis());
                Message msg = core.getJDA().getTextChannelById(ChannelID).retrieveMessageById(MessageID).complete();
                User submitterUser = core.getJDA().retrieveUserById(submitter).complete();
                msg.editMessage(MessageManager.get().getMessage(core.getSuggestionsConfig().suggestion_noted_message(),
                        PlaceholdObjectList.ofArray(new PlaceholdObject(this, "suggestion"), new PlaceholdObject(submitterUser, "submitter"), new PlaceholdObject(suggestionNote, "note"), new PlaceholdObject(core.getJDA().retrieveUserById(staff).complete(), "staff"))
                        ,null).build()).queue();
                return suggestionNote;
            } catch (SQLException ex) {
                throw new UnCheckedSQLException(ex);
            }
        });
    }
}
