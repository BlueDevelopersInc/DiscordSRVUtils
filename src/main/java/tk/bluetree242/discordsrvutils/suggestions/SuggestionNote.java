package tk.bluetree242.discordsrvutils.suggestions;

import java.util.Set;

public class SuggestionNote {

    private final Long Submitter;
    private final String NoteText;
    private final int SuggestionNumber;
    private final Long CreationTime;

    public SuggestionNote(Long submitter, String noteText, int suggestionNumber, Long creationTime) {
        Submitter = submitter;
        NoteText = noteText;
        SuggestionNumber = suggestionNumber;
        CreationTime = creationTime;
    }

    public Long getSubmitter() {
        return Submitter;
    }

    public String getNoteText() {
        return NoteText;
    }



    public int getSuggestionNumber() {
        return SuggestionNumber;
    }

    public Long getCreationTime() {
        return CreationTime;
    }
}
