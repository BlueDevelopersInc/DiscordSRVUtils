package tk.bluetree242.discordsrvutils.suggestions;

public class SuggestionNote {

    private final Long staffID;
    private final String NoteText;
    private final int SuggestionNumber;
    private final Long CreationTime;

    public SuggestionNote(Long staffID, String noteText, int suggestionNumber, Long creationTime) {
        this.staffID = staffID;
        NoteText = noteText;
        SuggestionNumber = suggestionNumber;
        CreationTime = creationTime;
    }

    public Long getStaffID() {
        return staffID;
    }

    public String getText() {
        return NoteText;
    }



    public int getSuggestionNumber() {
        return SuggestionNumber;
    }

    public Long getCreationTime() {
        return CreationTime;
    }
}
